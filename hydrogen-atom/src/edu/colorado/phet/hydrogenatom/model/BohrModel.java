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

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom.PhotonAbsorbedEvent;
import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom.PhotonEmittedEvent;
import edu.colorado.phet.hydrogenatom.util.RandomUtils;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;
import edu.colorado.phet.hydrogenatom.view.particle.PhotonNode;
import edu.colorado.phet.hydrogenatom.view.particle.ProtonNode;


public class BohrModel extends AbstractHydrogenAtom {

    //----------------------------------------------------------------------------
    // Private class data
    //----------------------------------------------------------------------------
    
    /* Number of electron states supported by this model */
    private static final int NUMBER_OF_STATES = 6;
    
    /* Ground state */
    private static final int GROUND_STATE = 1;
    
    /* Radius of the electron's ground state */
    private static final double GROUND_ORBIT_RADIUS = 8.5;
    
    /* Minimum spacing between the proton and electron shapes */
    private static final double MIN_PROTON_ELECTRON_SPACING = 2;
    
    /* how close a photon and electron must be to collide */
    private static final double PHOTON_ELECTRON_COLLISION_THRESHOLD = PhotonNode.DIAMETER / 2;
    
    /* probability that photon will be emitted */
    private static final double PHOTON_EMISSION_PROBABILITY = 0.01; // 1.0 = 100%
    
    /* probability that photon will be absorbed */
    private static final double PHOTON_ABSORPTION_PROBABILITY = 0.5; // 1.0 = 100%
    
    /* change in orbit angle per dt */
    private static final double ELECTRON_ANGLE_DELTA = Math.toRadians( 10 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // electron state
    private int _electronState;
    // orbit radii
    private double[] _orbitRadii;
    // number of photons the atom has absorbed and is "holding"
    private int _numberOfPhotonsAbsorbed;
    // offset of the electron relative to atom's center
    private Point2D _electronOffset;
    // current angle of electron
    private double _electronAngle;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BohrModel( Point2D position ) {
        super( position, 0 /* orientation */ );
        _electronState = GROUND_STATE;
        _orbitRadii = createOrbitRadii( NUMBER_OF_STATES );
        _numberOfPhotonsAbsorbed = 0;
        _electronOffset = new Point2D.Double( getOrbitRadius( _electronState ), 0 ); //XXX randomize position on 1st orbit?
        _electronAngle = 0;
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
    
    /**
     * Gets the number of electron states that the model supports.
     * This is the same as the number of orbits.
     * @return int
     */
    public int getNumberOfStates() {
        return _orbitRadii.length;
    }
    
    /**
     * Gets the ground state.
     * @return
     */
    public int getGroundState() {
        return GROUND_STATE;
    }
    
    /**
     * Gets the radius for a specified state.
     * @param state
     * @return
     */
    public double getOrbitRadius( int state ) {
        return _orbitRadii[ state - GROUND_STATE ];
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
     * Creates N orbit radii.
     * The ground orbit is fudged so that the visual representation
     * of the electron and proton won't overlap.
     */
    private static double[] createOrbitRadii( int numberOfOrbits ) {
        final double protonDiameter = new ProtonNode().getDiameter();
        final double electronDiameter = new ElectronNode().getDiameter();
        final double minGroundRadius = protonDiameter + electronDiameter + MIN_PROTON_ELECTRON_SPACING;
        double[] radii = new double[ NUMBER_OF_STATES ];
        radii[0] = Math.min( GROUND_ORBIT_RADIUS, minGroundRadius );
        for ( int n = 2; n <= radii.length; n++ ) {
            radii[n - 1] = n * n * GROUND_ORBIT_RADIUS;
        }
        return radii;
    }
    
    /*
     * Cannot absorb a photon if any of these are true:
     * - the photon was emitted by the atom
     */
    private boolean canAbsorb( Photon photon ) {
        return !( photon.wasEmitted() || _numberOfPhotonsAbsorbed == NUMBER_OF_STATES - 1 );
    }
    
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
    
    //----------------------------------------------------------------------------
    // Photon absorption and emission
    //----------------------------------------------------------------------------
    
    /*
     * Absorbs the specified photon.
     */
    private void absorbPhoton( Photon photon ) {
        _numberOfPhotonsAbsorbed += 1;
        PhotonAbsorbedEvent event = new PhotonAbsorbedEvent( this, photon );
        firePhotonAbsorbedEvent( event );
        
        _electronState += 1;
        notifyObservers( PROPERTY_ELECTRON_STATE );
    }
    
    /*
     * Emits a photon from the electron's location, at a random orientation.
     */
    private void emitPhoton() {
        if ( _numberOfPhotonsAbsorbed > 0 ) {
            
            _numberOfPhotonsAbsorbed -= 1;
            
            // Use the electron's position
            Point2D position = getElectronPosition();
            
            // Pick a random orientation
            double orientation = RandomUtils.nextOrientation();
            
            double speed = HAConstants.PHOTON_INITIAL_SPEED;
            
            double wavelength = 150; //XXX depends on state we're in
            
            // Create and emit a photon
            Photon photon = new Photon( wavelength, position, orientation, speed, true /* emitted */ );
            PhotonEmittedEvent event = new PhotonEmittedEvent( this, photon );
            firePhotonEmittedEvent( event );
            
            // Change states
            _electronState -= 1;
            notifyObservers( PROPERTY_ELECTRON_STATE );
        }
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
        
        boolean absorbed = false;
        
        if ( canAbsorb( photon ) ) {
            Point2D electronPosition = getElectronPosition();
            Point2D photonPosition = photon.getPosition();
            if ( pointsCollide( electronPosition, photonPosition, PHOTON_ELECTRON_COLLISION_THRESHOLD ) ) {
                if ( Math.random() < PHOTON_ABSORPTION_PROBABILITY ) {
                    absorbPhoton( photon );
                    absorbed = true;
                }
            }
        }
        
        if ( !absorbed ) {
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
     * XXX
     */
    public void stepInTime( double dt ) {
        
       // clockwise orbit
        _electronAngle -= dt * ( ELECTRON_ANGLE_DELTA / ( _electronState * _electronState ) );
        updateElectronOffset();

        if ( _numberOfPhotonsAbsorbed > 0 ) {
            // Randomly emit a photon
            if ( Math.random() < PHOTON_EMISSION_PROBABILITY ) {
                emitPhoton();
            }
        }
    }
}
