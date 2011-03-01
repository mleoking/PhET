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
import edu.colorado.phet.buildanatom.model.MonoIsotopeParticleBucket;
import edu.colorado.phet.buildanatom.model.SphericalParticle;
import edu.colorado.phet.buildanatom.modules.game.model.SimpleAtom;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.model.Resettable;
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

    // Size of the buckets that will hold the isotopes.
    private static final Dimension2D BUCKET_SIZE = new PDimension( 1000, 400 ); // In picometers.

    // Within this model, the isotopes come in two sizes, small and large, and
    // atoms are either one size or another, and all atoms that are shown at
    // a given time are all the same size.  The larger size is based somewhat
    // on reality.  The smaller size is used when we want to show a lot of
    // atoms at once.
    private static final double LARGE_ISOTOPE_RADIUS = 83; // in picometers.
    private static final double SMALL_ISOTOPE_RADIUS = 30; // in picometers.

    // Numbers of isotopes that are placed into the buckets when a new atomic
    // number is selected.
    private static final int NUM_LARGE_ISOTOPES_PER_BUCKET = 9;
    private static final int NUM_SMALL_ISOTOPES_PER_BUCKET = 90;

    // List of colors which will be used to represent the various isotopes.
    private static final Color [] ISOTOPE_COLORS = new Color [] { new Color( 180, 82, 205), Color.green,
        new Color(255, 69, 0), new Color( 139, 90, 43 ) };

    // Enum of atom size settings.
    public enum AtomSize { SMALL, LARGE };

    // -----------------------------------------------------------------------
    // Instance Data
    // -----------------------------------------------------------------------

    private final BuildAnAtomClock clock;

    // The test chamber into and out of which the isotopes can be moved.
    private final IsotopeTestChamber testChamber = new IsotopeTestChamber();

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
            new Property<List<ImmutableAtom>>( new ArrayList<ImmutableAtom>() );

    // This property contains the list of the buckets where isotopes that are
    // not in the test chamber reside.  This needs to change whenever a new
    // element is selected, since there is one bucket for each stable isotope
    // configuration for a given element.
    private final Property<List<MonoIsotopeParticleBucket>> bucketListProperty =
            new Property<List<MonoIsotopeParticleBucket>>( new ArrayList<MonoIsotopeParticleBucket>() );

    // Property that contains the size setting for the atoms.  This only
    // applies to the user's mix, whereas nature's mix always uses small
    // atoms.
    private final Property<AtomSize> atomSizeProperty = new Property<AtomSize>( AtomSize.LARGE );

    // List that contains the isotope instances that the user can move
    // between the buckets and the test chamber.
    private final List<MovableAtom> interactiveIsotopes = new ArrayList<MovableAtom>();

    // Matches isotopes to colors used to portray them as well as the buckets
    // in which they can reside, etc.
    private final Map< ImmutableAtom, Color > isotopeColorMap = new HashMap< ImmutableAtom, Color>();

    // Listener support.
    private final List<Listener> listeners = new ArrayList<Listener>();

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

        // Only make a change if the specified atom is different.
        if ( prototypeIsotope.getNumProtons() != atom.getNumProtons() ||
             prototypeIsotope.getNumNeutrons() != atom.getNumNeutrons() ||
             prototypeIsotope.getNumElectrons() != atom.getNumElectrons()){

            // Clear the test chamber.
            testChamber.removeAllIsotopes();

            // Remove all existing interactive isotope instances.
            List<MovableAtom> interactiveIsotopesCopy = new ArrayList<MovableAtom>( interactiveIsotopes );
            interactiveIsotopes.clear();
            for ( MovableAtom isotope : interactiveIsotopesCopy ){
                // Signal the isotope that it has been removed from the model.
                isotope.removeListener( isotopeGrabbedListener );
                isotope.removeFromModel();
            }

            // Update the prototype atom (a.k.a. isotope) configuration.
            prototypeIsotope.setNumProtons( atom.getNumProtons() );
            prototypeIsotope.setNumElectrons( atom.getNumElectrons() );
            prototypeIsotope.setNumNeutrons( atom.getNumNeutrons() );

            // Get a list of all stable isotopes for the current atomic number.
            ArrayList<ImmutableAtom> newIsotopeList = AtomIdentifier.getAllIsotopes( atom.getNumProtons() );

            // Sort from lightest to heaviest.
            Collections.sort( newIsotopeList, new Comparator<IAtom>(){
                public int compare( IAtom atom2, IAtom atom1 ) {
                    return new Double(atom2.getAtomicMass()).compareTo( atom1.getAtomicMass() );
                }
            });

            // Update the structure that maps isotope to colors.
            isotopeColorMap.clear();
            for ( ImmutableAtom isotope : newIsotopeList ) {
                isotopeColorMap.put( isotope, ISOTOPE_COLORS[isotopeColorMap.size()] );
            }

            // Update the list of possible isotopes for this atomic configuration.
            possibleIsotopesProperty.setValue( newIsotopeList );

            // Make a copy of the soon-to-be removed buckets - we'll need them
            // later for removal notifications.
            ArrayList< Bucket > oldBuckets = new ArrayList< Bucket >( bucketListProperty.getValue() );

            // Create a new list of buckets based on the new list of stable
            // isotopes.
            double bucketYOffset = testChamber.getTestChamberRect().getMinY() - 400;
            double interBucketDistanceX = (testChamber.getTestChamberRect().getWidth() * 1.2) / newIsotopeList.size();
            double bucketXOffset = testChamber.getTestChamberRect().getMinX() + interBucketDistanceX / 2;
            ArrayList<MonoIsotopeParticleBucket> newBucketList = new ArrayList<MonoIsotopeParticleBucket>();
            for ( int i = 0; i < newIsotopeList.size(); i++ ) {
                ImmutableAtom isotope = newIsotopeList.get( i );
                String bucketCaption = AtomIdentifier.getName( isotope ) + "-" + isotope.getMassNumber();
                newBucketList.add( new MonoIsotopeParticleBucket( new Point2D.Double(
                        bucketXOffset + interBucketDistanceX * i, bucketYOffset ),
                        BUCKET_SIZE, isotopeColorMap.get( isotope ), bucketCaption, LARGE_ISOTOPE_RADIUS,
                        isotope.getNumProtons(), isotope.getNumNeutrons() ) );
            }
            bucketListProperty.setValue( newBucketList );

            // Send notifications of removal for the old buckets, since they
            // were effectively removed by setting a new list of buckets.
            for ( Bucket bucket : oldBuckets ) {
                bucket.getPartOfModelProperty().setValue( false );
            }

            // Add the instances of each isotope to the appropriate bucket.
            for ( ImmutableAtom isotope : newIsotopeList ) {
                MonoIsotopeParticleBucket isotopeBucket = getBucketForIsotope( isotope );
                assert isotopeBucket != null; // If there is no bucket for this isotope, there is a bug.

                // Create each isotope instance and add to appropriate bucket.
                for ( int i = 0; i < NUM_LARGE_ISOTOPES_PER_BUCKET; i++){
                    MovableAtom movableIsotope = new MovableAtom( isotope.getNumProtons(), isotope.getNumNeutrons(),
                            LARGE_ISOTOPE_RADIUS, new Point2D.Double(0, 0), clock );
                    movableIsotope.addListener( isotopeGrabbedListener );
                    isotopeBucket.addIsotopeInstance( movableIsotope );
                    interactiveIsotopes.add( movableIsotope );
                    notifyIsotopeInstanceAdded( movableIsotope );
                }
            }
        }
    }

    /**
     * Get the bucket where the given isotope can be placed.
     *
     * @param isotope
     * @return A bucket that can hold the isotope if one exists, null if not.
     */
    private MonoIsotopeParticleBucket getBucketForIsotope( ImmutableAtom isotope ) {
        MonoIsotopeParticleBucket isotopeBucket = null;
        for ( MonoIsotopeParticleBucket bucket : bucketListProperty.getValue() ){
            if (bucket.isIsotopeAllowed( isotope )){
                // Found it.
                isotopeBucket = bucket;
                break;
            }
        }
        return isotopeBucket;
    }

    /**
     * Get a reference to the test chamber model.
     */
    public IsotopeTestChamber getIsotopeTestChamber(){
        return testChamber;
    }

    public Property<List<ImmutableAtom>> getPossibleIsotopesProperty() {
        return possibleIsotopesProperty;
    }

    public Property<List<MonoIsotopeParticleBucket>> getBucketListProperty() {
        return bucketListProperty;
    }

    public Property<AtomSize> getAtomSizeProperty(){
        return atomSizeProperty;
    }

    public Color getColorForIsotope( ImmutableAtom isotope ) {
        return isotopeColorMap.containsKey( isotope ) ? isotopeColorMap.get( isotope ) : Color.BLACK;
    }

    public void reset() {
        // Set the default element to be hydrogen.
        setAtomConfiguration( new ImmutableAtom( 1, 0, 1 ) );
    }

    public void addListener( Listener listener ){
        listeners.add( listener );
    }

    public void removeListener( Listener listener ){
        listeners.remove( listener );
    }

    private void notifyIsotopeInstanceAdded( MovableAtom atom ){
        for ( Listener listener : listeners ) {
            listener.isotopeInstanceAdded( atom );
        }
    }

    // ------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

    protected final SphericalParticle.Adapter isotopeGrabbedListener = new SphericalParticle.Adapter() {
        @Override
        public void grabbedByUser( SphericalParticle particle ) {
            assert particle instanceof MovableAtom;
            MovableAtom isotope = (MovableAtom)particle;
            if ( testChamber.isIsotopeContained( isotope ) ){
                // The particle is considered removed from the test chamber as
                // soon as it is grabbed.
                testChamber.removeIsotope( isotope );
            }
            isotope.addListener( isotopeDroppedListener );
        }
    };

    protected final SphericalParticle.Adapter isotopeDroppedListener = new SphericalParticle.Adapter() {
        @Override
        public void droppedByUser( SphericalParticle particle ) {
            assert particle instanceof MovableAtom;
            MovableAtom isotope = (MovableAtom)particle;
            if ( testChamber.isIsotopePositionedOverChamber( isotope ) ){
                // Dropped inside the test chamber, so add it to the chamber.
                testChamber.addIsotope( isotope );
            }
            else{
                // Particle was dropped outside of the test chamber, so return
                // it to the bucket.
                MonoIsotopeParticleBucket bucket = getBucketForIsotope( isotope.getAtomConfiguration() );
                assert bucket != null; // Should never have an isotope without a home.
                bucket.addIsotopeInstance( isotope );
            }
            particle.removeListener( isotopeDroppedListener );
        }
    };

    public interface Listener {
        void isotopeInstanceAdded( MovableAtom atom );
    }

    public class Adapter implements Listener {
        public void isotopeInstanceAdded( MovableAtom atom ) {}
    }

    //------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

    /**
     * Class that represents a "test chamber" where multiple isotopes can be
     * placed.  The test chamber calculates the average atomic mass and the
     * proportions of the various isotopes.
     */
    public static class IsotopeTestChamber {

        // ------------------------------------------------------------------------
        // Class Data
        // ------------------------------------------------------------------------

        // Size of the "test chamber", which is the area in model space into which
        // the isotopes can be dragged in order to contribute to the current
        // average atomic weight.
        private static final Dimension2D SIZE = new PDimension( 3500, 3000 ); // In picometers.

        // Rectangle that defines the location of the test chamber.  This is
        // set up so that the center of the test chamber is at (0, 0) in model
        // space.
        private static final Rectangle2D TEST_CHAMBER_RECT =
            new Rectangle2D.Double(
                    -SIZE.getWidth() / 2,
                    -SIZE.getHeight() / 2,
                    SIZE.getWidth(),
                    SIZE.getHeight() );

        // ------------------------------------------------------------------------
        // Instance Data
        // ------------------------------------------------------------------------

        // List of isotopes that are inside the chamber.  This is updated as
        // isotopes come and go.
        private final List<MovableAtom> containedIsotopes = new ArrayList<MovableAtom>();

        // Property that tracks the number of isotopes in the chamber.  This
        // can be monitored in order to update the view.
        private final Property<Integer> isotopeCountProperty = new Property<Integer>( 0 );

        // Property that tracks the average atomic mass of all isotopes in
        // the chamber.
        private final Property<Double> averageAtomicMassProperty = new Property<Double>( 0.0 );

        // ------------------------------------------------------------------------
        // Constructor(s)
        // ------------------------------------------------------------------------

        // ------------------------------------------------------------------------
        // Methods
        // ------------------------------------------------------------------------

        public Dimension2D getTestChamberSize() {
            return SIZE;
        }

        public Rectangle2D getTestChamberRect() {
            return TEST_CHAMBER_RECT;
        }

        /**
         * Get the position of the chamber.  The position is defined as the
         * center of the chamber, not as the upper left corner or any other
         * point.
         *
         * @return
         */
        public Point2D getPosition() {
            return new Point2D.Double( TEST_CHAMBER_RECT.getCenterX(), TEST_CHAMBER_RECT.getCenterY() );
        }

        /**
         * Test whether an isotope is within the chamber.  This is strictly
         * a 2D test that looks as the isotopes center position and determines
         * if it is within the bounds of the chamber rectangle.
         * @param isotop
         * @return
         */
        public boolean isIsotopePositionedOverChamber( MovableAtom isotope ){
            return TEST_CHAMBER_RECT.contains( isotope.getPosition() );
        }

        /**
         * Returns true if the specified isotope instance was previously added
         * to the chamber.
         *
         * @param isotope
         * @return
         */
        public boolean isIsotopeContained( MovableAtom isotope ){
            return containedIsotopes.contains( isotope );
        }

        /**
         * Add the specified isotope to the chamber.  This method requires
         * the the position of the isotope be within the chamber rectangle,
         * or the isotope will not be added.
         *
         * In cases where an isotope is in a position where the center is
         * within the chamber but the edges are not, the isotope will be moved
         * so that it is fully contained within the chamber.
         *
         * @param isotope
         */
        public void addIsotope( MovableAtom isotope ){
            if ( isIsotopePositionedOverChamber( isotope ) ){
                containedIsotopes.add( isotope );
                updateCountProperty();
                // Update the average atomic mass.
                averageAtomicMassProperty.setValue( ( averageAtomicMassProperty.getValue() *
                        ( isotopeCountProperty.getValue() - 1 ) + isotope.getAtomConfiguration().getAtomicMass() ) /
                        isotopeCountProperty.getValue() );
                // If the edges of the isotope are outside of the container,
                // move it to be fully inside.
                double protrusion = isotope.getPosition().getX() + isotope.getRadius() - TEST_CHAMBER_RECT.getMaxX();
                if (protrusion >= 0){
                    isotope.setPositionAndDestination( isotope.getPosition().getX() - protrusion,
                            isotope.getPosition().getY() );
                }
                else{
                    protrusion =  TEST_CHAMBER_RECT.getMinX() - (isotope.getPosition().getX() - isotope.getRadius());
                    if (protrusion >= 0){
                        isotope.setPositionAndDestination( isotope.getPosition().getX() + protrusion,
                                isotope.getPosition().getY() );
                    }
                }
                protrusion = isotope.getPosition().getY() + isotope.getRadius() - TEST_CHAMBER_RECT.getMaxY();
                if (protrusion >= 0){
                    isotope.setPositionAndDestination( isotope.getPosition().getX(),
                            isotope.getPosition().getY() - protrusion );
                }
                else{
                    protrusion =  TEST_CHAMBER_RECT.getMinY() - (isotope.getPosition().getY() - isotope.getRadius());
                    if (protrusion >= 0){
                        isotope.setPositionAndDestination( isotope.getPosition().getX(),
                                isotope.getPosition().getY() + protrusion );
                    }
                }
            }
            else{
                // This isotope is not positioned correctly.
                System.err.println(getClass().getName() + " - Warning: Ignoring attempt to add incorrectly located isotope to test chamber.");
            }
        }

        public void removeIsotope( MovableAtom isotope ){
            containedIsotopes.remove( isotope );
            updateCountProperty();
            // Update the average atomic mass.
            if ( isotopeCountProperty.getValue() > 0 ){
                averageAtomicMassProperty.setValue( ( averageAtomicMassProperty.getValue() * ( isotopeCountProperty.getValue() + 1 )
                        - isotope.getAtomConfiguration().getAtomicMass() ) / isotopeCountProperty.getValue() );
            }
            else{
                averageAtomicMassProperty.setValue( 0.0 );
            }
        }

        public void removeAllIsotopes(){
            containedIsotopes.clear();
            updateCountProperty();
            averageAtomicMassProperty.setValue( 0.0 );
        }

        /**
         * Get the property that tracks the number of isotopes in the chamber.
         * This can be monitored to trigger updates to view elements.
         */
        public Property<Integer> getIsotopeCountProperty(){
            return isotopeCountProperty;
        }

        /**
         * Get the property that tracks the average atomic mass of the
         * isotopes in the chamber.  This can be monitored to trigger update
         * events.
         */
        public Property<Double> getAverageAtomicMassProperty(){
            return averageAtomicMassProperty;
        }

        private void updateCountProperty(){
            isotopeCountProperty.setValue( containedIsotopes.size() );
        }

        /**
         * Get the proportion of isotopes currently within the chamber that
         * match the specified configuration.  Note that electrons are
         * ignored.
         *
         * @param isotopeConfig - Atom representing the configuration in
         * question, MUST BE NEUTRAL.
         * @return
         */
        public double getIsotopeProportion( ImmutableAtom isotopeConfig ){
            assert isotopeConfig.getCharge() == 0;
            // TODO: This could be done by a map that is updated each time an
            // atom is added if better performance is needed.  This should be
            // decided before publishing this sim.
            int isotopeCount = 0;
            for ( MovableAtom isotope : containedIsotopes ){
                if ( isotopeConfig.equals( isotope.getAtomConfiguration() )){
                    isotopeCount++;
                }
            }
            return (double)isotopeCount / (double)containedIsotopes.size();
        }

        /**
         * Add the isotope to the chamber, and move it to an open location.
         *
         * @param isotope
         */
        public void addIsotopeToOpenLocation( MovableAtom isotope ){

        }
    }
}
