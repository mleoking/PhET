// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.statesofmatter.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.defaults.AtomicInteractionDefaults;
import edu.colorado.phet.statesofmatter.model.particle.ConfigurableStatesOfMatterAtom;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;

/**
 * This is the model for two atoms interacting with a Lennard-Jones
 * interaction potential.
 *
 * @author John Blanco
 */
public class DualAtomModel {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    public static final int BONDING_STATE_UNBONDED = 0;
    public static final int BONDING_STATE_BONDING = 1;
    public static final int BONDING_STATE_BONDED = 2;

    private static final AtomType DEFAULT_ATOM_TYPE = AtomType.NEON;
    private static final int CALCULATIONS_PER_TICK = 8;
    private static final double THRESHOLD_VELOCITY = 100;  // Used to distinguish small oscillations from real movement. 
    private static final int VIBRATION_DURATION = 1200;  // In milliseconds. 
    private static final int VIBRATION_COUNTER_RESET_VALUE = VIBRATION_DURATION / AtomicInteractionDefaults.CLOCK_FRAME_DELAY;
    private static final double BONDED_OSCILLATION_PROPORTION = 0.06; // Proportion of atom radius.

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private final ArrayList<Listener> m_listeners = new ArrayList<Listener>();
    private StatesOfMatterAtom m_fixedAtom;
    private StatesOfMatterAtom m_movableAtom;
    private StatesOfMatterAtom m_shadowMovableAtom;
    private double m_attractiveForce;
    private double m_repulsiveForce;
    private boolean m_motionPaused;
    private final LjPotentialCalculator m_ljPotentialCalculator;
    private double m_timeStep;
    private final StatesOfMatterAtom.Adapter m_movableAtomListener;
    private boolean m_settingBothAtomTypes = false;  // Flag used to prevent getting in disallowed state.
    private int m_bondingState = BONDING_STATE_UNBONDED; // Tracks whether the atoms have formed a chemical bond.
    private int m_vibrationCounter = 0; // Used to vibrate fixed atom during bonding.
    private double m_potentialWhenAtomReleased = 0; // Used to set magnitude of vibration.
    private final Random m_rand = new Random();
    private double m_bondedOscillationRightDistance;
    private double m_bondedOscillationLeftDistance;
    private double m_minPotentialDistance;
    private final ConstantDtClock m_clock;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

    public DualAtomModel( ConstantDtClock clock ) {

        m_clock = clock;
        m_motionPaused = false;
        m_ljPotentialCalculator = new LjPotentialCalculator( StatesOfMatterConstants.MIN_SIGMA,
                                                             StatesOfMatterConstants.MIN_EPSILON ); // Initial values arbitrary, will be set during reset.
        updateTimeStep();

        // Register as a clock listener.
        clock.addClockListener( new ClockAdapter() {

            public void clockTicked( ClockEvent clockEvent ) {
                handleClockTicked( clockEvent );
            }

            public void simulationTimeReset( ClockEvent clockEvent ) {
                reset();
            }
        } );

        // Create a listener for detecting when the movable atom is moved
        // directly by the user.
        m_movableAtomListener = new StatesOfMatterAtom.Adapter() {
            public void positionChanged() {
                if ( m_motionPaused ) {
                    // The user must be moving the atom from the view.
                    // Update the forces correspondingly.
                    try {
                        m_shadowMovableAtom = (StatesOfMatterAtom) m_movableAtom.clone();
                    }
                    catch ( CloneNotSupportedException e ) {
                        e.printStackTrace();
                        return;
                    }
                    updateForces();
                }
            }
        };

        // Put the model into its initial state.
        reset();
    }

    //----------------------------------------------------------------------------
    // Accessor Methods
    //----------------------------------------------------------------------------

    public StatesOfMatterAtom getFixedAtomRef() {
        return m_fixedAtom;
    }

    public StatesOfMatterAtom getMovableAtomRef() {
        return m_movableAtom;
    }

    public double getAttractiveForce() {
        return m_attractiveForce;
    }

    public double getRepulsiveForce() {
        return m_repulsiveForce;
    }

    public AtomType getFixedAtomType() {
        return m_fixedAtom.getType();
    }

    public AtomType getMovableAtomType() {
        return m_movableAtom.getType();
    }

    public void setFixedAtomType( AtomType atomType ) {

        if ( m_fixedAtom == null || m_fixedAtom.getType() != atomType ) {
            if ( !m_settingBothAtomTypes &&
                 ( ( atomType == AtomType.ADJUSTABLE && m_movableAtom.getType() != AtomType.ADJUSTABLE ) ||
                   ( atomType != AtomType.ADJUSTABLE && m_movableAtom.getType() == AtomType.ADJUSTABLE ) ) ) {
                System.err.println( this.getClass().getName() + " - Error: Cannot set just one atom to be adjustable, ignoring request." );
                return;
            }
            ensureValidAtomType( atomType );
            m_bondingState = BONDING_STATE_UNBONDED;

            // Inform any listeners of the removal of existing atoms.
            if ( m_fixedAtom != null ) {
                notifyFixedAtomRemoved( m_fixedAtom );
                m_fixedAtom = null;
            }

            m_fixedAtom = AtomFactory.createAtom( atomType );

            // Set the value for sigma used in the LJ potential calculations.
            if ( m_movableAtom != null ) {
                m_ljPotentialCalculator.setSigma( SigmaTable.getSigma( getFixedAtomType(), getMovableAtomType() ) );
            }

            // If both atoms exist, set the value of epsilon.
            if ( m_movableAtom != null ) {
                m_ljPotentialCalculator.setEpsilon(
                        InteractionStrengthTable.getInteractionPotential( m_fixedAtom.getType(), m_movableAtom.getType() ) );
            }

            notifyFixedAtomAdded( m_fixedAtom );
            notifyInteractionPotentialChanged();
            notifyFixedAtomDiameterChanged();
            m_fixedAtom.setPosition( 0, 0 );
            resetMovableAtomPos();
        }
    }

    public void setMovableAtomType( AtomType atomType ) {

        if ( m_movableAtom == null || m_movableAtom.getType() != atomType ) {

            if ( !m_settingBothAtomTypes &&
                 ( ( atomType == AtomType.ADJUSTABLE && m_movableAtom.getType() != AtomType.ADJUSTABLE ) ||
                   ( atomType != AtomType.ADJUSTABLE && m_movableAtom.getType() == AtomType.ADJUSTABLE ) ) ) {
                System.err.println( this.getClass().getName() + " - Error: Cannot set just one atom to be adjustable, ignoring request." );
                return;
            }

            ensureValidAtomType( atomType );
            m_bondingState = BONDING_STATE_UNBONDED;

            if ( m_movableAtom != null ) {
                notifyMovableAtomRemoved( m_movableAtom );
                m_movableAtom.removeListener( m_movableAtomListener );
                m_movableAtom = null;
            }

            m_movableAtom = AtomFactory.createAtom( atomType );

            // Register to listen to motion of the movable atom so that we can
            // tell when the user is moving it.
            m_movableAtom.addListener( m_movableAtomListener );

            // Set the value for sigma used in the LJ potential calculations.
            if ( m_movableAtom != null ) {
                m_ljPotentialCalculator.setSigma( SigmaTable.getSigma( getFixedAtomType(), getMovableAtomType() ) );
            }

            // If both atoms exist, set the value of epsilon.
            if ( m_fixedAtom != null ) {
                m_ljPotentialCalculator.setEpsilon(
                        InteractionStrengthTable.getInteractionPotential( m_fixedAtom.getType(), m_movableAtom.getType() ) );
            }

            notifyMovableAtomAdded( m_movableAtom );
            notifyInteractionPotentialChanged();
            notifyMovableAtomDiameterChanged();
            resetMovableAtomPos();
        }
    }

    private void ensureValidAtomType( AtomType atomType ) {
        // Verify that this is a supported value.
        if ( ( atomType != AtomType.NEON ) &&
             ( atomType != AtomType.ARGON ) &&
             ( atomType != AtomType.OXYGEN ) &&
             ( atomType != AtomType.ADJUSTABLE ) ) {

            System.err.println( "Error: Unsupported atom type." );
            assert false;
        }
    }

    public void setBothAtomTypes( AtomType atomType ) {

        if ( m_fixedAtom == null || m_movableAtom == null || m_fixedAtom.getType() != atomType ||
             m_movableAtom.getType() != atomType ) {
            m_settingBothAtomTypes = true;
            setFixedAtomType( atomType );
            setMovableAtomType( atomType );
            m_settingBothAtomTypes = false;
        }
    }

    /**
     * Set the sigma value, a.k.a. the Atomic Diameter Parameter, for the
     * adjustable atom.  This is one of the two parameters that are used
     * for calculating the Lennard-Jones potential.  If an attempt is made to
     * set this value when the adjustable atom is not selected, it is ignored.
     *
     * @param sigma - distance parameter
     */
    public void setAdjustableAtomSigma( double sigma ) {
        if ( ( m_fixedAtom.getType() == AtomType.ADJUSTABLE ) &&
             ( m_movableAtom.getType() == AtomType.ADJUSTABLE ) &&
             ( sigma != m_ljPotentialCalculator.getSigma() ) ) {

            if ( sigma > StatesOfMatterConstants.MAX_SIGMA ) {
                sigma = StatesOfMatterConstants.MAX_SIGMA;
            }
            else if ( sigma < StatesOfMatterConstants.MIN_SIGMA ) {
                sigma = StatesOfMatterConstants.MIN_SIGMA;
            }
            m_ljPotentialCalculator.setSigma( sigma );
            notifyInteractionPotentialChanged();
            m_movableAtom.setPosition( m_ljPotentialCalculator.calculateMinimumForceDistance(), 0 );
            ( (ConfigurableStatesOfMatterAtom) m_fixedAtom ).setRadius( sigma / 2 );
            notifyFixedAtomDiameterChanged();
            ( (ConfigurableStatesOfMatterAtom) m_movableAtom ).setRadius( sigma / 2 );
            notifyMovableAtomDiameterChanged();
        }
    }

    /**
     * Get the value of the sigma parameter that is being used for the motion
     * calculations.  If the atoms are the same, it will be the diameter
     * of one atom.  If they are not, it will be a function of the
     * diameters.
     *
     * @return
     */
    public double getSigma() {
        return m_ljPotentialCalculator.getSigma();
    }

    /**
     * Set the epsilon value, a.k.a. the Interaction Strength Parameter, which
     * is one of the two parameters that describes the Lennard-Jones potential.
     *
     * @param epsilon - interaction strength parameter
     */
    public void setEpsilon( double epsilon ) {

        if ( epsilon < StatesOfMatterConstants.MIN_EPSILON ) {
            epsilon = StatesOfMatterConstants.MIN_EPSILON;
        }
        else if ( epsilon > StatesOfMatterConstants.MAX_EPSILON ) {
            epsilon = StatesOfMatterConstants.MAX_EPSILON;
        }

        if ( ( m_fixedAtom.getType() == AtomType.ADJUSTABLE ) &&
             ( m_movableAtom.getType() == AtomType.ADJUSTABLE ) ) {

            m_ljPotentialCalculator.setEpsilon( epsilon );
            notifyInteractionPotentialChanged();
        }
    }

    /**
     * Get the epsilon value, a.k.a. the Interaction Strength Parameter, which
     * is one of the two parameters that describes the Lennard-Jones potential.
     *
     * @return
     */
    public double getEpsilon() {
        return m_ljPotentialCalculator.getEpsilon();
    }

    public int getBondingState() {
        return m_bondingState;
    }

    //----------------------------------------------------------------------------
    // Other Public Methods
    //----------------------------------------------------------------------------

    /**
     * Reset the model.
     */
    public void reset() {

        if ( m_fixedAtom == null || m_fixedAtom.getType() != DEFAULT_ATOM_TYPE ||
             m_movableAtom == null || m_movableAtom.getType() != DEFAULT_ATOM_TYPE ) {
            setBothAtomTypes( DEFAULT_ATOM_TYPE );
        }
        else {
            resetMovableAtomPos();
        }

        // Make sure we are not paused.
        m_motionPaused = false;
    }

    /**
     * Put the movable atom back to the location where the force is
     * minimized, and reset the velocity and acceleration to 0.
     */
    public void resetMovableAtomPos() {
        if ( m_movableAtom != null ) {
            m_movableAtom.setPosition( m_ljPotentialCalculator.calculateMinimumForceDistance(), 0 );
            m_movableAtom.setVx( 0 );
            m_movableAtom.setAx( 0 );
        }
    }

    public void addListener( Listener listener ) {

        if ( m_listeners.contains( listener ) ) {
            // Don't bother re-adding.
            return;
        }

        m_listeners.add( listener );
    }

    public boolean removeListener( Listener listener ) {
        return m_listeners.remove( listener );
    }

    public void setMotionPaused( boolean paused ) {
        m_motionPaused = paused;
        m_movableAtom.setVx( 0 );
        if ( !paused ) {
            // The atom is being released by the user.  Record the amount of
            // energy that the atom has at this point in time for later use.  The
            // calculation is made be evaluating the force at the current
            // location and multiplying it by the distance to the point where
            // the LJ potential is minimized.  Note that this is not precisely
            // correct, since the potential is not continuous, but is close
            // enough for our purposes.
            m_potentialWhenAtomReleased =
                    m_ljPotentialCalculator.calculatePotentialEnergy( m_movableAtom.getPositionReference().distance( m_fixedAtom.getPositionReference() ) );
        }
    }

    public boolean getMotionPaused() {
        return m_motionPaused;
    }

    /**
     * Release the bond that exists between the two atoms (if there is one).
     */
    public void releaseBond() {
        if ( m_bondingState == BONDING_STATE_BONDING ) {
            // A bond is in the process of forming, so reset everything that
            // is involved in the bonding process.
            m_vibrationCounter = 0;
        }
        m_bondingState = BONDING_STATE_UNBONDED;
    }

    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------

    private void handleClockTicked( ClockEvent clockEvent ) {

        try {
            m_shadowMovableAtom = (StatesOfMatterAtom) m_movableAtom.clone();
        }
        catch ( CloneNotSupportedException e ) {
            e.printStackTrace();
            return;
        }
        updateTimeStep();

        // Update the forces and motion of the atoms.
        for ( int i = 0; i < CALCULATIONS_PER_TICK; i++ ) {

            // Execute the force calculation.
            updateForces();

            // Update the motion information (unless the atoms are bonded).
            if ( m_bondingState != BONDING_STATE_BONDED ) {
                updateAtomMotion();
            }
        }

        // Update the atom that is visible to the view.
        syncMovableAtomWithDummy();

        // Handle inter-atom bonding.
        if ( m_movableAtom.getType() == AtomType.OXYGEN && m_fixedAtom.getType() == AtomType.OXYGEN ) {
            switch( m_bondingState ) {

                case BONDING_STATE_UNBONDED:
                    if ( ( m_movableAtom.getVx() > THRESHOLD_VELOCITY ) &&
                         ( m_movableAtom.getPositionReference().distance( m_fixedAtom.getPositionReference() ) < m_fixedAtom.getRadius() * 2.5 ) ) {
                        // The atoms are close together and the movable one is
                        // starting to move away, which is the point at which we
                        // consider the bond to start forming.
                        m_bondingState = BONDING_STATE_BONDING;
                        startFixedAtomVibration();
                    }
                    break;

                case BONDING_STATE_BONDING:
                    if ( m_attractiveForce > m_repulsiveForce ) {
                        // A bond is forming and the force just exceeded the
                        // repulsive force, meaning that the atom is starting
                        // to pass the bottom of the well.
                        m_movableAtom.setAx( 0 );
                        m_movableAtom.setVx( 0 );
                        m_minPotentialDistance = m_ljPotentialCalculator.calculateMinimumForceDistance();
                        m_bondedOscillationRightDistance = m_minPotentialDistance +
                                                           BONDED_OSCILLATION_PROPORTION * m_movableAtom.getRadius();
                        m_bondedOscillationLeftDistance =
                                approximateEquivalentPotentialDistance( m_bondedOscillationRightDistance );
                        m_bondingState = BONDING_STATE_BONDED;
                        stepFixedAtomVibration();
                    }
                    break;

                case BONDING_STATE_BONDED:
                    // Override the atom motion calculations and cause the atom to
                    // oscillate a fixed distance from the bottom of the well.
                    // This is necessary because otherwise we tend to have an
                    // aliasing problem where it appears that the atom oscillates
                    // for a while, then damps out, then starts up again.
                    m_movableAtom.setAx( 0 );
                    m_movableAtom.setVx( 0 );
                    if ( m_movableAtom.getPositionReference().getX() > m_minPotentialDistance ) {
                        m_movableAtom.setPosition( m_bondedOscillationLeftDistance, 0 );
                    }
                    else {
                        m_movableAtom.setPosition( m_bondedOscillationRightDistance, 0 );
                    }

                    if ( isFixedAtomVibrating() ) {
                        stepFixedAtomVibration();
                    }
                    break;

                default:
                    System.err.println( this.getClass().getName() + " - Error: Unrecognized bonding state." );
                    assert false;
                    m_bondingState = BONDING_STATE_UNBONDED;
                    break;
            }
        }
    }

    private void updateTimeStep() {
        m_timeStep = m_clock.getDt() / 1000 / CALCULATIONS_PER_TICK;
    }

    /**
     *
     */
    private void syncMovableAtomWithDummy() {
        m_movableAtom.setAx( m_shadowMovableAtom.getAx() );
        m_movableAtom.setVx( m_shadowMovableAtom.getVx() );
        m_movableAtom.setPosition( m_shadowMovableAtom.getX(), m_shadowMovableAtom.getY() );
    }

    private void updateForces() {

        double distance = m_shadowMovableAtom.getPositionReference().distance( new Point2D.Double( 0, 0 ) );

        if ( distance < ( m_fixedAtom.getRadius() + m_movableAtom.getRadius() ) / 8 ) {
            // The atoms are too close together, and calculating the force
            // will cause unusable levels of speed later, so we limit it.
            distance = ( m_fixedAtom.getRadius() + m_movableAtom.getRadius() ) / 8;
        }

        // Calculate the force.  The result should be in newtons.
        m_attractiveForce = m_ljPotentialCalculator.calculateAttractiveLjForce( distance );
        m_repulsiveForce = m_ljPotentialCalculator.calculateRepulsiveLjForce( distance );
    }

    /**
     * Update the position, velocity, and acceleration of the dummy movable atom.
     */
    private void updateAtomMotion() {

        double mass = m_shadowMovableAtom.getMass() * 1.6605402E-27;  // Convert mass to kilograms.
        double acceleration = ( m_repulsiveForce - m_attractiveForce ) / mass;

        // Update the acceleration for the movable atom.  We do this
        // regardless of whether movement is paused so that the force vectors
        // can be shown appropriately if the user moves the atoms.
        m_shadowMovableAtom.setAx( acceleration );

        if ( !m_motionPaused ) {
            // Update the position and velocity of the atom.
            m_shadowMovableAtom.setVx( m_shadowMovableAtom.getVx() + ( acceleration * m_timeStep ) );
            double xPos = m_shadowMovableAtom.getPositionReference().getX() + ( m_shadowMovableAtom.getVx() * m_timeStep );
            m_shadowMovableAtom.setPosition( xPos, 0 );
        }
    }

    private void startFixedAtomVibration() {
        m_vibrationCounter = VIBRATION_COUNTER_RESET_VALUE;
    }

    private void stepFixedAtomVibration() {
        if ( m_vibrationCounter > 0 ) {
            double vibrationScaleFactor = 1;
            if ( m_vibrationCounter < VIBRATION_COUNTER_RESET_VALUE / 4 ) {
                // In the last part of the vibration, starting to wind it down.
                vibrationScaleFactor = (double) m_vibrationCounter / ( (double) VIBRATION_COUNTER_RESET_VALUE / 4 );
            }
            if ( m_fixedAtom.getPositionReference().getX() != 0 ) {
                // Go back to the original position every other time.
                m_fixedAtom.setPosition( 0, 0 );
            }
            else {
                // Move some distance from the original position based on the
                // energy contained at the time of bonding.  The
                // multiplication factor in the equation below is empirically
                // determined to look good on the screen.
                double xPos = ( m_rand.nextDouble() * 2 - 1 ) * m_potentialWhenAtomReleased * 5e21 * vibrationScaleFactor;
                double yPos = ( m_rand.nextDouble() * 2 - 1 ) * m_potentialWhenAtomReleased * 5e21 * vibrationScaleFactor;
                m_fixedAtom.setPosition( xPos, yPos );
            }

            // Decrement the vibration counter.
            m_vibrationCounter--;
        }
        else if ( m_fixedAtom.getPositionReference().getX() != 0 ||
                  m_fixedAtom.getPositionReference().getY() != 0 ) {
            m_fixedAtom.setPosition( 0, 0 );
        }
    }

    /**
     * This is a highly specialized function that is used for figuring out
     * the inter-atom distance at which the value of the potential on the left
     * side of the of the min of the LJ potential curve is equal to that at the
     * given distance to the right of the min of the LJ potential curve.
     *
     * @param distance - inter-atom distance, must be greater than the point at
     * which the potential is at the minimum value.
     * @return
     */
    private static final int MAX_APPROXIMATION_ITERATIONS = 100;

    private double approximateEquivalentPotentialDistance( double distance ) {

        if ( distance < m_ljPotentialCalculator.calculateMinimumForceDistance() ) {
            System.err.println( this.getClass().getName() + "- Error: Distance value out of range." );
            return 0;
        }

        // Iterate by a fixed amount until a reasonable value is found.
        double totalSpanDistance = distance - m_ljPotentialCalculator.getSigma();
        double distanceChangePerIteration = totalSpanDistance / MAX_APPROXIMATION_ITERATIONS;
        double targetPotential = m_ljPotentialCalculator.calculateLjPotential( distance );
        double equivalentPotentialDistance = m_ljPotentialCalculator.calculateMinimumForceDistance();
        for ( int i = 0; i < MAX_APPROXIMATION_ITERATIONS; i++ ) {
            if ( m_ljPotentialCalculator.calculateLjPotential( equivalentPotentialDistance ) > targetPotential ) {
                // We've crossed over to where the potential is less negative.
                // Close enough.
                break;
            }
            equivalentPotentialDistance -= distanceChangePerIteration;
        }

        return equivalentPotentialDistance;
    }

    private boolean isFixedAtomVibrating() {
        return m_vibrationCounter > 0;
    }

    private void notifyFixedAtomAdded( StatesOfMatterAtom atom ) {
        for ( Listener listener : m_listeners ) {
            ( listener ).fixedAtomAdded( atom );
        }
    }

    private void notifyMovableAtomAdded( StatesOfMatterAtom particle ) {
        for ( Listener listener : m_listeners ) {
            listener.movableAtomAdded( particle );
        }
    }

    private void notifyFixedAtomRemoved( StatesOfMatterAtom particle ) {
        for ( Listener listener : m_listeners ) {
            listener.fixedAtomRemoved( particle );
        }
    }

    private void notifyMovableAtomRemoved( StatesOfMatterAtom atom ) {
        for ( Listener listener : m_listeners ) {
            listener.movableAtomRemoved( atom );
        }
    }

    private void notifyInteractionPotentialChanged() {
        for ( Listener listener : m_listeners ) {
            listener.interactionPotentialChanged();
        }
    }

    private void notifyFixedAtomDiameterChanged() {
        for ( Listener listener : m_listeners ) {
            listener.fixedAtomDiameterChanged();
        }
    }

    private void notifyMovableAtomDiameterChanged() {
        for ( Listener listener : m_listeners ) {
            listener.movableAtomDiameterChanged();
        }
    }

    public ConstantDtClock getClock() {
        return m_clock;
    }

    //------------------------------------------------------------------------
    // Inner Interfaces and Classes
    //------------------------------------------------------------------------

    public static interface Listener {

        /**
         * Inform listeners that the model has been reset.
         */
        public void fixedAtomAdded( StatesOfMatterAtom atom );

        public void movableAtomAdded( StatesOfMatterAtom atom );

        public void fixedAtomRemoved( StatesOfMatterAtom atom );

        public void movableAtomRemoved( StatesOfMatterAtom atom );

        public void interactionPotentialChanged();

        public void fixedAtomDiameterChanged();

        public void movableAtomDiameterChanged();
    }

    public static class Adapter implements Listener {
        public void fixedAtomAdded( StatesOfMatterAtom atom ) {
        }

        public void movableAtomAdded( StatesOfMatterAtom atom ) {
        }

        public void fixedAtomRemoved( StatesOfMatterAtom atom ) {
        }

        public void movableAtomRemoved( StatesOfMatterAtom atom ) {
        }

        public void interactionPotentialChanged() {
        }

        public void fixedAtomDiameterChanged() {
        }

        public void movableAtomDiameterChanged() {
        }
    }
}
