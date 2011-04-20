// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.photonabsorption.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.photonabsorption.model.atoms.Atom;
import edu.colorado.phet.common.photonabsorption.model.atoms.AtomicBond;
import edu.colorado.phet.common.photonabsorption.model.molecules.CH4;
import edu.colorado.phet.common.photonabsorption.model.molecules.CO2;
import edu.colorado.phet.common.photonabsorption.model.molecules.H2O;
import edu.colorado.phet.common.photonabsorption.model.molecules.N2;
import edu.colorado.phet.common.photonabsorption.model.molecules.N2O;
import edu.colorado.phet.common.photonabsorption.model.molecules.O;
import edu.colorado.phet.common.photonabsorption.model.molecules.O2;

/**
 * Class that represents a molecule in the model.  This, by its nature, is
 * essentially a composition of other objects, generally atoms and atomic
 * bonds.
 *
 * @author John Blanco
 */
public abstract class Molecule {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    private static final double PHOTON_EMISSION_SPEED = 2; // Picometers per second.

    private static final double PHOTON_ABSORPTION_DISTANCE = 100;

    private static final Random RAND = new Random();

    private static final double VIBRATION_FREQUENCY = 5;  // Cycles per second of sim time.
    private static final double ROTATION_RATE = 1.1;  // Revolutions per second of sim time.
    private static final double ABSORPTION_HYSTERESIS_TIME = 200; // Milliseconds of sim time.

    // Scaler quantity representing the speed at which the constituent particles
    // move away from each other.  Note that this is a relative speed, not one
    // that is absolute in model space.
    protected static final double BREAK_APART_VELOCITY = 3.0;

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // Atoms and bonds that comprise this molecule.
    private final ArrayList<Atom> atoms = new ArrayList<Atom>();
    private final ArrayList<AtomicBond> atomicBonds = new ArrayList<AtomicBond>();

    // This is basically the location of the molecule, but it is specified as
    // the center of gravity since a molecule is a composite object.
    private final Point2D centerOfGravity = new Point2D.Double();

    // Structure of the molecule in terms of offsets from the center of
    // gravity.  These indicate the atom's position in the "relaxed" (i.e.
    // non-vibrating), non-rotated state.
    private final Map<Atom, Vector2D> initialAtomCogOffsets = new HashMap<Atom, Vector2D>();

    // Vibration offsets - these represent the amount of deviation from the
    // initial (a.k.a relaxed) configuration for each molecule.
    private final Map<Atom, Vector2D> vibrationAtomOffsets = new HashMap<Atom, Vector2D>();

    // Listeners to events that come from this molecule.
    protected final ArrayList<Listener> listeners = new ArrayList<Listener>();

    // Velocity for this molecule.
    private final Vector2D velocity = new Vector2D( 0, 0 );

    // Map that matches photon wavelengths to photon absorption strategies.
    // The strategies contained in this structure define whether the
    // molecule can absorb a given photon and, if it does absorb it, how it
    // will react.
    private final Map<Double, PhotonAbsorptionStrategy> mapWavelengthToAbsorptionStrategy = new HashMap<Double, PhotonAbsorptionStrategy>();

    // Currently active photon absorption strategy, active because a photon
    // was absorbed that activated it.
    private PhotonAbsorptionStrategy activePhotonAbsorptionStrategy = new PhotonAbsorptionStrategy.NullPhotonAbsorptionStrategy( this );

    // Variable that prevents reabsorption for a while after emitting a photon.
    private double absorbtionHysteresisCountdownTime = 0;

    // The "pass through photon list" keeps track of photons that were not
    // absorbed due to random probability (essentially a simulation of quantum
    // properties).  This is needed since the absorption of a given photon
    // will likely be tested at many time steps as the photon moves past the
    // molecule, and we don't want to keep deciding about the same photon.
    private static final int PASS_THROUGH_PHOTON_LIST_SIZE = 10;
    private final ArrayList<Photon> passThroughPhotonList = new ArrayList<Photon>( PASS_THROUGH_PHOTON_LIST_SIZE );

    // The current point within this molecule's vibration sequence.
    private double currentVibrationRadians = 0;

    // The amount of rotation currently applied to this molecule.  This is
    // relative to its original, non-rotated state.
    private double currentRotationRadians = 0;

    // Tracks if molecule is higher energy than its ground state.
    private boolean highElectronicEnergyState = false;

    // Boolean values that track whether the molecule is vibrating or
    // rotating.
    private boolean vibrating = false;
    private boolean rotating = false;
    private boolean rotationDirectionClockwise = true; // Controls the direction of rotation.

    // List of constituent molecules. This comes into play only when the
    // molecule breaks apart, which many of the molecules never do.
    private final ArrayList<Molecule> constituentMolecules = new ArrayList<Molecule>();

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    protected void setPhotonAbsorptionStrategy( double wavelength, PhotonAbsorptionStrategy strategy ){
        mapWavelengthToAbsorptionStrategy.put( wavelength, strategy );
    }

    protected boolean isPhotonAbsorbed() {
        // If there is an active non-null photon absorption strategy, it
        // indicates that a photon has been absorbed.
        return !(activePhotonAbsorptionStrategy instanceof PhotonAbsorptionStrategy.NullPhotonAbsorptionStrategy);
    }

    /**
     * Add an initial offset from the molecule's Center of Gravity (COG).
     * The offset is "initial" because this is where the atom should be when
     * it is not vibrating or rotating.
     */
    protected void addInitialAtomCogOffset( Atom atom, Vector2D offset ){
        // Check that the specified atom is a part of this molecule.  While it
        // would probably work to add the offsets first and the atoms later,
        // that's not how the sim was designed, so this is some enforcement of
        // the "add the atoms first" policy.
        assert atoms.contains( atom );

        initialAtomCogOffsets.put( atom, offset );
    }

    /**
     * Get the initial offset from the molecule's center of gravity (COG) for
     * the specified molecule.
     */
    protected Vector2D getInitialAtomCogOffset( Atom atom ){
        if ( !initialAtomCogOffsets.containsKey( atom )){
            System.out.println(getClass().getName() + " - Warning: Attempt to get initial COG offset for atom that is not in molecule.");
        }
        return initialAtomCogOffsets.get( atom );
    }

    /**
     * Get the current vibration offset from the molecule's center of gravity
     * (COG) for the specified molecule.
     */
    protected Vector2D getVibrationAtomOffset( Atom atom ){
        if ( !vibrationAtomOffsets.containsKey( atom )){
            System.out.println(getClass().getName() + " - Warning: Attempt to get vibrational COG offset for atom that is not in molecule.");
        }
        return vibrationAtomOffsets.get( atom );
    }

    /**
     * Add a "constituent molecule" to this molecule's list.  Constituent
     * molecules are what this molecule will break into if it breaks apart.
     * Note that this does NOT check for any sort of conservation of atoms,
     * so use this carefully or weird break apart behaviors could result.
     */
    protected void addConstituentMolecule( Molecule molecule ){
        constituentMolecules.add( molecule );
    }

    /**
     * Get the ID used for this molecule.
     */
    public abstract MoleculeID getMoleculeID();

    /**
     * Static factory method for producing molecules of a given type.
     */
    public static Molecule createMolecule( MoleculeID moleculeID){
        Molecule newMolecule = null;
        switch ( moleculeID ){
        case CH4:
            newMolecule = new CH4();
            break;
        case CO2:
            newMolecule = new CO2();
            break;
        case H2O:
            newMolecule = new H2O();
            break;
        case N2:
            newMolecule = new N2();
            break;
        case N2O:
            newMolecule = new N2O();
            break;
        case O:
            newMolecule = new O();
            break;
        case O2:
            newMolecule = new O2();
            break;
        default:
            System.err.println("Molecule: " + " - Error: Unrecognized molecule type.");
            assert false;
        }

        return newMolecule;
    }

    /**
     * Advance the molecule one step in time.
     */
    public void stepInTime(double dt){

        activePhotonAbsorptionStrategy.stepInTime( dt );

        if (absorbtionHysteresisCountdownTime >= 0){
            absorbtionHysteresisCountdownTime -= dt;
        }

        if ( vibrating ){
            advanceVibration( dt * VIBRATION_FREQUENCY / 1000 * 2 * Math.PI );
        }

        if ( rotating ){
            int directionMultiplier = rotationDirectionClockwise ? -1 : 1;
            rotate( dt * ROTATION_RATE / 1000 * 2 * Math.PI * directionMultiplier );
        }

        // Do any linear movement that is required.
        setCenterOfGravityPos( velocity.getDestination( centerOfGravity ) );
        setCenterOfGravityPos( centerOfGravity.getX() + velocity.getX() * dt, centerOfGravity.getY() + velocity.getY() * dt);
    }

    /**
     * Reset the molecule.  Any photons that have been absorbed are forgotten,
     * and any vibration is reset.
     * @return
     */
    public void reset(){
        activePhotonAbsorptionStrategy.reset();
        activePhotonAbsorptionStrategy = new PhotonAbsorptionStrategy.NullPhotonAbsorptionStrategy( this );
        absorbtionHysteresisCountdownTime = 0;
        setVibrating( false );
        setVibration( 0 );
        setRotating( false );
        setRotation( 0 );
    }

    public boolean isVibrating() {
        return vibrating;
    }

    public void setVibrating( boolean vibration ) {
        this.vibrating = vibration;
    }

    public boolean isRotating() {
        return rotating;
    }

    public void setRotating( boolean rotating ) {
        this.rotating = rotating;
    }

    public void setRotationDirectionClockwise( boolean rotationDirectionClockwise ) {
        this.rotationDirectionClockwise = rotationDirectionClockwise;
    }

    /**
     * Initialize the offsets from the center of gravity for each atom within
     * this molecule.  This should be in the "relaxed" (i.e. non-vibrating)
     * state.
     */
    protected abstract void initializeAtomOffsets();

    public void addListener(Listener listener){
        // Don't bother adding if already there.
        if (!listeners.contains( listener )){
            listeners.add(listener);
        }
    }

    public void removeListener(Listener listener){
        listeners.remove(listener);
    }

    public Point2D getCenterOfGravityPos(){
        return new Point2D.Double(centerOfGravity.getX(), centerOfGravity.getY());
    }

    protected Point2D getCenterOfGravityPosRef(){
        return centerOfGravity;
    }

    /**
     * Set the location of this molecule by specifying the center of gravity.
     * This will be unique to each molecule's configuration, and it will cause
     * the individual molecules to be located such that the center of gravity
     * is in the specified location.  The relative orientation of the atoms
     * that comprise the molecules will not be changed.
     *
     * @param x the x location to set
     * @param y the y location to set
     */
    public void setCenterOfGravityPos( double x, double y ){
        if ( centerOfGravity.getX() != x || centerOfGravity.getY() != y){
            this.centerOfGravity.setLocation( x, y );
            updateAtomPositions();
            notifyCenterOfGravityPosChanged();
        }
    }

    public void setCenterOfGravityPos(Point2D centerOfGravityPos){
        setCenterOfGravityPos( centerOfGravityPos.getX(), centerOfGravityPos.getY() );
    }

    /**
     * Set the angle, in terms of radians from 0 to 2*PI, where this molecule
     * is in its vibration sequence.
     */
    public void setVibration( double vibrationRadians ) {
        currentVibrationRadians = vibrationRadians;
        return; // Implements no vibration by default, override in descendant classes as needed.
    }

    /**
     * Advance the vibration by the prescribed radians.
     *
     * @param deltaRadians
     */
    public void advanceVibration( double deltaRadians ){
        currentVibrationRadians += deltaRadians;
        setVibration( currentVibrationRadians );
    }

    /**
     * Rotate the molecule about the center of gravity by the specified number
     * of radians.
     *
     * @param deltaRadians
     */
    public void rotate( double deltaRadians ){
         setRotation( ( currentRotationRadians + deltaRadians ) % ( Math.PI * 2 ) );
    }

    public void setRotation( double radians ){
        if ( radians != currentRotationRadians ){
            currentRotationRadians = radians;
            updateAtomPositions();
        }
    }

    protected double getRotation(){
        return currentRotationRadians;
    }

    /**
     * Enable/disable a molecule's high electronic energy state, which in the
     * real world is a state where one or more electrons has moved to a higher
     * orbit.  In this simulation, it is generally depicted by having the
     * molecule appear to glow.
     *
     * @param highElectronicEnergyState
     */
    public void setHighElectronicEnergyState( boolean highElectronicEnergyState ){
        this.highElectronicEnergyState  = highElectronicEnergyState;
        notifyElectronicEnergyStateChanged();
    }

    private void notifyElectronicEnergyStateChanged() {
        for ( Listener listener : listeners ) {
            listener.electronicEnergyStateChanged( this );
        }
    }

    private void notifyCenterOfGravityPosChanged() {
        for ( Listener listener : listeners ) {
            listener.centerOfGravityPosChanged( this );
        }
    }

    public boolean isHighElectronicEnergyState(){
        return highElectronicEnergyState;
    }

    /**
     * Cause the molecule to dissociate, i.e. to break apart.
     */
    public void breakApart() {
        System.err.println( getClass().getName() + " - Error: breakApart invoked on a molecule for which the action is not implemented." );
        assert false;
    }

    protected void markPhotonForPassThrough(Photon photon){
        if (passThroughPhotonList.size() >= PASS_THROUGH_PHOTON_LIST_SIZE){
            // Make room for this photon be deleting the oldest one.
            passThroughPhotonList.remove( 0 );
        }
        passThroughPhotonList.add( photon );
    }

    protected boolean isPhotonMarkedForPassThrough(Photon photon){
        return (passThroughPhotonList.contains( photon ));
    }

    public ArrayList<Atom> getAtoms() {
        return new ArrayList<Atom>(atoms);
    }

    public ArrayList<AtomicBond> getAtomicBonds() {
        return new ArrayList<AtomicBond>(atomicBonds);
    }

    /**
     * Decide whether or not to absorb the offered photon.  If the photon is
     * absorbed, the matching absorption strategy is set so that it can
     * control the molecule's post-absorption behavior.
     *
     * @param photon - The photon offered for absorption.
     * @return
     */
    public boolean queryAbsorbPhoton( Photon photon ){

        boolean absorbPhoton = false;

        if ( !isPhotonAbsorbed() &&
             absorbtionHysteresisCountdownTime <= 0 &&
             photon.getLocation().distance( getCenterOfGravityPos() ) < PHOTON_ABSORPTION_DISTANCE
             &&!isPhotonMarkedForPassThrough( photon )
                ) {

            // The circumstances for absorption are correct, but do we have an
            // absorption strategy for this photon's wavelength?
            PhotonAbsorptionStrategy candidateAbsorptionStrategy = mapWavelengthToAbsorptionStrategy.get( photon.getWavelength() );
            if ( candidateAbsorptionStrategy != null ){
                // Yes, there is a strategy available for this wavelength.
                // Ask it if it wants the photon.
                if ( candidateAbsorptionStrategy.queryAndAbsorbPhoton( photon ) ){
                    // It does want it, so consider the photon absorbed.
                    absorbPhoton = true;
                    activePhotonAbsorptionStrategy = candidateAbsorptionStrategy;
                    activePhotonAbsorptionStrategy.queryAndAbsorbPhoton( photon );
                }else{
                    markPhotonForPassThrough( photon );//we have the decision logic once for whether a photon should be absorbed, so it is not queried a second time
                }
            }
        }

        return absorbPhoton;
    }

    public void setActiveStrategy( PhotonAbsorptionStrategy activeStrategy ) {
        this.activePhotonAbsorptionStrategy = activeStrategy;
    }

    protected void addAtom( Atom atom ){
        atoms.add( atom );
        initialAtomCogOffsets.put( atom, new Vector2D(0, 0) );
        vibrationAtomOffsets.put( atom, new Vector2D(0, 0) );
    }

    protected void addAtomicBond( AtomicBond atomicBond ){
        atomicBonds.add( atomicBond );
    }

    /**
     * Cause the atom to emit a photon of the specified wavelength.
     *
     * @param wavelength
     */
    public void emitPhoton( double wavelength ){
        emitPhoton( new Photon( wavelength, null ) );
    }

    /**
     * Emit the specified photon in a random direction.
     *
     * @param photonToEmit
     */
    protected void emitPhoton( Photon photonToEmit ){
        double emissionAngle = RAND.nextDouble() * Math.PI * 2;
        photonToEmit.setVelocity( (float)(PHOTON_EMISSION_SPEED * Math.cos( emissionAngle )),
                (float)(PHOTON_EMISSION_SPEED * Math.sin( emissionAngle )));
        final Point2D centerOfGravityPosRef = getCenterOfGravityPosRef();
        photonToEmit.setLocation( centerOfGravityPosRef.getX(),centerOfGravityPosRef.getY() );
        notifyPhotonEmitted( photonToEmit );
        absorbtionHysteresisCountdownTime = ABSORPTION_HYSTERESIS_TIME;
    }

    private void notifyPhotonEmitted(Photon photon){
        for (Listener listener : listeners){
            listener.photonEmitted( photon );
        }
    }

    /**
     * Update the positions of all atoms that comprise this molecule based on
     * the current center of gravity and the offset for each atom.
     */
    protected void updateAtomPositions() {
        for ( Atom atom : initialAtomCogOffsets.keySet() ) {
            Vector2D atomOffset = new Vector2D( initialAtomCogOffsets.get( atom ));
            // Add the vibration, if any exists.
            atomOffset.add( vibrationAtomOffsets.get( atom ));
            // Rotate.
            atomOffset.rotate( currentRotationRadians );
            // Set location based on combination of offset and current center
            // of gravity.
            atom.setPosition( centerOfGravity.getX() + atomOffset.getX(), centerOfGravity.getY() + atomOffset.getY() );
        }
    }

    public void setVelocity( double vx, double vy ) {
        setVelocity( new ImmutableVector2D( vx, vy ) );
    }

    public void setVelocity( ImmutableVector2D newVelocity) {
        this.velocity.setValue( newVelocity );
    }

    public ImmutableVector2D getVelocity() {
        return velocity;
    }

    /**
     * Get an enclosing rectangle for this molecule.  This was created to
     * support searching for open locations for new molecules.
     *
     * @return
     */
    public Rectangle2D getBoundingRect(){
        Rectangle2D [] atomRects = new Rectangle2D[atoms.size()];
        for (int i = 0; i < atoms.size(); i++){
            atomRects[i] = atoms.get( i ).getBoundingRect();
        }

        return RectangleUtils.union( atomRects );
    }

    //------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

    public ArrayList<Molecule> getBreakApartConstituents() {
        return constituentMolecules;
    }

    public interface Listener {
        void photonEmitted( Photon photon );
        void brokeApart( Molecule molecule );
        void electronicEnergyStateChanged( Molecule molecule );
        void centerOfGravityPosChanged( Molecule molecule );
    }

    public static class Adapter implements Listener {
        public void photonEmitted(Photon photon) {}
        public void brokeApart(Molecule molecule) {}
        public void electronicEnergyStateChanged( Molecule molecule ) {}
        public void centerOfGravityPosChanged( Molecule molecule ) {}
    }
}
