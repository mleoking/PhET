// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.rutherfordscattering.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Random;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.rutherfordscattering.event.GunFiredEvent;
import edu.colorado.phet.rutherfordscattering.event.GunFiredListener;

/**
 * Gun is the model of a gun that can fire alpha particles.
 * It is located at a point in space with a specific orientation and it 
 * has a nozzle with a specific width.
 * The gun's local origin is at the center of its nozzle.
 * When the orientation is zero, the gun points to the right.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Gun extends FixedObject implements ModelElement {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_ENABLED = "enabled";
    public static final String PROPERTY_ORIENTATION = "orientation";
    public static final String PROPERTY_NOZZLE_WIDTH = "nozzleWidth";
    public static final String PROPERTY_INTENSITY = "intensity";
    public static final String PROPERTY_SPEED = "speed";
    
    private static final double DEFAULT_INTENSITY = 1; // 100%
    private static final int DEFAULT_MAX_PARTICLES = 20;
    
    private static final double Y0_MIN = 10;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private boolean _enabled; // is the gun on or off?
    private boolean _running; // is the gun running? used to stop alpha particle production while dragging sliders
    private double _nozzleWidth; // width of the beam
    private double _intensity; // intensity of the alpha particles, 0.0-1.0
    private double _speed; // initial speed of alpha particles shot from the gun
    private final double _defaultSpeed; // default speed
    private final double _minSpeed, _maxSpeed;
    private final Color _beamColor; // base color (no alpha) of the alpha particle beam
    private final Dimension _boxSize; // size of the box that the gun is shooting across
    
    private int _maxParticles; // particles in the animation box when gun intensity is 100%
    private double _dtPerGunFired; // dt between each firing of the gun
    private double _dtSinceGunFired; // dt since the gun was last fired
    
    private Random _randomPosition; // random number generator for alpha particle positions
    private Random _randomSign; // random number generator for +/- sign
    
    private EventListenerList _listenerList;
    
    private AffineTransform _transform; // reusable transform
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor
     * 
     * @param position
     * @param orientation radians
     * @param nozzleWidth
     * @param speedRange
     * @param beamColor
     * @param boxSize
     */
    public Gun( Point2D position, double orientation, double nozzleWidth, DoubleRange speedRange, Color beamColor, Dimension boxSize ) {
        super( position, orientation );
        
        if ( nozzleWidth <= 0 ) {
            throw new IllegalArgumentException( "invalid nozzleWidth: " + nozzleWidth );
        }
        
        _enabled = false;
        _running = true;
        _nozzleWidth = nozzleWidth;
        _defaultSpeed = _speed = speedRange.getDefault();
        _minSpeed = speedRange.getMin();
        _maxSpeed = speedRange.getMax();
        _beamColor = beamColor;
        _boxSize = new Dimension( boxSize );
        _intensity = DEFAULT_INTENSITY;
      
        _randomPosition = new Random();
        _randomSign = new Random();
        
        _dtSinceGunFired = 0;
        setMaxParticles( DEFAULT_MAX_PARTICLES );
        
        _listenerList = new EventListenerList();
        
        _transform = new AffineTransform();
    }
    
    //----------------------------------------------------------------------------
    // Mutators & accessors
    //----------------------------------------------------------------------------
    
    public void setEnabled( boolean enabled ) {
        if ( _enabled != enabled ) {
            _enabled = enabled;
            notifyObservers( PROPERTY_ENABLED );
        }
    }
    
    public boolean isEnabled() {
        return _enabled;
    }
    
    public void setRunning( boolean running ) {
        _running = running;
        // no notification required
    }
    
    public boolean isRunning() {
        return _running;
    }
    
    public void setNozzleWidth( double nozzleWidth ) {
        if ( nozzleWidth <= 0 ) {
            throw new IllegalArgumentException( "invalid nozzleWidth: " + nozzleWidth );
        }
        if ( nozzleWidth != _nozzleWidth ) {
            _nozzleWidth = nozzleWidth;
            notifyObservers( PROPERTY_NOZZLE_WIDTH );
        }
    }
    
    public double getNozzleWidth() {
        return _nozzleWidth;
    }
    
    public void setIntensity( double intensity ) {
        if ( intensity < 0 || intensity > 1 ) {
            throw new IllegalArgumentException( "invalid intensity: " + intensity );
        }
        if ( intensity != _intensity ) {
            _intensity = intensity;
            notifyObservers( PROPERTY_INTENSITY );
        }
    }
    
    public double getIntensity() {
        return _intensity;
    }
    
    public void setMaxParticles( int maxParticles ) {
        if ( maxParticles <= 0 ) {
            throw new IllegalArgumentException( "invalid maxParticles: " + maxParticles );
        }
        _maxParticles = maxParticles;
        _dtPerGunFired = ( _boxSize.height / _defaultSpeed ) / maxParticles;
    }
    
    public int getMaxParticles() {
        return _maxParticles;
    }
    
    public void setSpeed( double speed ) {
        if ( speed < _minSpeed || speed > _maxSpeed ) {
            throw new IllegalArgumentException( "invalid speed: " + speed );
        }
        if ( speed != _speed ) {
            _speed = speed;
            notifyObservers( PROPERTY_SPEED );
        }
    }
    
    public double getSpeed() {
        return _speed;
    }
    
    public double getDefaultSpeed() {
        return _defaultSpeed;
    }
    
    public double getMinSpeed() {
        return _minSpeed;
    }
    
    public double getMaxSpeed() {
        return _maxSpeed;
    }
    
    /**
     * Gets the color of the gun's beam.
     * The alpha component of the color is based on the gun's intensity.
     * 
     * @return Color, null if the gun is off
     */
    public Color getBeamColor() {
        Color beamColor = null;
        if ( _enabled ) {
            Color c = _beamColor;
            int a = (int) ( _intensity * 255 );
            beamColor = new Color( c.getRed(), c.getGreen(), c.getBlue(), a );
        }
        return beamColor;
    }
    
    //----------------------------------------------------------------------------
    // private
    //----------------------------------------------------------------------------
    
    /*
     * Gets a random point along the gun's nozzle.
     * Avoid points that are too "close" to the center of the nozzle.
     * 
     * @return Point2D
     */
    private Point2D getRandomNozzlePoint() {
        
        int ySign = _randomSign.nextBoolean() ? 1 : -1;
        
        // Start with the gun's origin at zero, gun pointing to the right
        double x = 1;
        double y = ySign * ( Y0_MIN + ( _randomPosition.nextDouble() * ( ( _nozzleWidth / 2 ) - Y0_MIN ) ) );
        if ( !(Math.abs( y ) >= Y0_MIN) ) {
            System.out.println( "y=" + y );
            System.out.println( "ySign=" + ySign );
            System.out.println( "nozzleWidth=" + _nozzleWidth );
            System.out.println( "Y_ABSOLUTE_MIN=" + Y0_MIN );
        }
        assert( Math.abs( y ) >= Y0_MIN );

        // Rotate by gun's orientation
        Point2D p = new Point2D.Double( x, y );
        _transform.setToIdentity();
        _transform.rotate( getOrientation() );
        _transform.transform( p, p );

        // Translate by the gun's position
        p.setLocation( p.getX() + getX(), p.getY() + getY() );
        
        return p;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------

    /*
     * Fires an alpha particle each time that time advances.
     * If the gun is disabled, do nothing.
     */
    public void stepInTime( double dt ) {
        if ( _enabled && _running && _intensity > 0 ) {
            fireAlphaParticle( dt );
        }
    }
    
    //----------------------------------------------------------------------------
    // Fire alpha particles
    //----------------------------------------------------------------------------
    
    /*
     * Fires an alpha particle when it's time to do so.
     * Each alpha particle is fired from a random location along the gun's nozzle.
     */
    private void fireAlphaParticle( double dt ) {

        _dtSinceGunFired += ( _intensity * dt );

        if ( _dtSinceGunFired >= _dtPerGunFired ) {

            _dtSinceGunFired = _dtSinceGunFired % _dtPerGunFired;
            
            // Pick a randon location along the gun's nozzle width
            Point2D position = getRandomNozzlePoint();

            // Direction of alpha particle is same as gun's orientation.
            double orientation = getOrientation();
            
            // Create the alpha particle
            AlphaParticle alphaParticle = new AlphaParticle( position, orientation, _speed, _defaultSpeed );

            // Fire the alpha particle
            GunFiredEvent event = new GunFiredEvent( this, alphaParticle );
            fireAlphaParticleFiredEvent( event );
        }
    }
    
    //----------------------------------------------------------------------------
    // GunFiredListener
    //----------------------------------------------------------------------------
    
    /**
     * Adds a GunFiredListener.
     *
     * @param listener the listener
     */
    public void addGunFiredListener( GunFiredListener listener ) {
        _listenerList.add( GunFiredListener.class, listener );
    }

    /**
     * Removes a GunFiredListener.
     *
     * @param listener the listener
     */
    public void removeGunFiredListener( GunFiredListener listener ) {
        _listenerList.remove( GunFiredListener.class, listener );
    }
    
    /*
     * Fires a GunFiredEvent when an alpha particle is fired.
     *
     * @param event the event
     */
    private void fireAlphaParticleFiredEvent( GunFiredEvent event ) {
        assert( event.getAlphaParticle() != null );
        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == GunFiredListener.class ) {
                ( (GunFiredListener)listeners[i + 1] ).alphaParticleFired( event );
            }
        }
    }
}
