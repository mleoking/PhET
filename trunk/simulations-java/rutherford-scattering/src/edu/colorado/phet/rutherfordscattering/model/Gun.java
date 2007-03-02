/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.model;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Random;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.rutherfordscattering.RSConstants;
import edu.colorado.phet.rutherfordscattering.event.GunFiredEvent;
import edu.colorado.phet.rutherfordscattering.event.GunFiredListener;

/**
 * Gun is the model of a gun that can fire alpha particles.
 * It is located at a point in space with a specific orientation and it 
 * has a nozzle with a specific width.
 * The gun's local origin is at the center of its nozzle.
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
    public static final String PROPERTY_INTENSITY = "alphaParticlesIntensity";
    
    private static final double DEFAULT_INTENSITY = 1; // 100%
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private boolean _enabled; // is the gun on or off?
    private double _nozzleWidth; // width of the beam
    private double _intensity; // intensity of the alpha particles, 0.0-1.0
    
    private int _maxParticles; // particles in the animation box when gun intensity is 100%
    private double _dtPerGunFired; // dt between each firing of the gun
    private double _dtSinceGunFired; // dt since the gun was last fired
    
    private Random _randomPosition; // random number generator for alpha particle positions
    
    private EventListenerList _listenerList;
    
    private AffineTransform _transform; // reusable transform
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor
     * @param position
     * @param orientation radians
     * @param nozzleWidth
     * @param minWavelength nm
     * @param maxWavelength nm
     */
    public Gun( Point2D position, double orientation, double nozzleWidth ) {
        super( position, orientation );
        
        if ( nozzleWidth <= 0 ) {
            throw new IllegalArgumentException( "invalid nozzleWidth: " + nozzleWidth );
        }
        
        _enabled = false;
        _nozzleWidth = nozzleWidth;
        _intensity = DEFAULT_INTENSITY;
      
        _randomPosition = new Random();
        
        _dtSinceGunFired = 0;
        setMaxParticles( 20 );
        
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
        _maxParticles = maxParticles;
        _dtPerGunFired = ( RSConstants.ANIMATION_BOX_SIZE.height / RSConstants.ALPHA_PARTICLE_INITIAL_SPEED ) / maxParticles;
    }
    
    public int getMaxParticles() {
        return _maxParticles;
    }
    
    /**
     * Gets the color of the gun's beam.
     * 
     * @return Color
     */
    public Color getBeamColor() {
        
        Color beamColor = null;
        
        if ( _enabled ) {
            Color c = RSConstants.ALPHA_PARTICLES_COLOR;
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
     * This is based on the nozzle width, gun position, and gun orientation.
     * 
     * @return Point2D
     */
    private Point2D getRandomNozzlePoint() {
        
        // Start with the gun's origin at zero, gun pointing to the right
        double x = 1;
        double y = ( _randomPosition.nextDouble() * _nozzleWidth ) - ( _nozzleWidth / 2 );

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
        if ( _enabled && _intensity > 0 ) {
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
            
            double speed = RSConstants.ALPHA_PARTICLE_INITIAL_SPEED;

            // Create the alpha particle
            AlphaParticle alphaParticle = new AlphaParticle( position, orientation, speed );

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
