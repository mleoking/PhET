/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.modules.isotopemixture.model;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
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
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
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

    // List of colors which will be used to represent the various isotopes.
    private static final Color [] ISOTOPE_COLORS = new Color [] { new Color( 180, 82, 205), Color.green,
        new Color(255, 69, 0), new Color( 139, 90, 43 ) };

    // Enum of the possible interactivity types.
    public enum InteractivityMode {
        BUCKETS_AND_LARGE_ATOMS,  // The user is dragging large isotopes between the test chamber and a set of buckets.
        SLIDERS_AND_SMALL_ATOMS   // The user is adding and removing small isotopes to/from the chamber using sliders.
    }

    // -----------------------------------------------------------------------
    // Instance Data
    // -----------------------------------------------------------------------

    private final BuildAnAtomClock clock;

    // The test chamber into and out of which the isotopes can be moved.
    private final IsotopeTestChamber testChamber = new IsotopeTestChamber( this );

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

    // List of the isotope buckets.
    private final List<MonoIsotopeParticleBucket> bucketList = new ArrayList<MonoIsotopeParticleBucket>();

    // List of the numerical controls that, when present, can be used to add
    // or remove isotopes to/from the test chamber.
    private final List<NumericalIsotopeQuantityControl> numericalControllerList =
            new ArrayList<NumericalIsotopeQuantityControl>();

    // Property that determines the type of user interactivity that is set.
    // See the enum definition for more information about the modes.
    private final Property<InteractivityMode> interactivityModeProperty =
        new Property<InteractivityMode>( InteractivityMode.BUCKETS_AND_LARGE_ATOMS );

    // Map of elements to user mixes.  These are restored when switching
    // between elements.  The integer represents the atomic number.
    private final Map<Integer, State> mapIsotopeConfigToUserMixState = new HashMap<Integer, State>();

    // Property that determines whether the user's mix or nature's mix is
    // being displayed.  When this is set to true, indicating that nature's
    // mix should be displayed, the isotope size property is ignored.
    private final BooleanProperty showingNaturesMix = new BooleanProperty( false );

    // Listener support.
    private final List<Listener> listeners = new ArrayList<Listener>();

    // -----------------------------------------------------------------------
    // Constructor(s)
    // -----------------------------------------------------------------------

    public IsotopeMixturesModel( BuildAnAtomClock clock ) {
        this.clock = clock;

        // Listen to our own interactive mode property so that things can be
        // reconfigured when this property changes.
        interactivityModeProperty.addObserver( new SimpleObserver() {
            public void update() {
                assert showingNaturesMix.getValue() == false; // Should never change when showing nature's mix.
                addIsotopeControllers();
                if ( interactivityModeProperty.getValue() == InteractivityMode.BUCKETS_AND_LARGE_ATOMS ){
                    addInitialIsotopesToBuckets();
                }
            }
        }, false );

        // Listen to our own "showing nature's mix" property so that we can
        // show and hide the appropriate isotopes when the value changes.
        showingNaturesMix.addObserver( new SimpleObserver() {
            public void update() {
                if ( showingNaturesMix.getValue() ){
                    // Save the current user's mix.
                    mapIsotopeConfigToUserMixState.put( prototypeIsotope.getNumProtons(), getState() );
                    // Hide the user's mix, show nature's mix.
                    hideUsersMix();
                    showNaturesMix();
                }
                else{
                    clearTestChamber();
                    showUsersMix2();
                }
            }
        }, false );
    }

    // -----------------------------------------------------------------------
    // Methods
    // -----------------------------------------------------------------------

    /**
     * Create and add an isotope of the specified configuration.  Where the
     * isotope is initially placed depends upon the current interactivity mode.
     */
    protected MovableAtom createAndAddIsotope( ImmutableAtom isotopeConfig ){
        assert isotopeConfig.getNumProtons() == prototypeIsotope.getNumProtons(); // Verify that this is a valid isotope.
        assert isotopeConfig.getNumProtons() == isotopeConfig.getNumElectrons();  // Should always be neutral.
        MovableAtom newIsotope;
        if ( interactivityModeProperty.getValue() == InteractivityMode.BUCKETS_AND_LARGE_ATOMS ){
            // Create the specified isotope and add it to the appropriate bucket.
            newIsotope = new MovableAtom( isotopeConfig.getNumProtons(), isotopeConfig.getNumNeutrons(),
                    LARGE_ISOTOPE_RADIUS, new Point2D.Double(), getClock() );
            newIsotope.addListener( isotopeGrabbedListener );
            // Add this isotope to a bucket.
            getBucketForIsotope( isotopeConfig ).addIsotopeInstance( newIsotope );
        }
        else{
            // Create the specified isotope and add it directly to the test chamber.
            Point2D randomIsotopeLocation = testChamber.generateRandomLocation();
            newIsotope = new MovableAtom( isotopeConfig.getNumProtons(), isotopeConfig.getNumNeutrons(),
                    SMALL_ISOTOPE_RADIUS, randomIsotopeLocation, getClock() );
            testChamber.addIsotopeToChamber( newIsotope );
        }
        notifyIsotopeInstanceAdded( newIsotope );
        return newIsotope;
    }

    /**
     * Remove an arbitrary isotope instance that matches the specified
     * configuration from the model.
     *
     * @return The removed isotope instance, null of no instances can be
     * found.
     */
    protected MovableAtom removeArbitraryIsotope( ImmutableAtom isotopeConfig ){
        // For now, this only handles the case where the interactivity mode is
        // set to be sliders, since that is all that is initially needed.
        assert interactivityModeProperty.getValue() == InteractivityMode.SLIDERS_AND_SMALL_ATOMS;
        MovableAtom removedIsotope = testChamber.removeIsotopeMatchingConfig( isotopeConfig );
        removedIsotope.removedFromModel();
        return removedIsotope;
    }

    public BuildAnAtomClock getClock() {
        return clock;
    }

    public IDynamicAtom getAtom(){
        return prototypeIsotope;
    }

    private State getState(){
        return new State( this );
    }

    /**
     * Set the state of the model based on a previously created state
     * representation.
     */
    private void setState( State modelState ){

        // Restore the prototype isotope.
        prototypeIsotope.setConfiguration( modelState.getElementConfiguration() );

        // Restore the interactivity mode.
        interactivityModeProperty.setValue( modelState.getInteractivityMode() );

        // Clear the model of existing controllers.
        removeBuckets();
        removeNumericalControllers();
        // TODO: Need to remove isotopes from test chamber too.

        // Restore any particles that were in the test chamber.
        testChamber.setState( modelState.getIsotopeTestChamberState() );
        for ( MovableAtom isotope : testChamber.getContainedIsotopes() ){
            notifyIsotopeInstanceAdded( isotope );
        }

        // Add the buckets, if any, and send notifications for any particles
        // contained therein.
        for ( MonoIsotopeParticleBucket.State bucketState : modelState.getBucketStates() ) {
            addBucket( new MonoIsotopeParticleBucket( bucketState ) );
            for ( MovableAtom isotope : bucketState.getContainedIsotopes() ) {
                isotope.addedToModel();
                isotope.addListener( isotopeGrabbedListener );
                notifyIsotopeInstanceAdded( isotope );
            }
        }
    }

    /**
     * Set the element that is currently in use, and for which all stable
     * isotopes will be available for movement in and out of the test chamber.
     * In case you're wondering why this is done as an atom instead of just
     * setting the atomic number, it is so that this will play well with the
     * existing controllers (such as the PeriodicTableControlNode) that
     * already existed at the time this class was created.
     */
    public void setAtomConfiguration( IAtom atom ) {
        // This method does NOT check if the specified atom is already the
        // current configuration.  This allows it to be as a sort of reset
        // routine.  For the sake of efficiency, callers should be careful not
        // to call this when it isn't needed.

        if ( !showingNaturesMix.getValue() ){
            // Save the current user's mix state.
            mapIsotopeConfigToUserMixState.put( prototypeIsotope.getNumProtons(), getState() );
        }

        // Get a list of all stable isotopes for the current atomic number.
        ArrayList<ImmutableAtom> newIsotopeList = AtomIdentifier.getStableIsotopes( atom.getNumProtons() );

        // Sort from lightest to heaviest.
        Collections.sort( newIsotopeList, new Comparator<IAtom>(){
            public int compare( IAtom atom2, IAtom atom1 ) {
                return new Double(atom2.getAtomicMass()).compareTo( atom1.getAtomicMass() );
            }
        });

        // Update the list of possible isotopes for this atomic configuration.
        possibleIsotopesProperty.setValue( newIsotopeList );

        // Update the prototype atom (a.k.a. isotope) configuration.
        prototypeIsotope.setNumProtons( atom.getNumProtons() );
        prototypeIsotope.setNumElectrons( atom.getNumElectrons() );
        prototypeIsotope.setNumNeutrons( atom.getNumNeutrons() );

        // Display the isotopes to the user.
        if ( showingNaturesMix.getValue() ){
            showNaturesMix();
        }
        else{
            showUsersMix2();
        }
    }

    /**
     * Remove all buckets that are currently in the model, as well as the particles they contained.
     */
    private void removeBuckets(){
        for ( MonoIsotopeParticleBucket bucket : bucketList ) {
            for ( MovableAtom movableAtom : bucket.getContainedIsotopes() ) {
                movableAtom.removedFromModel();
            }
        }
        ArrayList< Bucket > oldBuckets = new ArrayList< Bucket >( bucketList );
        bucketList.clear();
        for ( Bucket bucket : oldBuckets ) {
            bucket.getPartOfModelProperty().setValue( false );
        }
    }

    /**
     * Add isotope controllers based on the specified list of isotopes and the
     * specified interactivity mode.  Isotope controllers are the model
     * portion of the devices with which the user can interact with in order
     * to add or remove isotopes to/from the test chamber.  There are two
     * types of controllers: buckets and numerical controllers (i.e. sliders).
     *
     * Note that this does NOT add any isotopes to the model.
     *
     * @param newIsotopeList
     * @param value
     */
    private void addIsotopeControllers() {
        // Figure out the atom radius to use based on the current atom size
        // setting.
        double isotopeRadius = interactivityModeProperty.getValue() == InteractivityMode.BUCKETS_AND_LARGE_ATOMS ? LARGE_ISOTOPE_RADIUS : SMALL_ISOTOPE_RADIUS;

        // Create a new list of controllers based on the new list of stable
        // isotopes.
        double controllerYOffset = testChamber.getTestChamberRect().getMinY() - 400;
        double interControllerDistanceX;
        double controllerXOffset;
        if ( possibleIsotopesProperty.getValue().size() < 4 ){
            // We can fit 3 or less cleanly under the test chamber.
            interControllerDistanceX = testChamber.getTestChamberRect().getWidth() / possibleIsotopesProperty.getValue().size();
            controllerXOffset = testChamber.getTestChamberRect().getMinX() + interControllerDistanceX / 2;
        }
        else{
            // Four controllers don't fit well under the chamber, so use a
            // positioning algorithm where they are extended a bit to the
            // right.
            interControllerDistanceX = (testChamber.getTestChamberRect().getWidth() * 1.2) / possibleIsotopesProperty.getValue().size();
            controllerXOffset = testChamber.getTestChamberRect().getMinX() + interControllerDistanceX / 2;
        }
        for ( int i = 0; i < possibleIsotopesProperty.getValue().size(); i++ ) {
            ImmutableAtom isotope = possibleIsotopesProperty.getValue().get( i );
            if ( interactivityModeProperty.getValue() == InteractivityMode.BUCKETS_AND_LARGE_ATOMS ){
                String bucketCaption = AtomIdentifier.getName( isotope ) + "-" + isotope.getMassNumber();
                MonoIsotopeParticleBucket newBucket = new MonoIsotopeParticleBucket( new Point2D.Double(
                        controllerXOffset + interControllerDistanceX * i, controllerYOffset ),
                        BUCKET_SIZE, getColorForIsotope( isotope ), bucketCaption, isotopeRadius,
                        isotope.getNumProtons(), isotope.getNumNeutrons() );
                addBucket( newBucket );
            }
            else{
                // Assume a numerical controller.
                NumericalIsotopeQuantityControl newController = new NumericalIsotopeQuantityControl( this, isotope,
                        new Point2D.Double(controllerXOffset + interControllerDistanceX * i, controllerYOffset) );
                numericalControllerList.add( newController );
                notifyNumericalControllerAdded( newController );
            }
        }
    }

    private void addBucket( MonoIsotopeParticleBucket newBucket ) {
        bucketList.add( newBucket );
        notifyBucketAdded( newBucket );
    }

    private void removeNumericalControllers(){
        ArrayList< NumericalIsotopeQuantityControl > oldControllers = new ArrayList< NumericalIsotopeQuantityControl >( numericalControllerList );
        numericalControllerList.clear();
        for ( NumericalIsotopeQuantityControl controller : oldControllers ) {
            controller.removedFromModel();
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
        for ( MonoIsotopeParticleBucket bucket : bucketList ){
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

    public Property<InteractivityMode> getInteractivityModeProperty() {
        return interactivityModeProperty;
    }

    public List<MonoIsotopeParticleBucket> getBucketList() {
        return bucketList;
    }

    protected List<NumericalIsotopeQuantityControl> getNumericalControllerListProperty() {
        return numericalControllerList;
    }

    public BooleanProperty getShowingNaturesMixProperty() {
        return showingNaturesMix;
    }

    public Color getColorForIsotope( ImmutableAtom isotope ) {
        int index = possibleIsotopesProperty.getValue().indexOf( isotope );
        return index >= 0 ? ISOTOPE_COLORS[possibleIsotopesProperty.getValue().indexOf( isotope )] : Color.WHITE;
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

    private void notifyBucketAdded( MonoIsotopeParticleBucket bucket ){
        for ( Listener listener : listeners ) {
            listener.isotopeBucketAdded( bucket );
        }
    }

    private void notifyNumericalControllerAdded( NumericalIsotopeQuantityControl controller ){
        for ( Listener listener : listeners ) {
            listener.isotopeNumericalControllerAdded( controller );
        }
    }

    /**
     * Hide the user's mix of isotopes.  This is generally done when
     * switching to show nature's mix.  It is accomplished by sending
     * notifications of removal for each isotope but keeping them around.
     */
    private void hideUsersMix(){
        // TODO: 3/14/2011 - This is known to be broken, since it removes
        // everything in the bucket or the chamber.  It should be fixed when
        // the state handling is implemented.
        testChamber.removeAllIsotopes( true );
        for ( MonoIsotopeParticleBucket bucket : bucketList ){
            List<MovableAtom> isotopesFromBucket = new ArrayList<MovableAtom>(bucket.getContainedIsotopes());
            bucket.reset(); // Clear the bucket.
            for ( MovableAtom isotope : isotopesFromBucket ){
                isotope.removeListener( isotopeGrabbedListener );
                isotope.removedFromModel();
            }
        }
    }

    /**
     * Show the user's mix.  If the user's mix has never been shown for this
     * element, show it in initial form, which is with the relatively large
     * isotopes in the buckets.
     */
    private void showUsersMix(){
        if ( interactivityModeProperty.getValue() == InteractivityMode.BUCKETS_AND_LARGE_ATOMS ){
            addInitialIsotopesToBuckets();
        }
    }

    /**
     * Show the user's mix.  If the user's mix has never been shown for this
     * element, show it in initial form, which is with the relatively large
     * isotopes in the buckets.
     */
    private void showUsersMix2(){
        assert showingNaturesMix.getValue() == false; // This method shouldn't be called if we're showing nature's mix.

        if ( mapIsotopeConfigToUserMixState.containsKey( prototypeIsotope.getNumProtons() ) ){
            // Restore previously stored state.
            setState( mapIsotopeConfigToUserMixState.get( prototypeIsotope.getNumProtons() ));
        }
        else{
            // Clear the test chamber.
            // TODO: Fundamental question here.  Does removing an isotope from
            // the test chamber remove it from the model?  With the recent
            // changes, sometimes it should and sometimes it shouldn't.  I've
            // added a flag for saying whether they go away entirely or just from
            // the chamber, but I find this ugly.  I'd like to think of a better
            // way.
            testChamber.removeAllIsotopes( true );

            // Remove the old isotope controllers (i.e. buckets or numerical
            // controllers).
            removeBuckets();
            removeNumericalControllers();

            // Add the new isotope controllers.
            addIsotopeControllers();

            if ( interactivityModeProperty.getValue() == InteractivityMode.BUCKETS_AND_LARGE_ATOMS ){
                // Create and add initial isotopes to their respective buckets.
                addInitialIsotopesToBuckets();
            }
        }
    }

    /**
     * Add the initial set of isotopes to the buckets.
     */
    private void addInitialIsotopesToBuckets() {
        for ( ImmutableAtom isotopeConfig : possibleIsotopesProperty.getValue() ) {
            MonoIsotopeParticleBucket isotopeBucket = getBucketForIsotope( isotopeConfig );
            assert isotopeBucket != null; // If there is no bucket for this isotope, there is a bug.

            // Create each isotope instance and add to appropriate bucket.
            for ( int i = 0; i < NUM_LARGE_ISOTOPES_PER_BUCKET; i++){
                createAndAddIsotope( isotopeConfig );
            }
        }
    }

    private void showNaturesMix(){
        assert showingNaturesMix.getValue() == true; // This method shouldn't be called if we're not showing nature's mix.
        int totalNumIsotopes = 1000;  // TODO: Make this a constant if actually used.

        // Clear out anything that is in the test chamber.  If anything
        // needed to be stored, it should have been done by now.
        clearTestChamber();

        // Get the list of possible isotopes and then sort it by abundance
        // so that the least abundant are added last, thus assuring that
        // they will be visible.
        ArrayList<ImmutableAtom> possibleIsotopesCopy = new ArrayList<ImmutableAtom>(getPossibleIsotopesProperty().getValue());
        Collections.sort( possibleIsotopesCopy,  new Comparator<IAtom>(){
                public int compare( IAtom atom2, IAtom atom1 ) {
                    return new Double(AtomIdentifier.getNaturalAbundance( atom1 )).compareTo( AtomIdentifier.getNaturalAbundance( atom2 ) );
                }
            } );

        // Add the isotopes.
        for ( ImmutableAtom isotopeConfig : possibleIsotopesCopy ){
            int numToCreate = (int)Math.round( totalNumIsotopes * AtomIdentifier.getNaturalAbundance( isotopeConfig ) );
            if ( numToCreate == 0 ){
                System.err.println("Warning, quantity at zero for " + AtomIdentifier.getName( isotopeConfig ) + "-" + isotopeConfig.getMassNumber());
                numToCreate = 1;
            }
            for ( int i = 0; i < numToCreate; i++){
                MovableAtom newIsotope = new MovableAtom(
                        isotopeConfig.getNumProtons(),
                        isotopeConfig.getNumNeutrons(),
                        SMALL_ISOTOPE_RADIUS,
                        testChamber.generateRandomLocation(),
                        clock );
                testChamber.addIsotopeToChamber( newIsotope );
                notifyIsotopeInstanceAdded( newIsotope );
            }
        }
    }

    private void clearTestChamber(){
        for ( MovableAtom isotope : new ArrayList<MovableAtom>( testChamber.getContainedIsotopes() ) ){
            testChamber.removeIsotopeFromChamber( isotope );
            isotope.removeListener( isotopeGrabbedListener );
            isotope.removedFromModel();
        }
    }

    // -----------------------------------------------------------------------
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
                testChamber.removeIsotopeFromChamber( isotope );
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
                testChamber.addIsotopeToChamber( isotope );
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
        void isotopeBucketAdded( MonoIsotopeParticleBucket bucket );
        void isotopeNumericalControllerAdded( NumericalIsotopeQuantityControl controller );
    }

    public static class Adapter implements Listener {
        public void isotopeInstanceAdded( MovableAtom atom ) {}
        public void isotopeBucketAdded( MonoIsotopeParticleBucket bucket ) {}
        public void isotopeNumericalControllerAdded( NumericalIsotopeQuantityControl controller ) {}
    }

    /**
     * Class that defines the state of the model.  This can be used for saving
     * and restoring of the state.
     *
     * @author John Blanco
     */
    private static class State {

        private final ImmutableAtom elementConfig;
        private final IsotopeTestChamber.State isotopeTestChamberState;
        private final ArrayList<MonoIsotopeParticleBucket.State> bucketStates = new ArrayList<MonoIsotopeParticleBucket.State>(  );
        private final InteractivityMode interactivityMode;

        public State( IsotopeMixturesModel model ){
            elementConfig = model.getAtom().toImmutableAtom();
            isotopeTestChamberState = model.getIsotopeTestChamber().getState();
            interactivityMode = model.getInteractivityModeProperty().getValue();
            for ( MonoIsotopeParticleBucket bucket : model.bucketList ) {
                bucketStates.add( bucket.getState() );
            }
        }

        public IAtom getElementConfiguration() {
            return elementConfig;
        }

        public IsotopeTestChamber.State getIsotopeTestChamberState() {
            return isotopeTestChamberState;
        }

        public ArrayList<MonoIsotopeParticleBucket.State> getBucketStates() {
            return bucketStates;
        }

        public InteractivityMode getInteractivityMode() {
            return interactivityMode;
        }
    }

    /**
     * This class is the model representation of a numerical controller that
     * allows the user to add or remove isotopes from the test chamber.  It is
     * admittedly a little odd to have a class like this that is really more
     * of a view sort of thing, but it was needed in order to be consistent
     * with the buckets, which are the other UI device that the user has for
     * moving isotopes into and out of the test chamber.  The buckets must
     * have a presence in the model so that the isotopes that are outside of
     * the chamber have somewhere to go, so this class allows buckets and
     * other controls to be handled consistently between the model and view.
     */
    public static class NumericalIsotopeQuantityControl {
        private static final int CAPACITY = 100;
        private final Point2D centerPosition = new Point2D.Double();
        private final ImmutableAtom isotopeConfig;
        private final IsotopeMixturesModel model;

        // This property tracks whether this model element is still a part
        // of the active model, such that it should be displayed in the view.
        private final BooleanProperty partOfModelProperty = new BooleanProperty( true );

        /**
         * Constructor.
         */
        public NumericalIsotopeQuantityControl( IsotopeMixturesModel model, ImmutableAtom atomConfig, Point2D position ){
            this.model = model;
            this.isotopeConfig = atomConfig;
            this.centerPosition.setLocation( position );
        }

        public Point2D getCenterPositionRef(){
            return centerPosition;
        }

        public ImmutableAtom getAtomConfig(){
            return isotopeConfig;
        }

        public int getCapacity(){
            return CAPACITY;
        }

        public BooleanProperty getPartOfModelProperty(){
            return partOfModelProperty;
        }

        /**
         * Notify this model element that it has been removed from the model.
         * This will result in notifications being sent that should cause view
         * elements to be removed from the view.
         */
        protected void removedFromModel(){
            partOfModelProperty.setValue( false );
        }

        /**
         * Create a new instance of the isotope for this controller and add
         * it to the test chamber.
         */
        public void addIsotopeToChamber(){
            model.createAndAddIsotope( isotopeConfig );
        }

        /**
         * Set the quantity of the isotope associated with this control to the
         * specified value.
         *
         * @param quantity
         * @return
         */
        public void setIsotopeQuantity( int targetQuantity ){
            int changeAmount = targetQuantity - model.getIsotopeTestChamber().getIsotopeCount( isotopeConfig );
            if (changeAmount > 0){
                for (int i = 0; i < changeAmount; i++){
                    MovableAtom newIsotope = new MovableAtom( isotopeConfig.getNumProtons(),
                            isotopeConfig.getNumNeutrons(), SMALL_ISOTOPE_RADIUS,
                            model.getIsotopeTestChamber().generateRandomLocation(), model.getClock() );
                    model.getIsotopeTestChamber().addIsotopeToChamber( newIsotope );
                    model.notifyIsotopeInstanceAdded( newIsotope );
                }
            }
            else if ( changeAmount < 0 ){
                for (int i = 0; i < -changeAmount; i++){
                    MovableAtom isotope = model.getIsotopeTestChamber().removeIsotopeMatchingConfig( isotopeConfig );
                    if (isotope != null){
                        isotope.removedFromModel();
                    }
                }
            }
        }

        /**
         * @return
         */
        public Color getBaseColor() {
            return model.getColorForIsotope( isotopeConfig );
        }
    }
}
