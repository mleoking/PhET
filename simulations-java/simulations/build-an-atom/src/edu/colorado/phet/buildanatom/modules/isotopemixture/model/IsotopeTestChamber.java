// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.modules.isotopemixture.model;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Class that represents a "test chamber" where multiple isotopes can be
 * placed.  The test chamber calculates the average atomic mass and the
 * proportions of the various isotopes.  It is intended to be contained in
 * the main model class.
 *
 * @author John Blanco
 */
public class IsotopeTestChamber {

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

    // Random number generator for generating positions.
    private static final Random RAND = new Random();

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    // Isotope Mixtures Model that contains this test chamber.
    private final IsotopeMixturesModel model;

    // List of isotopes that are inside the chamber.  This is updated as
    // isotopes come and go.
    private final List<MovableAtom> containedIsotopes = new ArrayList<MovableAtom>();

    // Property that tracks the number of isotopes in the chamber.  This
    // can be monitored in order to update the view.
    private final Property<Integer> isotopeCountProperty = new Property<Integer>( 0 );

    // Property that tracks the average atomic mass of all isotopes in
    // the chamber.
    private final Property<Double> averageAtomicMassProperty = new Property<Double>( 0.0 );

    // Override values.  These are used when overriding the calculated values
    // from the chamber.
    private boolean averageAtomicMassOverridden = false;
    private Map<ImmutableAtom, Double> isotopeProportionsOverride = null; // Null signals inactive.

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    IsotopeTestChamber( IsotopeMixturesModel model ){
        this.model = model;
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    public Dimension2D getTestChamberSize() {
        return SIZE;
    }

    /**
     * Get the number of isotopes currently in the chamber that match the
     * specified configuration.
     *
     * @param isotopeConfig
     * @return
     */
    public int getIsotopeCount( ImmutableAtom isotopeConfig ){
        assert isotopeConfig.getNumProtons() == isotopeConfig.getNumElectrons(); // Should always be neutral atom.
        int isotopeCount = 0;
        for ( MovableAtom isotope : containedIsotopes ) {
            if ( isotope.getAtomConfiguration().equals( isotopeConfig ) ) {
                isotopeCount++;
            }
        }
        return isotopeCount;
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
     * Returns true if the specified isotope instance is contained by the
     * chamber.  Note that it is possible for an isotope to be positioned
     * within the chamber bounds but not contained by it.
     *
     * @param isotope
     * @return
     */
    public boolean isIsotopeContained( MovableAtom isotope ){
        return containedIsotopes.contains( isotope );
    }

    /**
     * Override the calculated value for average atomic mass with the supplied
     * value.  This is necessary because we sometimes want to have accurate
     * values for the average atomic mass of this isotope in nature, but can't
     * put enough particles into the chamber to achieve the needed value.
     *
     * This override will be cleared when any particles are added to or removed
     * from the chamber, and can also be cleared manually.
     */
    public void overrideAverageAtomicMass( double overrideValue ){
        averageAtomicMassProperty.setValue( overrideValue );
        averageAtomicMassOverridden = true;
    }

    public void clearAverageAtomicMassOverride(){
        averageAtomicMassProperty.setValue( calculateAverageAtomicMass() );
        averageAtomicMassOverridden = false;
    }

    private void clearAllOverrides(){
        clearAverageAtomicMassOverride();
        clearIsotopeProportionsOverride();
    }

    private double calculateAverageAtomicMass(){
        double totalMass = 0;
        for ( MovableAtom isotope : containedIsotopes ){
            totalMass += isotope.getAtomConfiguration().getAtomicMass();
        }
        return totalMass / containedIsotopes.size();
    }

    /**
     * Override the calculated proportions for the contained isotopes.  This
     * is necessary because we sometimes want to have accurate values of
     * isotope proportions in nature, but can't add enough particles to the
     * chamber to make the calculations come out right.
     *
     * This override will be cleared when any particles are added to or removed
     * from the chamber, and can also be cleared manually.
     */
    public void overrideIsotopeProportions( Map<ImmutableAtom, Double> overrideValue ){
        isotopeProportionsOverride = overrideValue;
    }

    public void clearIsotopeProportionsOverride(){
        isotopeProportionsOverride = null;
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
    protected void addIsotopeToChamber( MovableAtom isotope ){
        if ( isIsotopePositionedOverChamber( isotope ) ){
            clearAllOverrides();
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

    public void removeIsotopeFromChamber( MovableAtom isotope ){
        clearAllOverrides();
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

    /**
     * Remove an isotope from the chamber that matches the specified atom
     * configuration.  Note that electrons are ignored.
     */
    public MovableAtom removeIsotopeMatchingConfig( ImmutableAtom isotopeConfig ){
        // Argument checking.
        if (isotopeConfig.getCharge() != 0){
            throw new IllegalArgumentException( "Isotope must be neutral" );
        }
        // Locate and remove a matching isotope.
        MovableAtom removedIsotope = null;
        for ( MovableAtom isotope : containedIsotopes ){
            if ( isotope.getAtomConfiguration().equals( isotopeConfig )){
                removedIsotope = isotope;
                break;
            }
        }
        removeIsotopeFromChamber( removedIsotope );
        return removedIsotope;
    }

    public void removeAllIsotopes( boolean removeFromModel ){
//        List<MovableAtom> containedIsotopesCopy = new ArrayList<MovableAtom>( containedIsotopes );
//        for ( MovableAtom isotope : containedIsotopesCopy ) {
//            removeIsotopeFromChamber( isotope );
//            if ( removeFromModel ){
//                isotope.removeListener( model.isotopeGrabbedListener );
//                isotope.removedFromModel();
//            }
//        }
        clearAverageAtomicMassOverride();
        clearIsotopeProportionsOverride();
        List<MovableAtom> containedIsotopesCopy = new ArrayList<MovableAtom>( containedIsotopes );
        containedIsotopes.clear();
        if ( removeFromModel ){
            for ( MovableAtom isotope : containedIsotopesCopy ) {
                isotope.removeListener( model.isotopeGrabbedListener );
                isotope.removedFromModel();
            }
        }
        updateCountProperty();
        averageAtomicMassProperty.setValue( 0.0 );

        assert isotopeCountProperty.getValue() == 0;      // Logical consistency check.
        assert averageAtomicMassProperty.getValue() == 0; // Logical consistency check.
    }

    protected List<MovableAtom> getContainedIsotopes() {
        return containedIsotopes;
    }

    /**
     * Get a count of the total number of isotopes in the chamber.
     */
    public int getTotalIsotopeCount(){
        return isotopeCountProperty.getValue();
    }


    public void addTotalCountChangeObserver( SimpleObserver so ){
        isotopeCountProperty.addObserver( so );
    }

    private void updateCountProperty(){
        isotopeCountProperty.setValue( containedIsotopes.size() );
    }

    public void addAverageAtomicMassPropertyListener( SimpleObserver so ){
        averageAtomicMassProperty.addObserver( so );
    }

    public double getAverageAtomicMass(){
        return averageAtomicMassProperty.getValue();
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
        double isotopeProportion = 0;
        if ( isotopeProportionsOverride != null ){
            // The override is currently active, so return the value it
            // specifies.
            if ( isotopeProportionsOverride.containsKey( isotopeConfig )){
                isotopeProportion = isotopeProportionsOverride.get( isotopeConfig );
            }
        }
        else{
            // TODO: This could be done by a map that is updated each time an
            // atom is added if better performance is needed.  This should be
            // decided before publishing this sim.
            int isotopeCount = 0;
            for ( MovableAtom isotope : containedIsotopes ){
                if ( isotopeConfig.equals( isotope.getAtomConfiguration() )){
                    isotopeCount++;
                }
            }
            isotopeProportion = (double)isotopeCount / (double)containedIsotopes.size();
        }
        return isotopeProportion;
    }

    /**
     * Generate a random location within the test chamber.
     * @return
     */
    public Point2D generateRandomLocation(){
        return new Point2D.Double(
                TEST_CHAMBER_RECT.getMinX() + RAND.nextDouble() * TEST_CHAMBER_RECT.getWidth(),
                TEST_CHAMBER_RECT.getMinY() + RAND.nextDouble() * TEST_CHAMBER_RECT.getHeight() );
    }

    public State getState(){
        return new State( this );
    }

    /**
     * Restore a previously captured state.
     */
    public void setState( State state ){
        removeAllIsotopes( true );
        for( MovableAtom isotope : state.getContainedIsotopes() ){
            addIsotopeToChamber( isotope );
        }
        isotopeProportionsOverride = state.isotopeProportionsOverride;
        if ( state.isAverageAtomicMassOverridden() ){
            overrideAverageAtomicMass( state.getAverageAtomicMassValue() );
        }
    }

    /**
     * Class that contains the state of the isotope test chamber, and can be
     * used for saving and later restoring the state.
     */
    public static class State {
        private final List<MovableAtom> containedIsotopes;
        private final boolean averageAtomicMassOverridden;
        private final double averageAtomicMassValue;
        private final Map<ImmutableAtom, Double> isotopeProportionsOverride;

        public State( IsotopeTestChamber isotopeTestChamber ){
            this.containedIsotopes = new ArrayList<MovableAtom>( isotopeTestChamber.getContainedIsotopes() );
            this.averageAtomicMassOverridden = isotopeTestChamber.averageAtomicMassOverridden;
            this.averageAtomicMassValue = isotopeTestChamber.getAverageAtomicMass();
            if ( isotopeTestChamber.isotopeProportionsOverride != null ){
                this.isotopeProportionsOverride = new HashMap<ImmutableAtom, Double>( isotopeTestChamber.isotopeProportionsOverride );
            }
            else{
                this.isotopeProportionsOverride = null;
            }
        }

        protected List<MovableAtom> getContainedIsotopes() {
            return containedIsotopes;
        }

        /**
         * @return the averageAtomicMassOverridden
         */
        public boolean isAverageAtomicMassOverridden() {
            return averageAtomicMassOverridden;
        }

        /**
         * @return the averageAtomicMassValue
         */
        public double getAverageAtomicMassValue() {
            return averageAtomicMassValue;
        }

        /**
         * @return the isotopeProportionsOverride
         */
        public Map<ImmutableAtom, Double> getIsotopeProportionsOverride() {
            return isotopeProportionsOverride;
        }
    }
}