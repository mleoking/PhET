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
import java.util.Random;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.util.RandomUtils;


public class BohrModel extends AbstractHydrogenAtom {

    //----------------------------------------------------------------------------
    // Debug
    //----------------------------------------------------------------------------
    
    /* used to turn off aspects of this model from the Developer Controls dialog */
    public static boolean DEBUG_ASBORPTION_ENABLED = true;
    public static boolean DEBUG_EMISSION_ENABLED = true;
    public static boolean DEBUG_STIMULATED_EMISSION_ENABLED = true; 
    
    /* enabled debugging output */
    private static boolean DEBUG_OUTPUT_ENABLED = false;
    
    //----------------------------------------------------------------------------
    // Private class data
    //----------------------------------------------------------------------------
    
    /* Ground state */
    private static final int GROUND_STATE = 1;
    
    /* Radius of each orbit supported by this model */
    private static final double[] ORBIT_RADII = { 15, 44, 81, 124, 174, 233 };
    
    /* probability that photon will be absorbed */
    private static final double PHOTON_ABSORPTION_PROBABILITY = 0.5; // 1.0 = 100%
    
    /* probability that photon will be emitted */
    private static final double PHOTON_EMISSION_PROBABILITY = 0.1; // 1.0 = 100%
    
    /* probability of stimulated emission should be the same as absorption */
    private static final double PHOTON_STIMULATED_EMISSION_PROBABILITY = PHOTON_ABSORPTION_PROBABILITY;
    
    /* change in orbit angle per dt */
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
    // current angle of electron
    private double _electronAngle;
    // offset of the electron relative to atom's center
    private Point2D _electronOffset;
    
    // random number generator for absorption probability
    private Random _randomAbsorption;
    // random number generator for emission probability
    private Random _randomEmission;
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
        _electronAngle = RandomUtils.nextAngle();
        _electronOffset = new Point2D.Double();
        
        _randomAbsorption = new Random();
        _randomEmission = new Random();
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
    private void setElectronState( int state ) {
        assert( state >= GROUND_STATE && state <= GROUND_STATE + getNumberOfStates() - 1 );
        if ( state != _electronState ) {
            _electronState = state;
            notifyObservers( PROPERTY_ELECTRON_STATE );
        }
    }
    
    /**
     * Gets the number of electron states that the model supports.
     * This is the same as the number of orbits.
     * @return int
     */
    public static int getNumberOfStates() {
        return ORBIT_RADII.length;
    }
    
    /**
     * Gets the ground state.
     * @return
     */
    public static int getGroundState() {
        return GROUND_STATE;
    }
    
    /**
     * Gets the radius for a specified state.
     * @param state
     * @return
     */
    public double getOrbitRadius( int state ) {
        return ORBIT_RADII[ state - GROUND_STATE ];
    }
    
    /**
     * Gets the electron's offset, relative to the atom's center.
     * @return Point2D
     */
    public Point2D getElectronOffset() {
        return _electronOffset;
    }
    
    //----------------------------------------------------------------------------
    // utilities
    //----------------------------------------------------------------------------
    
    /*
     * Gets the electron's position in world coordinates.
     * This is the electron's offset adjusted by the atom's position.
     */
    private Point2D getElectronPosition() {
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
    
    //----------------------------------------------------------------------------
    // Photon absorption and emission
    //----------------------------------------------------------------------------
    
    /*
     * Attempts to absorb the specified photon.
     * 
     * @param photon
     * @return true or false
     */
    private boolean absorbPhoton( Photon photon ) {

        boolean absorbed = false;
        
        if ( !DEBUG_ASBORPTION_ENABLED ) {
            return absorbed;
        }
        
        if ( !photon.isEmitted() ) {

            // Do the photon and electron collide?
            Point2D electronPosition = getElectronPosition();
            Point2D photonPosition = photon.getPosition();
            final boolean collide = pointsCollide( electronPosition, photonPosition, ABSORPTION_CLOSENESS );

            if ( collide ) {

                // Can the photon be absorbed?...
                boolean canAbsorb = false;
                int newState = 0;
                final int maxState = GROUND_STATE + getNumberOfStates() - 1;
                final double photonWavelength = photon.getWavelength();
                for ( int n = _electronState + 1; n <= maxState && !canAbsorb; n++ ) {
                    final double transitionWavelength = getWavelengthAbsorbed( _electronState, n );
                    if ( close( photonWavelength, transitionWavelength ) ) {
                        canAbsorb = true;
                        newState = n;
                    }
                }

                // Absorb the photon with some probability...
                if ( canAbsorb && _randomAbsorption.nextDouble() < PHOTON_ABSORPTION_PROBABILITY ) {

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
     * Emits a photon from the electron's location, at a random orientation.
     */
    private void emitPhoton() {
        
        if ( !DEBUG_EMISSION_ENABLED ) {
            return;
        }
        
        if ( _electronState > GROUND_STATE ) {
            if ( _randomEmission.nextDouble() < PHOTON_EMISSION_PROBABILITY ) {
                
                // Randomly pick a new state, each state has equal probability.
                final int newState = GROUND_STATE + (int)( _randomState.nextDouble() * ( _electronState - GROUND_STATE ) );
                
                // New photon's properties
                Point2D position = getElectronPosition();
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
                setElectronState( newState );
            }
        }
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
        
        if ( _electronState > GROUND_STATE && !photon.isEmitted() ) {
            
            // Do the photon and electron collide?
            Point2D electronPosition = getElectronPosition();
            Point2D photonPosition = photon.getPosition();
            final boolean collide = pointsCollide( electronPosition, photonPosition, ABSORPTION_CLOSENESS );
            
            if ( collide ) {
                
                // Can this photon stimulate emission?
                boolean stimulatesEmission = false;
                final double photonWavelength = photon.getWavelength();
                int newState = 0;
                for ( int m = GROUND_STATE; m < _electronState && !stimulatesEmission; m++ ) {
                    final double transitionWavelength = getWavelengthAbsorbed( m, _electronState );
                    if ( close( photonWavelength, transitionWavelength ) ) {
                        stimulatesEmission = true;
                        newState = m;
                    }
                }
                
                // Emit a photon with some probability...
                if ( stimulatesEmission && _randomStimulatedEmission.nextDouble() < PHOTON_STIMULATED_EMISSION_PROBABILITY ) {
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
    
    /**
     * Gets the wavelength that must be absorbed for the 
     * electron to transition from state m to state n, where m < n.
     * This algorithm assumes that the ground state is 1.
     * 
     * @param m
     * @param n
     */
    public static double getWavelengthAbsorbed( int m, int n ) {
        assert ( GROUND_STATE == 1 );
        assert ( m < n );
        assert ( m > 0 );
        assert ( n <= GROUND_STATE + getNumberOfStates() - 1 );
        return 1240.0 / ( 13.6 * ( ( 1.0 / ( m * m ) ) - ( 1.0 / ( n * n ) ) ) );
    }
   
    /*
     * Gets the wavelength that is emitted when the 
     * electron transitions from state n to state m, where m < n.
     * 
     * @param n
     * @param m
     * @return
     */
    private static double getWavelengthEmitted( int n, int m ) {
        return getWavelengthAbsorbed( m, n );
    }
    
    /**
     * Determines if two wavelengths are "close" for the purposes of absorption and emission.
     * @param w1
     * @param w2
     * @return true or false
     */
    private boolean close( double w1, double w2 ) {
        return( Math.abs( w1 - w2 ) < WAVELENGTH_CLOSENESS_THRESHOLD );
    }
    
    //----------------------------------------------------------------------------
    // AbstractHydrogenAtom implementation
    //----------------------------------------------------------------------------
    
    /**
     * Moves a photon.
     * A collision occurs when a photon comes "close" to the electron.
     * If a collision occurs, there is a probability of absorption.
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

        // clockwise orbit
        _electronAngle -= dt * ( ELECTRON_ANGLE_DELTA / ( _electronState * _electronState ) );
        updateElectronOffset();

        // Attempt to emit a photon
        emitPhoton();
    }
}
