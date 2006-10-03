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
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Random;

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
 * The gun's local origin is at the center of its nozzle.
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
    public static final String PROPERTY_LIGHT_TYPE = "lightType";
    public static final String PROPERTY_LIGHT_INTENSITY = "lightIntensity";
    public static final String PROPERTY_WAVELENGTH = "waveLength";
    public static final String PROPERTY_ALPHA_PARTICLES_INTENSITY = "alphaParticlesIntensity";
    
    public static final int MODE_PHOTONS = 0;
    public static final int MODE_ALPHA_PARTICLES = 1;
    
    public static final int LIGHT_TYPE_WHITE = 0;
    public static final int LIGHT_TYPE_MONOCHROMATIC = 1;
    
    private static final double DEFAULT_WAVELENGTH = VisibleColor.MIN_WAVELENGTH;
    private static final double DEFAULT_LIGHT_INTENSITY = 0;
    private static final double DEFAULT_ALPHA_PARTICLE_INTENSITY = 0;
    
    private static final int PHOTONS_PER_CLOCK_TICK = 50;
    private static final int ALPHA_PARTICLES_PER_CLOCK_TICK = 50;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private boolean _enabled; // is the gun on or off?
    private int _mode; // is the gun firing photons or alpha particles?
    private double _orientation; // direction the gun points, in degrees
    private double _nozzleWidth; // width of the beam
    private int _lightType; // type of light (white or monochromatic)
    private double _lightIntensity; // intensity of the light, 0.0-1.0
    private double _wavelength; // wavelength of the light
    private double _minWavelength, _maxWavelength; // range of wavelength
    private double _alphaParticlesIntensity; // intensity of the alpha particles, 0.0-1.0
    private Random _random; // random number generator, for white light wavelength
    
    private EventListenerList _listenerList;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor
     * @param position
     * @param orientation degrees
     * @param nozzleWidth
     * @param minWavelength nm
     * @param maxWavelength nm
     */
    public Gun( Point2D position, double orientation, double nozzleWidth, double minWavelength, double maxWavelength ) {
        super( position );
        
        if ( nozzleWidth <= 0 ) {
            throw new IllegalArgumentException( "invalid nozzleWidth: " + nozzleWidth );
        }
        
        _enabled = false;
        _mode = MODE_PHOTONS;
        _orientation = orientation;
        _nozzleWidth = nozzleWidth;
        _lightType = LIGHT_TYPE_MONOCHROMATIC;
        _lightIntensity = DEFAULT_LIGHT_INTENSITY;
        _wavelength = DEFAULT_WAVELENGTH;
        _minWavelength = minWavelength;
        _maxWavelength = maxWavelength;
        _alphaParticlesIntensity = DEFAULT_ALPHA_PARTICLE_INTENSITY;
        _random = new Random();
        
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

    public void setLightType( int lightType ) {
        if ( lightType != LIGHT_TYPE_WHITE && lightType != LIGHT_TYPE_MONOCHROMATIC ) {
            throw new IllegalArgumentException( "invalid lightType: " + lightType );
        }
        if ( lightType != _lightType ) {
            _lightType = lightType;
            notifyObservers( PROPERTY_LIGHT_TYPE );
        }
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
    
    public double getMinWavelength() {
        return _minWavelength;
    }
    
    public double getMaxWavelength() {
        return _maxWavelength;
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
    
    /**
     * Is the gun in the mode to fire photons (light)?
     * 
     * @return true or false
     */
    public boolean isPhotonsMode() {
        return ( _mode == MODE_PHOTONS );
    }
    
    /**
     * Is the gun in the mode to fire alpha particles?
     * 
     * @return true or false
     */
    public boolean isAlphaParticlesMode() {
        return ( _mode == MODE_ALPHA_PARTICLES );
    }
    
    /**
     * Is the gun configured to fire white light?
     * 
     * @return true or false
     */
    public boolean isWhiteLightType() {
        return ( _lightType == LIGHT_TYPE_WHITE );
    }
    
    /**
     * Is the gun configured to fire monochromatic light?
     * 
     * @return true or false
     */
    public boolean isMonochromaticLightType() {
        return ( _lightType == LIGHT_TYPE_MONOCHROMATIC );   
    }
    
    /**
     * Gets the color assoociate with the gun's monochromatic wavelength.
     * 
     * @return Color
     */
    public Color getWavelengthColor() {
        return ColorUtils.wavelengthToColor( _wavelength );
    }
    
    /**
     * Gets the color of the gun's beam.
     * The alpha component of the Color returned corresponds to the intensity.
     * If the gun is disabled, null is returned.
     * If the gun is shooting alpha particles, HAConstants.ALPHA_PARTICLES_COLOR is returned.
     * If the gun is shooting white light, Color.WHITE is returned.
     * If the gun is shooting monochromatic light, a Color corresponding to the wavelength is returned.
     * 
     * @return Color
     */
    public Color getBeamColor() {
        
        Color beamColor = null;
        Color c = null;
        
        if ( _enabled ) {
            if ( _mode == MODE_PHOTONS ) {
                if ( _lightType == LIGHT_TYPE_WHITE ) {
                    c = Color.WHITE;
                }
                else {
                    c = getWavelengthColor();
                }
            }
            else {
                c = HAConstants.ALPHA_PARTICLES_COLOR;
            }

            if ( c != null ) {
                double intensity = ( _mode == MODE_PHOTONS ) ? _lightIntensity : _alphaParticlesIntensity;
                int a = (int) ( intensity * 255 );
                beamColor = new Color( c.getRed(), c.getGreen(), c.getBlue(), a );
            }
        }
        
        return beamColor;
    }
    
    //----------------------------------------------------------------------------
    // private
    //----------------------------------------------------------------------------
    
    /*
     * Gets a random wavelength in the gun's wavelength range.
     * 
     * @return double
     */
    private double getRandomWavelength() {
        return ( _random.nextDouble() * ( _maxWavelength - _minWavelength ) ) + _minWavelength;
    }
    
    /*
     * Gets a random point along the gun's nozzle.
     * This is based on the nozzle width, gun position, and gun orientation.
     * 
     * @return Point2D
     */
    private Point2D getRandomNozzlePoint() {
        
        // Start with the gun's origin at zero, gun pointing to the right
        double x = 0;
        double y = _random.nextDouble() * _nozzleWidth;

        if ( _orientation == 0 || ( _orientation % 180 == 0 ) ) {
            // do nothing
        }
        else if ( _orientation % 90 == 0 ) {
            // swap x & y
            double tmp = x;
            x = y;
            y = tmp;
        }
        else {
            // rotate x & y
            Point2D p1 = new Point2D.Double( x, y );
            AffineTransform t = new AffineTransform();
            t.rotate( Math.toRadians( _orientation ) );
            Point2D p2 = t.transform( p1, null );
            x = p2.getX();
            y = p2.getY();
        }
        
        // Translate by the gun's position
        x += getX();
        y += getY();
        
        return new Point2D.Double( x, y );
    }
    
    //----------------------------------------------------------------------------
    // ClockListener implementation
    //----------------------------------------------------------------------------

    /*
     * Fires photons or alpha particles each time the clock ticks.
     * If the gun is disabled, do nothing.
     */
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
    
    /*
     * Fires photons.
     * The number of photons fired is based on clock ticks and intensity.
     * Each photon is fired from a random location along the gun's nozzle.
     */
    private void firePhotons( ClockEvent clockEvent ) {
        
        // Determine how many photons to fire.
        double ticks = clockEvent.getSimulationTimeChange();
        int numberOfPhotons = (int) ( _lightIntensity * ticks * PHOTONS_PER_CLOCK_TICK );
        if ( numberOfPhotons == 0 && _lightIntensity > 0 ) {
            numberOfPhotons = 1;
        }

        // Fire photons
        System.out.println( "firing " + numberOfPhotons + " photons" );//XXX
        for ( int i = 0; i < numberOfPhotons; i++ ) {
            
            // Pick a randon location along the gun's nozzle width
            Point2D position = getRandomNozzlePoint();
            
            // Direction of photon is same as gun's orientation.
            double direction = _orientation;
            
            // For white light, assign a random wavelength to each photon.
            double wavelength = _wavelength;
            if ( _lightType == LIGHT_TYPE_WHITE ) {
                wavelength = getRandomWavelength();
            }
            
            // Create the photon
            Photon photon = new Photon( position, direction, wavelength );
            
            // Fire the photon
            PhotonFiredEvent event = new PhotonFiredEvent( this, photon );
            firePhotonFiredEvent( event );
        }
    }
    
    /*
     * Fires alpha particles.
     * The number of alpha particles fired is based on clock ticks and intensity.
     * Each alpha particle is fired from a random location along the gun's nozzle.
     */
    private void fireAlphaParticles( ClockEvent clockEvent ) {
        
        // Determine how many alpha particles to fire.
        double ticks = clockEvent.getSimulationTimeChange();
        int numberOfAlphaParticles = (int) ( _alphaParticlesIntensity * ticks * ALPHA_PARTICLES_PER_CLOCK_TICK );
        if ( numberOfAlphaParticles == 0 && _alphaParticlesIntensity > 0 ) {
            numberOfAlphaParticles = 1;
        }

        // Fire alpha particles
        System.out.println( "firing " + numberOfAlphaParticles + " alpha particles" );//XXX
        for ( int i = 0; i < numberOfAlphaParticles; i++ ) {
            
            // Pick a randon location along the gun's nozzle width
            Point2D position = getRandomNozzlePoint();
            
            // Direction of alpha particle is same as gun's orientation.
            double direction = _orientation;
            
            // Create the alpha particle
            AlphaParticle alphaParticle = new AlphaParticle( position, direction );
            
            // Fire the alpha particle
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

    /*
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

    /*
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
