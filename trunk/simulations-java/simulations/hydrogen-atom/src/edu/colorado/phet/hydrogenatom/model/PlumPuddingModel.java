// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.hydrogenatom.model;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.util.RandomUtils;

/**
 * PlumPuddingModel models the hydrogen atom as plum pudding.
 * <p>
 * Physical representation:
 * The proton is a blob of pudding (or "goo"), modeled as a circle.
 * An electron oscillates inside the goo along a straight line 
 * that passes through the center of the goo and has its end points
 * on the circle.  
 * <p>
 * Collision behavior:
 * Photons collide with the electron when they are "close".
 * Alpha particles collide with the goo and are deflected 
 * using a Rutherford scattering algorithm.
 * <p>
 * Absorption behavior:
 * The electron can absorb N photons.
 * When any photon collides with the electron, it is absorbed with
 * some probability, and (if absorbed) causes the electron to start oscillating.
 * Alpha particles are not absorbed.
 * <p>
 * Emission behavior:
 * The electron can emit one UV photon for each photon absorbed.
 * Photons are emitted at the electron's location.
 * No photons are emitted until the electron has completed one
 * oscillation cycle, and after emitting its last photon,
 * the electron completes its current oscillation cycles,
 * coming to rest at the atoms center.
 * Alpha particles are not emitted.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PlumPuddingModel extends AbstractHydrogenAtom {
    
    //----------------------------------------------------------------------------
    // Private class data
    //----------------------------------------------------------------------------
    
    /* default radius of the atom, tweaked to match PlumPuddingNode image */
    private static final double DEFAULT_RADIUS = 30;
    
    /* maximum number of photons that can be absorbed */
    private static final int MAX_PHOTONS_ABSORBED = 1; //WARNING: Untested with values != 1
    
    /* wavelength of emitted photons */
    private static final double PHOTON_EMISSION_WAVELENGTH = 150; // nm
    
    /* probability that photon will be emitted */
    private static final double PHOTON_EMISSION_PROBABILITY = 0.1; // 1.0 = 100%
    
    /* probability that photon will be absorbed */
    private static final double PHOTON_ABSORPTION_PROBABILITY = 0.5; // 1.0 = 100%
    
    /* number of discrete steps in the electron line */
    private static final int ELECTRON_LINE_SEGMENTS = 30;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // number of photons the atom has absorbed and is "holding"
    private int _numberOfPhotonsAbsorbed;
    // radius of the atom's goo
    private double _radius;
    // offset of the electron relative to atom's center
    private Point2D _electronOffset;
    // electron's position in world coordinates
    private Point2D _electronPosition;
    // line on which the electron oscillates, relative to atom's center
    private Line2D _electronLine;
    // the electron's direction of motion, relative to the X (horizontal) axis
    private boolean _electronDirectionPositive;
    
    // is the electron moving?
    private boolean _electronIsMoving;
    // how many times has the electron crossed the atom's center since it started moving?
    private int _numberOfZeroCrossings;
    // the amplitude of the electron just before emitting its last photon
    private double _previousAmplitude;
    
    // random number generator for absorption probability
    private Random _randomAbsorption;
    // random number generator for emission probability
    private Random _randomEmission;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructs an atom with a default size.
     * @param position
     */
    public PlumPuddingModel( Point2D position ) {
        this( position, DEFAULT_RADIUS );
        
        _randomAbsorption = new Random();
        _randomEmission = new Random();
    }
    
    /*
     * Constructor.
     * @param position
     * @param radius
     */
    private PlumPuddingModel( Point2D position, double radius ) {
        super( position, 0 /* orientation */ );
        
        _numberOfPhotonsAbsorbed = 0;
        _radius = radius;
        _electronOffset = new Point2D.Double();
        _electronPosition = new Point2D.Double();
        _electronLine = new Line2D.Double();
        
        _electronIsMoving = false;
        _numberOfZeroCrossings = 0;
        _previousAmplitude = 0;
        
        updateElectronLine();
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the radius of the atom.
     * @return radius
     */
    public double getRadius() {
        return _radius;
    }
    
    /**
     * Gets the electron's offset, relative to the atom's center.
     * This method does NOT allocate a Point2D -- do not modify the value returned!
     * 
     * @return Point2D
     */
    public Point2D getElectronOffsetRef() {
        return _electronOffset;
    }
    
    /*
     * Sets the electron's offset (and position).
     * 
     * @param xOffset
     * @param yOffset
     */
    private void setElectronOffset( double xOffset, double yOffset ) {
        _electronOffset.setLocation( xOffset, yOffset );
        _electronPosition.setLocation( getX() + xOffset, getY() + yOffset );
        notifyObservers( PROPERTY_ELECTRON_OFFSET );
    }
    
    //----------------------------------------------------------------------------
    // utilities
    //----------------------------------------------------------------------------
    
    /*
     * Updates the line that determines the electron's oscillation path
     * when the electron is moving at maximum amplitude.
     * The line is specified in coordinates relative to the atom's center.
     */
    private void updateElectronLine() {
        
        double angle = RandomUtils.nextAngle();
        double x = Math.abs( _radius * Math.sin( angle ) );
        double y = RandomUtils.nextSign() * _radius * Math.cos( angle );
        _electronLine.setLine( -x, -y, x, y );
        assert( _electronLine.getX1() < _electronLine.getX2() ); // required by moveElectron()
        
        _electronDirectionPositive = RandomUtils.nextBoolean();
        
        // move electron back to center
        setElectronOffset( 0, 0 );
    }
    
    /*
     * Gets the electron's amplitude.
     * This is ratio of the number of photons actually absorbed to
     * the number of photons the electron is capable of absorbing.
     */
    private double getElectronAmplitude() {
        return ( _numberOfPhotonsAbsorbed / (double)MAX_PHOTONS_ABSORBED );
    }
    
    /*
     * Gets the sign (+-) that corresponds to the electron's direction.
     * +x is to the right, +y is down.
     */
    private int getElectronDirectionSign() {
        return ( _electronDirectionPositive == true ) ? +1 : -1;
    }
    
    /*
     * Changes the electron's direction.
     */
    private void changeElectronDirection() {
        _electronDirectionPositive = !_electronDirectionPositive;
    }
    
    /*
     * Gets the number of oscillations that the electron has completed 
     * since it started moving. This is a function of the number of times
     * the electron has crossed the center of the atom.
     */
    private int getNumberOfElectronOscillations() {
        return ( _numberOfZeroCrossings % 2 );
    }
    
    /*
     * Determines if the sign (+-) on two numbers is different.
     */
    private boolean signIsDifferent( double d1, double d2 ) {
        return ( ( d1 > 0 && d2 < 0 ) || ( d1 < 0 && d2 > 0 ) );
    }
    
    //----------------------------------------------------------------------------
    // Photon absorption and emission
    //----------------------------------------------------------------------------
    
    /*
     * Cannot absorb a photon if any of these are true:
     * - the photon was emitted by the atom
     * - we've already absorbed the max
     * - we've emitted out last photon and haven't completed oscillation.
     */
    private boolean canAbsorb( Photon photon ) {
        return !( photon.isEmitted() || _numberOfPhotonsAbsorbed == MAX_PHOTONS_ABSORBED || ( _numberOfPhotonsAbsorbed == 0 && _electronIsMoving ) );
    }
    
    /*
     * Attempts to absorb the specified photon.
     * @param photon
     * @param returns true or false
     */
    private boolean absorbPhoton( Photon photon ) {
        boolean absorbed = false;
        if ( canAbsorb( photon ) ) {
            Point2D photonPosition = photon.getPositionRef();
            if ( pointsCollide( _electronPosition, photonPosition, COLLISION_CLOSENESS ) ) {
                if ( _randomAbsorption.nextDouble() < PHOTON_ABSORPTION_PROBABILITY ) {
                    _numberOfPhotonsAbsorbed++;
                    assert( _numberOfPhotonsAbsorbed <= MAX_PHOTONS_ABSORBED );
                    firePhotonAbsorbed( photon );
                    absorbed = true;
                }
            }
        }
        return absorbed;
    }
    
    /*
     * Emits a photon from the electron's location, at a random orientation.
     */
    private void emitPhoton() {
        if ( _numberOfPhotonsAbsorbed > 0 ) {
            
            _numberOfPhotonsAbsorbed--;
            
            // Use the electron's position
            Point2D position = _electronPosition;
            
            // Pick a random orientation
            double orientation = RandomUtils.nextAngle();
            
            double speed = HAConstants.PHOTON_INITIAL_SPEED;
            
            // Create and emit a photon
            firePhotonEmitted( new Photon( PHOTON_EMISSION_WAVELENGTH, position, orientation, speed, true /* emitted */ ) );
        }
    }
    
    //----------------------------------------------------------------------------
    // AbstractHydrogenAtom implementation
    //----------------------------------------------------------------------------
    
    /**
     * Tries to absorb the photon.
     * If it's not absorbed, the photon is moved.
     * 
     * @param photon
     */
    public void movePhoton( Photon photon, double dt ) {
        boolean absorbed = absorbPhoton( photon );
        if ( !absorbed ) {
            super.movePhoton( photon, dt );
        }
    }
    
    /**
     * Moves an alpha particle using a Rutherford Scattering algorithm.
     * <p>
     * WORKAROUND -
     * If the particle is "close" to the atom's center, then it simply
     * passes through at constant speed.  This is a workaround for a 
     * problem in RutherfordScattering; particles get stuck at the 
     * center of the plum pudding atom, or they seem to stick slightly
     * and then accelerate off.  The value of "closeness" was set 
     * through trial and error, to eliminate these problems while still
     * making the motion look continuous. This workaround assumes that 
     * alpha particles are moving vertically from bottom to top.
     * 
     * @param alphaParticle
     * @param dt
     */
    public void moveAlphaParticle( AlphaParticle alphaParticle, double dt ) {
        final double closeness = 10;
        if ( Math.abs( alphaParticle.getX() - getX() ) < closeness ) {
            super.moveAlphaParticle( alphaParticle, dt );
        }
        else {
            RutherfordScattering.moveParticle( this, alphaParticle, dt );
        }
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /**
     * Oscillates the electron inside the atom.
     * Emits photon at random time.
     * After emitting its last photon, the electron completes its oscillation
     * and returns to (0,0).
     */
    public void stepInTime( double dt ) {
        
       if ( _numberOfPhotonsAbsorbed > 0 ) {
           
            _electronIsMoving = true;
            
            // Move the electron
            final double amplitude = getElectronAmplitude();
            moveElectron( dt, amplitude );
            
            // Randomly emit a photon after completing an oscillation cycle
            if ( getNumberOfElectronOscillations() != 0 ) {
                if ( _randomEmission.nextDouble() < PHOTON_EMISSION_PROBABILITY ) {
                    emitPhoton();
                    if ( _numberOfPhotonsAbsorbed == 0 ) {
                        // If we have not more photons, remember amplitude so we can complete oscillation.
                        _previousAmplitude = amplitude;
                    }
                }
            }
        }
        else if ( _electronIsMoving && _numberOfPhotonsAbsorbed == 0 ) {
            
            // Stop the electron when it completes its current oscillation
            int before = getNumberOfElectronOscillations();
            moveElectron( dt, _previousAmplitude );
            int after = getNumberOfElectronOscillations();
            if ( before != after ) {
                _electronIsMoving = false;
                _numberOfZeroCrossings = 0;
                _previousAmplitude = 0;
                updateElectronLine();
                setElectronOffset( 0, 0 );
            }
        }
    }
    
    /*
     * Moves the electron along its oscillation path with some amplitude.
     */
    private void moveElectron( double dt, double amplitude ) {

        // Assumptions about the electron's oscillation line...
        assert( _electronLine.getX1() < _electronLine.getX2() );
        assert( Math.abs( _electronLine.getX1() ) == Math.abs( _electronLine.getX2() ) );
        assert( Math.abs( _electronLine.getY1() ) == Math.abs( _electronLine.getY2() ) );
        
        // Remember the old offset 
        final double xo = _electronOffset.getX();
        final double yo = _electronOffset.getY();

        // Determine dx and dy
        double distanceDelta = dt * ( amplitude * ( 2 * _radius ) / ELECTRON_LINE_SEGMENTS );
        double dx = Math.abs( _electronLine.getX1() ) * ( distanceDelta / _radius );
        double dy = Math.abs( _electronLine.getY1() ) * ( distanceDelta / _radius );

        // Adjust signs for electron's horizontal direction
        final double sign = getElectronDirectionSign();
        dx *= sign;
        dy *= sign;
        if ( _electronLine.getY1() > _electronLine.getY2() ) {
            dy *= -1;
        }

        // Electron's new offset
        double x = _electronOffset.getX() + dx;
        double y = _electronOffset.getY() + dy;

        // Is the new offset past the ends of the oscillation line?
        if ( Math.abs( x ) > Math.abs( _electronLine.getX1() ) || Math.abs( y ) > Math.abs( _electronLine.getY1() ) ) {
            if ( _electronDirectionPositive ) {
                x = _electronLine.getX2();
                y = _electronLine.getY2();
            }
            else {
                x = _electronLine.getX1();
                y = _electronLine.getY1();
            }
            changeElectronDirection();
        }

        // Did we cross the origin?
        if ( ( x == 0 && y == 0 ) || signIsDifferent( x, xo ) || signIsDifferent( y, yo ) ) {
            _numberOfZeroCrossings++;
        }

        setElectronOffset( x, y );
    }
}
