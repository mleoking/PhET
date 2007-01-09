/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.util.RandomUtils;

/**
 * BohrModel is the Bohr model of the hydrogen atom.
 * <p>
 * Physical representation:
 * Electron orbiting a proton.
 * Each orbit corresponds to a different electron state.
 * See createOrbitRadii for details on how orbit radii are calculated.
 * <p>
 * Collision behavior:
 * Alpha particles are repelled by the electron using a Rutherford Scattering algorithm.
 * Photons may be absorbed (see below) if they collide with the electron.
 * <p>
 * Absorption behavior:
 * Photons that match the transition wavelength of the electron's state are 
 * absorbed with some probability. Other photons are not absorbed or affected.
 * <p>
 * Emission behavior:
 * Spontaneous emission of a photon takes the electron to a lower state,
 * and the photon emitted is has the transition wavelength that corresponds
 * to the current and new state. Transition to each lower state is equally 
 * likely. 
 * Stimulated emission of a photon occurs when a photon that hits the 
 * electron, and the photon's wavelength corresponds to a wavelength 
 * the could have been absorbed in a lower state.  In this case, the 
 * colliding photon is not aborbed, but a new photon is emitted with 
 * the same wavelength, and the electron moves to the lower state.
 */
public class BohrModel extends AbstractHydrogenAtom {

    //----------------------------------------------------------------------------
    // Debug
    //----------------------------------------------------------------------------
    
    /* used to turn off aspects of this model from the Developer Controls dialog */
    public static boolean DEBUG_ABSORPTION_ENABLED = true;
    public static boolean DEBUG_SPONTANEOUS_EMISSION_ENABLED = true;
    public static boolean DEBUG_STIMULATED_EMISSION_ENABLED = true; 
    
    /* enabled debugging output */
    private static boolean DEBUG_OUTPUT_ENABLED = false;
    
    //----------------------------------------------------------------------------
    // Public class data
    //----------------------------------------------------------------------------
    
    /* minimum time (clock time change) that electron stays in a state before emission can occur */
    public static int MIN_TIME_IN_STATE = 50;
    
    //----------------------------------------------------------------------------
    // Private class data
    //----------------------------------------------------------------------------
    
    /* Radius of each orbit supported by this model */
    private static final double[] ORBIT_RADII = { 15, 44, 81, 124, 174, 233 };
    
    /* probability that photon will be absorbed */
    private static final double PHOTON_ABSORPTION_PROBABILITY = 0.5; // 1.0 = 100%
    
    /* probability that photon will be emitted */
    private static final double PHOTON_SPONTANEOUS_EMISSION_PROBABILITY = 0.1; // 1.0 = 100%
    
    /* Probability of stimulated emission should be the same as absorption,
     * but we test for it first so it seems to happen way more often.
     * So we've lowered its probability.
     */
    private static final double PHOTON_STIMULATED_EMISSION_PROBABILITY = 0.25;
    
    /* change in orbit angle per dt for ground state orbit */
    private static final double ELECTRON_ANGLE_DELTA = Math.toRadians( 10 );
    
    /* wavelengths must be less than this close to be considered equal */
    private static final double WAVELENGTH_CLOSENESS_THRESHOLD = 0.5;
    
    /* How close an emitted photon is placed to the photon that causes stimulated emission */
    private static final double STIMULATED_EMISSION_X_OFFSET = 10;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // electron state
    private int _electronState;
    // time that the electron has been in its current state
    private double _timeInState;
    // current angle of electron
    private double _electronAngle;
    // offset of the electron relative to atom's center
    private Point2D _electronOffset;
    
    // random number generator for absorption probability
    private Random _randomAbsorption;
    // random number generator for emission probability
    private Random _randomSpontaneousEmission;
    // random number generator for stimulated emission probability
    private Random _randomStimulatedEmission;
    // random number generator for electron state selection
    private Random _randomState;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BohrModel( Point2D position ) {
        super( position, 0 /* orientation */ );
        
        _electronState = GROUND_STATE;
        _timeInState = 0;
        _electronAngle = RandomUtils.nextAngle();
        _electronOffset = new Point2D.Double();
        
        _randomAbsorption = new Random();
        _randomSpontaneousEmission = new Random();
        _randomStimulatedEmission = new Random();
        _randomState = new Random();
        
        updateElectronOffset();
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the electron's state.
     * @return int
     */
    public int getElectronState() {
        return _electronState;
    }
    
    /*
     * Sets the electron's state.
     * @param state
     */
    protected void setElectronState( int state ) {
        assert( state >= GROUND_STATE && state <= GROUND_STATE + getNumberOfStates() - 1 );
        if ( state != _electronState ) {
            _electronState = state;
            _timeInState = 0;
            notifyObservers( PROPERTY_ELECTRON_STATE );
        }
    }
    
    /**
     * Gets the radius for a specified state.
     * @param state
     * @return
     */
    public static double getOrbitRadius( int state ) {
        return ORBIT_RADII[ state - GROUND_STATE ];
    }
    
    /**
     * Gets the electron's offset, relative to the atom's center.
     * @return Point2D
     */
    public Point2D getElectronOffset() {
        return _electronOffset;
    }
    
    /**
     * Gets the current angle of the electron.
     * The orbit radius and this angle determine the electron's offset
     * in Polar coordinates.
     * 
     * @return
     */
    public double getElectronAngle() {
        return _electronAngle;
    }
    
    /**
     * Gets the change in electron angle per unit of time.
     * @return double
     */
    protected double getElectronAngleDelta() {
       return ELECTRON_ANGLE_DELTA;
    }
    
    /**
     * Gets the radisu of the electron's orbit.
     * The orbit radius and the electron's angle determine the electron's offset
     * in Polar coordinates.
     * 
     * @return
     */
    public double getElectronOrbitRadius() {
        return getOrbitRadius( _electronState );
    }
    
    //----------------------------------------------------------------------------
    // AbstractHydrogenAtom overrides
    //----------------------------------------------------------------------------
    
    /**
     * Gets the number of electron states that the model supports.
     * This is the same as the number of orbits.
     * @return int
     */
    public static int getNumberOfStates() {
        return ORBIT_RADII.length;
    }
    
    /**
     * Gets the transition wavelengths for a specified state.
     * @param state
     * @return double[]
     */
    public double[] getTransitionWavelengths( int state ) {
        double[] transitionWavelengths = null;
        int maxState = getGroundState() + getNumberOfStates() - 1;
        if ( state < maxState ) {
            transitionWavelengths = new double[ maxState - state ];
            for ( int i = 0; i < transitionWavelengths.length; i++ ) {
                transitionWavelengths[i] = getWavelengthAbsorbed( state, state + i + 1 );
            }
        }
        return transitionWavelengths;
    }
    
    //----------------------------------------------------------------------------
    // utilities
    //----------------------------------------------------------------------------
    
    /**
     * Gets the electron's position in world coordinates.
     * This is the electron's offset adjusted by the atom's position.
     */
    protected Point2D getElectronPosition() {
        double x = getX() + _electronOffset.getX();
        double y = getY() + _electronOffset.getY();
        return new Point2D.Double( x, y );
    }
    
    /*
     * Updates the electron's offset to match its current orbit and angle.
     * This is essentially a conversion from Cartesian to Polar coordinates.
     */
    private void updateElectronOffset() {
        double radius = getOrbitRadius( _electronState );
        double x = radius * Math.sin( _electronAngle );
        double y = radius * Math.cos( _electronAngle );
        _electronOffset.setLocation( x, y );
        notifyObservers( PROPERTY_ELECTRON_OFFSET );
    }
    
    /**
     * Creates radii for N orbits.
     * This is the physically correct way to specify the orbits.
     * In this sim, we use distorted orbits, so this method is not used.
     * We keep it here for historical purposes.
     * 
     * @param numberOfOrbits
     * @param groundOrbitRadius
     * @return double[] of orbits
     */
    public static double[] createOrbitRadii( int numberOfOrbits, double groundOrbitRadius ) {
        double[] radii = new double[numberOfOrbits];
        for ( int n = 1; n <= numberOfOrbits; n++ ) {
            radii[n - 1] = n * n * groundOrbitRadius;
        }
        return radii;
    }
    
    /**
     * Gets the set of wavelengths that cause a state transition.
     * When firing white light, the gun preference to firing these wavelengths
     * so that the probability of seeing a photon absorbed is higher.
     * 
     * @param minWavelength
     * @param maxWavelength
     * @return double[]
     */
    public static double[] getTransitionWavelengths( double minWavelength, double maxWavelength ) {
        
        // Create the set of wavelengths, include only those between min and max.
        ArrayList wavelengths = new ArrayList();
        int n = getNumberOfStates();
        int g = getGroundState();
        for ( int i = g; i < g + n - 1; i++ ) {
            for ( int j = i + 1; j < g + n; j++ ) {
                double wavelength = getWavelengthAbsorbed( i, j );
                if ( wavelength >= minWavelength && wavelength <= maxWavelength ) {
                    wavelengths.add( new Double( wavelength ) );
                }
            }
        }
        
        // Convert to double[]
        double[] array = new double[wavelengths.size()];
        for ( int i = 0; i < wavelengths.size(); i++ ) {
            array[i] = ( (Double) wavelengths.get( i ) ).doubleValue();
//            System.out.println( "spectrum[" + i + "]=" + spectrum[i] );//XXX
        }
        return array;
    }
    
    //----------------------------------------------------------------------------
    // Photon absorption and emission
    //----------------------------------------------------------------------------
    
    /**
     * Determines whether a photon collides with this atom.
     * In this case, we treat the photon and electron as points, 
     * and see if the points are close enough to cause a collision.
     * 
     * @param photon
     * @return true or false
     */
    protected boolean collides( Photon photon ) {
        Point2D electronPosition = getElectronPosition();
        Point2D photonPosition = photon.getPositionRef();
        return pointsCollide( electronPosition, photonPosition, COLLISION_CLOSENESS );
    }
    
    /*
     * Attempts to absorb the specified photon.
     * 
     * @param photon
     * @return true or false
     */
    private boolean absorbPhoton( Photon photon ) {

        boolean absorbed = false;
        
        if ( !DEBUG_ABSORPTION_ENABLED ) {
            return absorbed;
        }
        
        if ( _timeInState >= MIN_TIME_IN_STATE && !photon.isEmitted() ) {

            // Do the photon and electron collide?
            final boolean collide = collides( photon );

            if ( collide ) {

                // Can the photon be absorbed?...
                boolean canAbsorb = false;
                int newState = 0;
                final int maxState = GROUND_STATE + getNumberOfStates() - 1;
                final double photonWavelength = photon.getWavelength();
                for ( int n = _electronState + 1; n <= maxState && !canAbsorb; n++ ) {
                    final double transitionWavelength = getWavelengthAbsorbed( _electronState, n );
                    if ( closeEnough( photonWavelength, transitionWavelength ) ) {
                        canAbsorb = true;
                        newState = n;
                    }
                }

                // Absorb the photon with some probability...
                if ( canAbsorb && willAbsorbPhoton() ) {

                    // absorb photon
                    absorbed = true;
                    PhotonAbsorbedEvent event = new PhotonAbsorbedEvent( this, photon );
                    firePhotonAbsorbedEvent( event );

                    if ( DEBUG_OUTPUT_ENABLED ) {
                        System.out.println( "BohrModel: absorbed photon, wavelength=" + photonWavelength );
                    }

                    // move electron to new state
                    setElectronState( newState );
                }
            }
        }
        
        return absorbed;
    }
    
    /*
     * Probabilistically determines whether to absorb a photon.
     * 
     * @return true or false
     */
    protected boolean willAbsorbPhoton() {
        return _randomAbsorption.nextDouble() < PHOTON_ABSORPTION_PROBABILITY;
    }
    
    /*
     * Emits a photon from the electron's location, at a random orientation.
     * This is also known as "spontaneous emission".
     */
    private void emitPhoton() {
        
        if ( !DEBUG_SPONTANEOUS_EMISSION_ENABLED ) {
            return;
        }
        
        if ( _timeInState >= MIN_TIME_IN_STATE && _electronState > GROUND_STATE ) {
            
            if ( willSpontaneousEmitPhoton() ) {
                
                int newState = chooseLowerElectronState();
                if ( newState == -1 ) {
                    // For some subclasses, there may be no valid transition.
                    return;
                }
                
                // New photon's properties
                Point2D position = getSpontaneousEmissionPosition();
                double orientation = RandomUtils.nextAngle();
                double speed = HAConstants.PHOTON_INITIAL_SPEED;
                double wavelength = getWavelengthEmitted( _electronState, newState );
                
                // Create and emit a photon
                Photon photon = new Photon( wavelength, position, orientation, speed, true /* emitted */ );
                PhotonEmittedEvent event = new PhotonEmittedEvent( this, photon );
                firePhotonEmittedEvent( event );
                
                if ( DEBUG_OUTPUT_ENABLED ) {
                    System.out.println( "BohrModel: emitted photon, wavelength=" + wavelength );
                }
                
                // move electron to new state
//              System.out.println( "BohrModel.emitPhoton " + _electronState + "->" + newState );//XXX
                setElectronState( newState );
            }
        }
    }
    
    /*
     * Probabilistically determines whether not the atom will spontaneously emit a photon.
     * 
     * @return true or false
     */
    protected boolean willSpontaneousEmitPhoton() {
        return _randomSpontaneousEmission.nextDouble() < PHOTON_SPONTANEOUS_EMISSION_PROBABILITY;
    }
    
    /*
     * Chooses a new state for the electron.
     * The state chosen is a lower state.
     * This is used when moving to a lower state, during spontaneous emission.
     * Each lower state has the same probability of being chosen.
     * 
     * @return int positive state number, -1 if there is no state could be chosen
     */
    protected int chooseLowerElectronState() {
        int newState = GROUND_STATE;
        if ( _electronState > GROUND_STATE  + 1 ) {
            newState = GROUND_STATE + _randomState.nextInt( _electronState - GROUND_STATE );
        }
        return newState;
    }
    
    /*
     * Gets the position of a photon created via spontaneous emission.
     * The default behavior is to create the photon at the electron's position.
     * 
     * @return Point2D
     */
    protected Point2D getSpontaneousEmissionPosition() {
        return getElectronPosition();
    }
    
    /*
     * Attempts to stimulate emission with a specified photon.
     * <p>
     * Definition of stimulated emission, for state m < n:
     * If an electron in state n gets hit by a photon whose absorption
     * would cause a transition from state m to n, then the electron
     * should drop to state m and emit a photon.  The emitted photon
     * should be the same wavelength and be traveling alongside the 
     * original photon.
     * 
     * @param photon
     */
    private void stimulateEmission( Photon photon ) {
        
        if ( !DEBUG_STIMULATED_EMISSION_ENABLED ) {
            return;
        }
        
        if (  _timeInState >= MIN_TIME_IN_STATE && _electronState > GROUND_STATE && !photon.isEmitted() ) {
            
            // Do the photon and electron collide?
            final boolean collide = collides( photon );
            
            if ( collide ) {
                
                // Can this photon stimulate emission?
                boolean canStimulateEmission = false;
                final double photonWavelength = photon.getWavelength();
                int newState = 0;
                for ( int m = GROUND_STATE; m < _electronState && !canStimulateEmission; m++ ) {
                    final double transitionWavelength = getWavelengthAbsorbed( m, _electronState );
                    if ( closeEnough( photonWavelength, transitionWavelength ) ) {
                        canStimulateEmission = true;
                        newState = m;
                    }
                }
                
                if ( ! isaLegalTransition( _electronState, newState ) ) {
                    return;
                }
                
                // Emit a photon with some probability...
                if ( canStimulateEmission && willStimulatedEmitPhoton() ) {
                    // This algorithm assumes that photons are moving vertically from bottom to top.
                    assert( photon.getOrientation() == Math.toRadians( -90 ) );
                    
                    // New photon's properties
                    double wavelength = photon.getWavelength();
                    final double x = photon.getX() + STIMULATED_EMISSION_X_OFFSET;
                    final double y = photon.getY();
                    Point2D position = new Point2D.Double( x, y );
                    double orientation = photon.getOrientation();
                    double speed = HAConstants.PHOTON_INITIAL_SPEED;
                    
                    // Create and emit a photon
                    Photon emittedPhoton = new Photon( wavelength, position, orientation, speed, true /* emitted */ );
                    PhotonEmittedEvent event = new PhotonEmittedEvent( this, emittedPhoton );
                    firePhotonEmittedEvent( event );
                    
                    if ( DEBUG_OUTPUT_ENABLED ) {
                        System.out.println( "BohrModel: stimulated emission of photon, wavelength=" + wavelength );
                    }
                    
                    // move electron to new state
                    setElectronState( newState );
                }
                
            }
        }
    }
    
    /*
     * Probabilistically determines whether the atom will emit a photon via stimulated emission.
     * 
     * @return true or false
     */
    protected boolean willStimulatedEmitPhoton() {
        return _randomStimulatedEmission.nextDouble() < PHOTON_STIMULATED_EMISSION_PROBABILITY;
    }
    
    /*
     * Determines if a proposed state transition is legal.
     * This is true by default if the 2 states are different.
     * 
     * @param nOld
     * @param nNew
     */
    protected boolean isaLegalTransition( final int nOld, final int nNew ) {
        return ( nOld != nNew );
    }
    
    /**
     * Gets the wavelength that must be absorbed for the electron to transition
     * from state nOld to state nNew, where nOld < nNew.
     * This algorithm assumes that the ground state is 1.
     * 
     * @param nOld
     * @param nNew
     */
    public static double getWavelengthAbsorbed( int nOld, int nNew ) {
        assert ( GROUND_STATE == 1 );
        assert ( nOld < nNew );
        assert ( nOld > 0 );
        assert ( nNew <= GROUND_STATE + getNumberOfStates() - 1 );
        return 1240.0 / ( 13.6 * ( ( 1.0 / ( nOld * nOld ) ) - ( 1.0 / ( nNew * nNew ) ) ) );
    }
   
    /*
     * Gets the wavelength that is emitted when the electron transitions
     * from state nOld to state nNew, where newNew < nOld.
     * 
     * @param nOld
     * @param nNew
     * @return
     */
    private static double getWavelengthEmitted( int nOld, int nNew ) {
        return getWavelengthAbsorbed( nNew, nOld );
    }
    
    /*
     * Determines if two wavelengths are "close enough" 
     * for the purposes of absorption and emission.
     * 
     * @param w1
     * @param w2
     * @return true or false
     */
    private boolean closeEnough( double w1, double w2 ) {
        return( Math.abs( w1 - w2 ) < WAVELENGTH_CLOSENESS_THRESHOLD );
    }
    
    //----------------------------------------------------------------------------
    // AbstractHydrogenAtom implementation
    //----------------------------------------------------------------------------
    
    /**
     * Moves a photon.
     * <p>
     * A collision occurs when a photon comes "close" to the electron.
     * If a collision occurs, there is a probability of absorption.
     * If there is no absorption, then there may be stimulated emission.
     * <p>
     * Nothing is allowed to happen until the electron has been in its 
     * current state for a minimum time period.
     * 
     * @param photon
     */
    public void movePhoton( Photon photon, double dt ) {
        boolean absorbed = absorbPhoton( photon );
        if ( !absorbed ) {
            stimulateEmission( photon );
            super.movePhoton( photon, dt );
        }
    }
    
    /**
     * Moves an alpha particle using a Rutherford Scattering algorithm.
     * 
     * @param alphaParticle
     * @param dt
     */
    public void moveAlphaParticle( AlphaParticle alphaParticle, double dt ) {
        RutherfordScattering.moveParticle( this, alphaParticle, dt );
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /**
     * Advances the model when the clock ticks.
     * @param dt
     */
    public void stepInTime( double dt ) {

        // Keep track of how long the electron has been in its current state.
        _timeInState += dt;
        
        // Advance the electron along its orbit
        _electronAngle = calculateNewElectronAngle( dt );
        updateElectronOffset();

        // Attempt to emit a photon
        emitPhoton();
    }
    
    /**
     * Calculates the new electron angle for some time step.
     * Subclasses may override this to produce different oscillation behavior.
     * 
     * @param dt
     * @return double
     */
    protected double calculateNewElectronAngle( double dt ) {
        double deltaAngle = dt * ( ELECTRON_ANGLE_DELTA / ( _electronState * _electronState ) );
        return _electronAngle - deltaAngle; // clockwise
    }
}
