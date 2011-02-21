/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.modules.isotopemixture.model;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.buildanatom.model.AtomIdentifier;
import edu.colorado.phet.buildanatom.model.Bucket;
import edu.colorado.phet.buildanatom.model.BuildAnAtomClock;
import edu.colorado.phet.buildanatom.model.IAtom;
import edu.colorado.phet.buildanatom.model.IConfigurableAtomModel;
import edu.colorado.phet.buildanatom.model.IDynamicAtom;
import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.buildanatom.modules.game.model.SimpleAtom;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model portion of Isotopes Mixture module.  This model contains a mixture
 * of isotopes and allows a user to move various different isotopes in and
 * out of the "Isotope Test Chamber", and simply keeps track of the average
 * mass within the chamber.
 *
 * @author John Blanco
 */
public class IsotopeMixturesModel implements Resettable, IConfigurableAtomModel {

    // -----------------------------------------------------------------------
    // Class Data
    // -----------------------------------------------------------------------

    // Size of the "test chamber", which is the area in model space into which
    // the isotopes can be dragged in order to contribute to the current
    // average atomic weight.
    private static final Dimension2D ISOTOPE_TEST_CHAMBER_SIZE = new PDimension( 350, 300 ); // In picometers.

    // Rectangle that defines the location of the test chamber.  This is
    // set up so that the center of the test chamber is at (0, 0) in model
    // space.
    private static final Rectangle2D ISOTOPE_TEST_CHAMBER_RECT =
        new Rectangle2D.Double(
                -ISOTOPE_TEST_CHAMBER_SIZE.getWidth() / 2,
                -ISOTOPE_TEST_CHAMBER_SIZE.getHeight() / 2,
                ISOTOPE_TEST_CHAMBER_SIZE.getWidth(),
                ISOTOPE_TEST_CHAMBER_SIZE.getHeight() );

    private static final Dimension2D BUCKET_SIZE = new PDimension( 50, 30 ); // In picometers.

    // -----------------------------------------------------------------------
    // Instance Data
    // -----------------------------------------------------------------------

    private final BuildAnAtomClock clock;

    // This atom is the "prototype isotope", meaning that it is set in order
    // to set the atomic weight of the family of isotopes that are currently
    // in use.
    private final SimpleAtom prototypeIsotope = new SimpleAtom();

    // This property contains the list of isotopes that exist in nature as
    // variations of the current "prototype isotope".  In other words, this
    // contains a list of all stable isotopes that match the atomic weight
    // of the currently configured isotope.  There should be only one of each
    // possible isotope.
    private final Property<ArrayList<ImmutableAtom>> possibleIsotopesProperty =
            new Property<ArrayList<ImmutableAtom>>( new ArrayList<ImmutableAtom>() ){{
                // Wire up the bucket list to be updated whenever the list of
                // possible isotopes is updated.
                addObserver( new SimpleObserver() {
                    public void update() {
                        // Make a copy of the current bucket list so that we can send a
                        // notification of their demise.
                        ArrayList< Bucket > oldBuckets = new ArrayList< Bucket >( bucketListProperty.getValue() );

                        // Create a new list of buckets based on the new list
                        // of stable isotopes.
                        double bucketYOffset = ISOTOPE_TEST_CHAMBER_RECT.getMinY() - 20;
                        double interBucketDistanceX = ISOTOPE_TEST_CHAMBER_RECT.getWidth() / (getValue().size() + 1);
                        double bucketXOffset = ISOTOPE_TEST_CHAMBER_RECT.getMinX();
                        ArrayList<Bucket> newBucketList = new ArrayList<Bucket>();
                        for ( int i = 0; i < getValue().size(); i++ ){
                            newBucketList.add( new Bucket(new Point2D.Double(
                                    bucketXOffset + interBucketDistanceX * (i + 1), bucketYOffset),
                                    BUCKET_SIZE, Color.BLUE, AtomIdentifier.getName( getValue().get( i ) )) );
                        }
                        bucketListProperty.setValue( newBucketList );

                        // Send notifications of removal for the old buckets.
                        for ( Bucket bucket : oldBuckets ) {
                            bucket.getPartOfModelProperty().setValue( false );
                        }
                    }
                }, false);
            }};

    // This property contains the list of the buckets where isotopes that are
    // not in the test chamber reside.  This needs to change whenever a new
    // element is selected, since there is one bucket for each stable isotope
    // configuration for a given element.
    private final Property<ArrayList<Bucket>> bucketListProperty =
            new Property<ArrayList<Bucket>>( new ArrayList<Bucket>() );

    // -----------------------------------------------------------------------
    // Constructor(s)
    // -----------------------------------------------------------------------

    public IsotopeMixturesModel( BuildAnAtomClock clock ) {
        this.clock = clock;
    }

    // -----------------------------------------------------------------------
    // Methods
    // -----------------------------------------------------------------------

    public BuildAnAtomClock getClock() {
        return clock;
    }

    public IDynamicAtom getAtom(){
        return prototypeIsotope;
    }

    /**
     * Set the element that is currently in use, and for which all stable
     * isotopes will be available for movement in and out of the test chamber.
     * In case you're wondering why this is done as an atom instead of just
     * setting the atomic weight, it is so that this will play well with the
     * existing controllers (such as the PeriodicTableControlNode) that
     * already existed at the time this class was created.
     */
    public void setAtomConfiguration( IAtom atom ) {

        if ( prototypeIsotope.getNumProtons() != atom.getNumProtons() ||
             prototypeIsotope.getNumNeutrons() != atom.getNumNeutrons() ||
             prototypeIsotope.getNumElectrons() != atom.getNumElectrons()){

            // Update the prototype atom (a.k.a. isotope) configuration.
            prototypeIsotope.setNumProtons( atom.getNumProtons() );
            prototypeIsotope.setNumElectrons( atom.getNumElectrons() );
            prototypeIsotope.setNumNeutrons( atom.getNumNeutrons() );

            // Update the list of possible isotopes for this atomic configuration.
            possibleIsotopesProperty.setValue( AtomIdentifier.getAllIsotopes( atom.getNumProtons() ) );
        }
    }

    /**
     * Get the size of the test chamber.  Note that this is just the size, and
     * that the position is obtained separately.
     *
     * @return
     */
    public Dimension2D getIsotopeTestChamberSize() {
        return ISOTOPE_TEST_CHAMBER_SIZE;
    }

    /**
     * Get the position of the test chamber rectangle.  The position is
     * defined such that the center of the chamber corresponds to (0,0) in
     * model space.
     *
     * @return - The position of the upper left corner, in model space, of the
     * isotope test chamber.
     */
    public Point2D getIsotopeTestChamberPosition() {
        return new Point2D.Double( ISOTOPE_TEST_CHAMBER_RECT.getX(), ISOTOPE_TEST_CHAMBER_RECT.getY() );
    }

    public Property<ArrayList<ImmutableAtom>> getPossibleIsotopesProperty() {
        return possibleIsotopesProperty;
    }

    public Property<ArrayList<Bucket>> getBucketListProperty() {
        return bucketListProperty;
    }

    public void reset() {
        // Set the default element to be hydrogen.
        setAtomConfiguration( new ImmutableAtom( 1, 0, 1 ) );
    }

    // -----------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------
}
