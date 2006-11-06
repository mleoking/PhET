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

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import com.sun.rsasign.al;

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
 */
public class PlumPuddingModel extends AbstractHydrogenAtom {

    //----------------------------------------------------------------------------
    // Public class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_ELECTRON_OFFSET = "electronOffset";
    
    //----------------------------------------------------------------------------
    // Private class data
    //----------------------------------------------------------------------------
    
    /* default radius of the atom, tweaked to match PlumPuddingNode image */
    private static final double DEFAULT_RADIUS = 90;
    
    /* maximum number of photons that can be absorbed */
    private static final int MAX_PHOTONS_ABSORBED = 1; //WARNING: Untested with values != 1
    
    /* how close a photon and electron must be to collide */
    private static final double PHOTON_ELECTRON_COLLISION_THRESHOLD = 20;
    
    /* wavelength of emitted photons */
    private static final double PHOTON_EMISSION_WAVELENGTH = 150; // nm
    
    /* probability that photon will be emitted */
    public static final double PHOTON_EMISSION_PROBABILITY = 0.1; // 1.0 = 100%
    
    /* probability that photon will be absorbed */
    public static final double PHOTON_ABSORPTION_PROBABILITY = 0.5; // 1.0 = 100%
    
    /* number of discrete steps in the electron line */
    private static final int ELECTRON_LINE_SEGMENTS = 30;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // number of photons the atom has absorbed and is "holding"
    private int _numberOfPhotonsAbsorbed;
    // radius of the atom's goo
    private double _radius;
    // shape used to represent the atom, in world coordinates
    private Shape _shape;
    // offset of the electron relative to atom's center
    private Point2D _electronOffset;
    // line on which the electron oscillates, relative to atom's center
    private Line2D _electronLine;
    // the electron's direction of motion, relative to the X (horizontal) axis
    private boolean _electronDirectionPositive;
    
    // is the electron moving?
    private boolean _electronIsMoving;
    // how many times has the electron crossed the atom's center since it started moving?
    private int _numberOfZeroCrossings;
    private double _previousAmplitude;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructs an atom with a default size.
     * @param position
     */
    public PlumPuddingModel( Point2D position ) {
        this( position, DEFAULT_RADIUS );
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
        _electronOffset = new Point2D.Double( 0, 0 );
        _electronLine = new Line2D.Double();
        
        _electronIsMoving = false;
        _numberOfZeroCrossings = 0;
        _previousAmplitude = 0;
        
        updateShape();
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
     * Sets the atom's position and updates its shape.
     * @param p
     */
    public void setPosition( Point2D p ) {
        setPosition( p.getX(), p.getY() );
    }
    
    /**
     * Sets the atom's position and updates its shape.
     * @param x
     * @param y
     */
    public void setPosition( double x, double y ) {
        super.setPosition( x, y );
        updateShape();
    }
    
    /**
     * Gets the electron's offset, relative to the atom's center.
     * @return Point2D
     */
    public Point2D getElectronOffset() {
        return _electronOffset;
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
    
    //----------------------------------------------------------------------------
    // utilities
    //----------------------------------------------------------------------------
    
    /*
     * Updates the line that determines the electron's oscillation path
     * when the electron is moving at maximum amplitude.
     * The line is specified in coordinates relative to the atom's center.
     */
    private void updateElectronLine() {
        
        double angle = RandomUtils.nextOrientation();
        double x = Math.abs( _radius * Math.sin( angle ) );
        double y = RandomUtils.nextSign() * _radius * Math.cos( angle );
        _electronLine.setLine( -x, -y, x, y );
        assert( _electronLine.getX1() < _electronLine.getX2() ); // required by moveElectron()
        
        _electronDirectionPositive = RandomUtils.nextBoolean();
        
        // move electron back to center
        _electronOffset.setLocation( 0, 0 );
        notifyObservers( PROPERTY_ELECTRON_OFFSET );
    }
    
    /*
     * Updates the shape (a circle) used to represent the atom's goo.
     * The shape is specified in world coordinates.
     */
    private void updateShape() {
        double diameter = ( 2 * _radius );
        _shape = new Ellipse2D.Double( getX() - _radius, getY() - _radius, diameter, diameter );
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
     * Cannot absorb a photon if any of these are true:
     * - the photon was emitted by the atom
     * - we've already absorbed the max
     * - we've emitted out last photon and haven't completed oscillation.
     */
    private boolean canAbsorb( Photon photon ) {
        return !( photon.wasEmitted() || _numberOfPhotonsAbsorbed == MAX_PHOTONS_ABSORBED || ( _numberOfPhotonsAbsorbed == 0 && _electronIsMoving ) );
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
     * Absorbed the specified photon.
     */
    private void absorbPhoton( Photon photon ) {
        _numberOfPhotonsAbsorbed += 1;
        assert( _numberOfPhotonsAbsorbed <= MAX_PHOTONS_ABSORBED );
        PhotonAbsorbedEvent event = new PhotonAbsorbedEvent( this, photon );
        firePhotonAbsorbedEvent( event );
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
            
            // Create and emit a photon
            Photon photon = new Photon( PHOTON_EMISSION_WAVELENGTH, position, orientation, speed, true /* emitted */ );
            PhotonEmittedEvent event = new PhotonEmittedEvent( this, photon );
            firePhotonEmittedEvent( event );
        }
    }
    
    //----------------------------------------------------------------------------
    // AbstractHydrogenAtom implementation
    //----------------------------------------------------------------------------
    
    /**
     * Detects and handles collision with a photon.
     * A collision occurs when a photon comes "close" to the electron.
     * If a collision occurs, there is a probability of absorption.
     * 
     * @param photon
     */
    public void detectCollision( Photon photon ) {
        if ( canAbsorb( photon ) ) {
            Point2D electronPosition = getElectronPosition();
            Point2D photonPosition = photon.getPosition();
            if ( pointsCollide( electronPosition, photonPosition, PHOTON_ELECTRON_COLLISION_THRESHOLD ) ) {
                if ( Math.random() < PHOTON_ABSORPTION_PROBABILITY ) {
                    absorbPhoton( photon );
                }
            }
        }
    }
    
    /**
     * Detects and handles collision with an alpha particle.
     * If a collision occurs, the alpha particle is deflected using
     * a Rutherford Scattering algorithm.
     * 
     * @param alphaParticle
     */
    public void detectCollision( AlphaParticle alphaParticle ) {
        Point2D position = alphaParticle.getPosition();
        if ( _shape.contains( position ) ) {
            //XXX use Rutherford Scattering algorithm
        }
    }
    
    /*
     * Determines if two points collide.
     */
    private static boolean pointsCollide( Point2D p1, Point2D p2, double threshold ) {
        boolean xClose = ( Math.abs( p1.getX() - p2.getX() ) < threshold );
        boolean yClose = ( Math.abs( p1.getY() - p2.getY() ) < threshold );
        return ( xClose && yClose );
    }
    
    /**
     * Move an alpha particle using a Rutherford Scattering algorithm.
     * If the alpha particle's initial x position is the same as the atom's,
     * then divide-by-zero errors can occur (since b=0), so simply use 
     * the default method of moving the alpha particle.
     * 
     * @param alphaParticle
     * @param dt
     */
    public void move( AlphaParticle alphaParticle, final double dt ) {
        final double b = alphaParticle.getInitialPosition().getX() - getX();
        if ( b == 0 ) {
            super.move( alphaParticle, dt );
        }
        else {
            final double L = HAConstants.ANIMATION_BOX_SIZE.height;
            final double R = _radius;
            final double DB = L / 4;
            final double D = ( b <= R ) ? ( ( DB * b * b ) / ( R * R ) ) : DB;
            RutherfordScattering.move( this, alphaParticle, dt, D );
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
            boolean b1 = ( Math.random() < PHOTON_EMISSION_PROBABILITY );
            boolean b2 = ( getNumberOfElectronOscillations() != 0 );
            if ( b1 && b2 ) {
                emitPhoton();
                if ( _numberOfPhotonsAbsorbed == 0 ) {
                    // If we have not more photons, remember amplitude so we can complete oscillation.
                    _previousAmplitude = amplitude;
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
                _electronOffset.setLocation( 0, 0 );
                notifyObservers( PROPERTY_ELECTRON_OFFSET );
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

        _electronOffset.setLocation( x, y );
        notifyObservers( PROPERTY_ELECTRON_OFFSET );
    }
}
