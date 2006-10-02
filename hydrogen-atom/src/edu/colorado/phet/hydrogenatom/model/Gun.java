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

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.EventListener;
import java.util.EventObject;

import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.ClockListener;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.util.ColorUtils;

/**
 * Gun is the model of a gun that can fire either photons or alpha particles.
 * It is located at a point in space with a specific orientation and it 
 * has a nozzle with a specific width.
 * When firing photons, it shoots a beam of light that wavelength and intensity.
 * When firing photons, it shoots alpha particles with some intensity.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Gun extends StationaryObject implements ClockListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_ENABLED = "enabled";
    public static final String PROPERTY_MODE = "mode";
    public static final String PROPERTY_ORIENTATION = "orientation";
    public static final String PROPERTY_NOZZLE_WIDTH = "nozzleWidth";
    public static final String PROPERTY_WAVELENGTH = "waveLength";
    public static final String PROPERTY_LIGHT_INTENSITY = "lightIntensity";
    public static final String PROPERTY_ALPHA_PARTICLES_INTENSITY = "alphaParticlesIntensity";
    
    public static final int MODE_PHOTONS = 0;
    public static final int MODE_ALPHA_PARTICLES = 1;
    
    private static final int PHOTONS_PER_CLOCK_TICK = 1;
    private static final int ALPHA_PARTICLES_PER_CLOCK_TICK = 1;
    
    private static final double DEFAULT_WAVELENGTH = VisibleColor.MIN_WAVELENGTH;
    private static final double DEFAULT_LIGHT_INTENSITY = 0;
    private static final double DEFAULT_ALPHA_PARTICLE_INTENSITY = 0;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private boolean _enabled; // is the gun on or off?
    private int _mode; // is the gun firing photons or alpha particles?
    private double _orientation; // direction the gun points, in degrees
    private double _nozzleWidth; // width of the beam
    private double _wavelength; // wavelength of the light
    private double _lightIntensity; // intensity of the light, 0.0-1.0
    private double _alphaParticlesIntensity; // intensity of the alpha particles, 0.0-1.0
    
    private EventListenerList _listenerList;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Gun( Point2D position, double orientation, double nozzleWidth ) {
        super( position );
        
        if ( nozzleWidth <= 0 ) {
            throw new IllegalArgumentException( "invalid nozzleWidth: " + nozzleWidth );
        }
        
        _enabled = false;
        _mode = MODE_PHOTONS;
        _orientation = orientation;
        _nozzleWidth = nozzleWidth;
        _wavelength = DEFAULT_WAVELENGTH;
        _lightIntensity = DEFAULT_LIGHT_INTENSITY;
        _alphaParticlesIntensity = DEFAULT_ALPHA_PARTICLE_INTENSITY;
        
        _listenerList = new EventListenerList();
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
    
    public void setMode( int mode ) {
        if ( mode != MODE_PHOTONS && mode != MODE_ALPHA_PARTICLES ) {
            throw new IllegalArgumentException( "invalid mode: " + mode );
        }
        if ( mode != _mode ) {
            _mode = mode;
            notifyObservers( PROPERTY_MODE );
        }
    }
    
    public int getMode() {
        return _mode;
    }
    
    public void setOrientation( double orientation ) {
        if ( orientation != _orientation ) {
            _orientation = orientation;
            notifyObservers( PROPERTY_ORIENTATION );
        }
    }
    
    public double getOrientation() {
        return _orientation;
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
    
    public void setWavelength( double wavelength ) {
        if ( wavelength < 0 ) {
            throw new IllegalArgumentException( "invalid wavelength: " + wavelength );
        }
        if ( wavelength != _wavelength ) {
            _wavelength = wavelength;
            notifyObservers( PROPERTY_WAVELENGTH );
        }
    }
    
    public double getWavelength() {
        return _wavelength;
    }
    
    public void setLightIntensity( double lightIntensity ) {
        if ( lightIntensity < 0 || lightIntensity > 1 ) {
            throw new IllegalArgumentException( "invalid lightIntensity: " + lightIntensity );
        }
        if ( lightIntensity != _lightIntensity ) {
            _lightIntensity = lightIntensity;
            notifyObservers( PROPERTY_LIGHT_INTENSITY );
        }
    }
    
    public double getLightIntensity() {
        return _lightIntensity;
    }

    public void setAlphaParticlesIntensity( double alphaParticlesIntensity ) {
        if ( alphaParticlesIntensity < 0 || alphaParticlesIntensity > 1 ) {
            throw new IllegalArgumentException( "invalid alphaParticlesIntensity: " + alphaParticlesIntensity );
        }
        if ( alphaParticlesIntensity != _alphaParticlesIntensity ) {
            _alphaParticlesIntensity = alphaParticlesIntensity;
            notifyObservers( PROPERTY_ALPHA_PARTICLES_INTENSITY );
        }
    }
    
    public double getAlphaParticlesIntensity() {
        return _alphaParticlesIntensity;
    }
    
    //----------------------------------------------------------------------------
    // Convenience accessors
    //----------------------------------------------------------------------------
    
    public boolean isPhotonsMode() {
        return ( _mode == MODE_PHOTONS );
    }
    
    public boolean isAlphaParticlesMode() {
        return ( _mode == MODE_ALPHA_PARTICLES );
    }
    
    public Color getWavelengthColor() {
        Color wavelengthColor = null;
        if ( _enabled ) {
            if ( _mode == MODE_PHOTONS ) {
                wavelengthColor = ColorUtils.wavelengthToColor( _wavelength );
            }
            else {
                wavelengthColor = HAConstants.ALPHA_PARTICLES_COLOR;
            }
        }
        return wavelengthColor;
    }
    
    public Color getBeamColor() {
        Color beamColor = null;
        Color c = getWavelengthColor();
        if ( c != null ) {
            double intensity = ( _mode == MODE_PHOTONS ) ? _lightIntensity : _alphaParticlesIntensity;
            int a = (int) ( intensity * 255 );
            beamColor = new Color( c.getRed(), c.getGreen(), c.getBlue(), a );
        }
        return beamColor;
    }
    
    //----------------------------------------------------------------------------
    // ClockListener implementation
    //----------------------------------------------------------------------------

    public void simulationTimeChanged( ClockEvent clockEvent ) {
        if ( _enabled ) {
            if ( _mode == MODE_PHOTONS && _lightIntensity > 0 ) {
                firePhotons( clockEvent );
            }
            else if ( _mode == MODE_ALPHA_PARTICLES && _alphaParticlesIntensity > 0 ) {
                fireAlphaParticles( clockEvent );
            }
        }
    }

    public void clockTicked( ClockEvent clockEvent ) {}

    public void clockStarted( ClockEvent clockEvent ) {}

    public void clockPaused( ClockEvent clockEvent ) {}

    public void simulationTimeReset( ClockEvent clockEvent ) {}
    
    //----------------------------------------------------------------------------
    // Fire photons and alpha particles
    //----------------------------------------------------------------------------
    
    private void firePhotons( ClockEvent clockEvent ) {
        
        // Determine how many photons to fire.
        double ticks = clockEvent.getSimulationTimeChange();
        int numberOfPhotons = (int) ( _lightIntensity * ticks / PHOTONS_PER_CLOCK_TICK );
        if ( numberOfPhotons == 0 && _lightIntensity > 0 ) {
            numberOfPhotons = 1;
        }

        // Fire photons
//        System.out.println( "firing " + numberOfPhotons + " photons" );//XXX
        for ( int i = 0; i < numberOfPhotons; i++ ) {
            //XXX pick a randon location along the gun's nozzle width
            double x = getPositionRef().getX();
            double y = getPositionRef().getY();
            Point2D position = new Point2D.Double( x, y );
            double direction = _orientation;
            Photon photon = new Photon( position, direction, _wavelength );
            PhotonFiredEvent event = new PhotonFiredEvent( this, photon );
            firePhotonFiredEvent( event );
        }
    }
    
    private void fireAlphaParticles( ClockEvent clockEvent ) {
        
        // Determine how many alpha particles to fire.
        double ticks = clockEvent.getSimulationTimeChange();
        int numberOfAlphaParticles = (int) ( _alphaParticlesIntensity * ticks / ALPHA_PARTICLES_PER_CLOCK_TICK );
        if ( numberOfAlphaParticles == 0 && _alphaParticlesIntensity > 0 ) {
            numberOfAlphaParticles = 1;
        }

        // Fire alpha particles
//        System.out.println( "firing " + numberOfAlphaParticles + " alpha particles" );//XXX
        for ( int i = 0; i < numberOfAlphaParticles; i++ ) {
            //XXX pick a randon location along the gun's nozzle width
            double x = getPositionRef().getX();
            double y = getPositionRef().getY();
            Point2D position = new Point2D.Double( x, y );
            AlphaParticle alphaParticle = new AlphaParticle( position, _orientation );
            AlphaParticleFiredEvent event = new AlphaParticleFiredEvent( this, alphaParticle );
            fireAlphaParticleFiredEvent( event );
        }
    }
    
    //----------------------------------------------------------------------------
    // PhotonFiredListener
    //----------------------------------------------------------------------------
    
    /**
     * PhotonFiredListener is the interface implemented by all listeners
     * who wish to be informed when a photon is fired by the gun.
     */
    public interface PhotonFiredListener extends EventListener {
        public void photonFired( PhotonFiredEvent event );
    }

    /**
     * PhotonFiredEvent indicates the a photon has been fired.
     */
    public class PhotonFiredEvent extends EventObject {

        private Photon _photon;

        public PhotonFiredEvent( Object source, Photon photon ) {
            super( source );
            _photon = photon;
        }

        public Photon getPhoton() {
            return _photon;
        }
    }
    
    /**
     * Adds a PhotonFiredListener.
     *
     * @param listener the listener
     */
    public void addPhotonFiredListener( PhotonFiredListener listener ) {
        _listenerList.add( PhotonFiredListener.class, listener );
    }

    /**
     * Removes a PhotonFiredListener.
     *
     * @param listener the listener
     */
    public void removePhotonFiredListener( PhotonFiredListener listener ) {
        _listenerList.remove( PhotonFiredListener.class, listener );
    }

    /**
     * Fires a PhotonFiredEvent.
     *
     * @param event the event
     */
    private void firePhotonFiredEvent( PhotonFiredEvent event ) {
        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == ChangeListener.class ) {
                ( (PhotonFiredListener)listeners[i + 1] ).photonFired( event );
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // AlphaParticleFiredListener
    //----------------------------------------------------------------------------
    
    /**
     * AlphaParticleFiredListener is the interface implemented by all listeners
     * who wish to be informed when an alpha particle is fired by the gun.
     */
    public interface AlphaParticleFiredListener extends EventListener {
        public void alphaParticleFired( AlphaParticleFiredEvent event );
    }

    /**
     * AlphaParticleFiredEvent indicates the an alpha particle has been fired.
     */
    public class AlphaParticleFiredEvent extends EventObject {

        private AlphaParticle _alphaParticle;

        public AlphaParticleFiredEvent( Object source, AlphaParticle alphaParticle ) {
            super( source );
            _alphaParticle = alphaParticle;
        }

        public AlphaParticle getAlphaParticle() {
            return _alphaParticle;
        }
    }
    
    /**
     * Adds an AlphaParticleFiredListener.
     *
     * @param listener the listener
     */
    public void addAlphaParticleFiredListener( AlphaParticleFiredListener listener ) {
        _listenerList.add( AlphaParticleFiredListener.class, listener );
    }

    /**
     * Removes an AlphaParticleFiredListener.
     *
     * @param listener the listener
     */
    public void removeAlphaParticleFiredListener( AlphaParticleFiredListener listener ) {
        _listenerList.remove( AlphaParticleFiredListener.class, listener );
    }

    /**
     * Fires a PhotonFiredEvent.
     *
     * @param event the event
     */
    private void fireAlphaParticleFiredEvent( AlphaParticleFiredEvent event ) {
        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == ChangeListener.class ) {
                ( (AlphaParticleFiredListener)listeners[i + 1] ).alphaParticleFired( event );
            }
        }
    }
}
