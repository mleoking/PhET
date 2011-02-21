/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.modules.isotopemixture.model;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // Size of the buckets that will hold the isotopes.
    private static final Dimension2D BUCKET_SIZE = new PDimension( 80, 40 ); // In picometers.

    // List of colors which will be used to represent the various isotopes.
    private static final Color [] ISOTOPE_COLORS = new Color [] { new Color( 180, 82, 205), Color.green,
        new Color(255, 69, 0), new Color( 139, 90, 43 ) };
    private static final Map< ImmutableAtom, Color > ISOTOPE_COLOR_MAP = new HashMap< ImmutableAtom, Color>();

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
    private final Property<List<ImmutableAtom>> possibleIsotopesProperty =
            new Property<List<ImmutableAtom>>( new ArrayList<ImmutableAtom>() ){{
                // Add an observer that will update the bucket list when the
                // list of isotopes changes.
                addObserver( new SimpleObserver() {
                    public void update() {
                        // Update the list of buckets that hold the isotopes.
                        ArrayList< Bucket > oldBuckets = new ArrayList< Bucket >( bucketListProperty.getValue() );

                        // Create a new list of buckets based on the new list
                        // of stable isotopes.
                        double bucketYOffset = ISOTOPE_TEST_CHAMBER_RECT.getMinY() - 40;
                        double interBucketDistanceX = ISOTOPE_TEST_CHAMBER_RECT.getWidth() / getValue().size();
                        double bucketXOffset = ISOTOPE_TEST_CHAMBER_RECT.getMinX() + interBucketDistanceX / 2;
                        ArrayList<Bucket> newBucketList = new ArrayList<Bucket>();
                        for ( int i = 0; i < getValue().size(); i++ ){
                            ImmutableAtom atom = getValue().get( i );
                            newBucketList.add( new Bucket(new Point2D.Double(
                                    bucketXOffset + interBucketDistanceX * i, bucketYOffset),
                                    BUCKET_SIZE, ISOTOPE_COLOR_MAP.get( atom ), AtomIdentifier.getName( atom )) );
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
    private final Property<List<Bucket>> bucketListProperty =
            new Property<List<Bucket>>( new ArrayList<Bucket>() );

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

            // Get the new isotope list.
            ArrayList<ImmutableAtom> newIsotopeList = AtomIdentifier.getAllIsotopes( atom.getNumProtons() );

            // Sort from lightest to heaviest.
            Collections.sort( newIsotopeList, new Comparator<IAtom>(){
                public int compare( IAtom atom2, IAtom atom1 ) {
                    return new Double(atom2.getAtomicMass()).compareTo( atom1.getAtomicMass() );
                }
            });

            // Update the structure that maps isotope to colors.  This must
            // be done before update the isotope list so that anything that
            // listens for changes to the list can get the correct colors.
            ISOTOPE_COLOR_MAP.clear();
            for ( ImmutableAtom isotope : newIsotopeList ) {
                ISOTOPE_COLOR_MAP.put( isotope, ISOTOPE_COLORS[ISOTOPE_COLOR_MAP.size()] );
            }

            // Update the list of possible isotopes for this atomic configuration.
            possibleIsotopesProperty.setValue( newIsotopeList );

            // Update the map of isotopes to color.
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

    public Property<List<ImmutableAtom>> getPossibleIsotopesProperty() {
        return possibleIsotopesProperty;
    }

    public Property<List<Bucket>> getBucketListProperty() {
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
