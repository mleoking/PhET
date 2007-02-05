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
import java.util.Random;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.enums.GunMode;
import edu.colorado.phet.hydrogenatom.enums.LightType;
import edu.colorado.phet.hydrogenatom.event.GunFiredEvent;
import edu.colorado.phet.hydrogenatom.event.GunFiredListener;
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
public class Gun extends FixedObject implements ModelElement {
    
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
    
    private static final double DEFAULT_WAVELENGTH = VisibleColor.MIN_WAVELENGTH;
    private static final double DEFAULT_LIGHT_INTENSITY = 0;
    private static final double DEFAULT_ALPHA_PARTICLE_INTENSITY = 0;
    
    // probability that a "white light" photon's wavelength will one that causes a state transition
    private static final double TRANSITION_WAVELENGTHS_WEIGHT = 0.40; // 1.0 = 100%
    
    // probability that the gun will fire from it's center
    private static final double CENTER_FIRE_PROBABILITY = 0.10; // 1.0 = 100%
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private boolean _enabled; // is the gun on or off?
    private GunMode _mode; // is the gun firing photons or alpha particles?
    private double _nozzleWidth; // width of the beam
    private LightType _lightType; // type of light (white or monochromatic)
    private double _lightIntensity; // intensity of the light, 0.0-1.0
    private double _wavelength; // wavelength of the light
    private double _minWavelength, _maxWavelength; // range of wavelength
    private double _alphaParticlesIntensity; // intensity of the alpha particles, 0.0-1.0
    
    private int _maxParticles; // particles in the animation box when gun intensity is 100%
    private double _dtPerGunFired; // dt between each firing of the gun
    private double _dtSinceGunFired; // dt since the gun was last fired
    
    private double[] _transitionWavelengths; // wavelengths that cause a state transition
    private Random _randomWavelengthType; // for deciding whether to generate a transmission or visible wavelength
    private Random _randomTransitionWavelength; // random number generator for transmission wavelengths
    private Random _randomVisibleWavelength; // random number generator for visible wavelengths
    private Random _randomPosition; // random number generator for photon position
    private Random _randomCenterFire; // determines whether to fire from center of gun
    
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
    public Gun( Point2D position, double orientation, double nozzleWidth, double minWavelength, double maxWavelength ) {
        super( position, orientation );
        
        if ( nozzleWidth <= 0 ) {
            throw new IllegalArgumentException( "invalid nozzleWidth: " + nozzleWidth );
        }
        
        _enabled = false;
        _mode = GunMode.PHOTONS;
        _nozzleWidth = nozzleWidth;
        _lightType = LightType.MONOCHROMATIC;
        _lightIntensity = DEFAULT_LIGHT_INTENSITY;
        _wavelength = DEFAULT_WAVELENGTH;
        _minWavelength = minWavelength;
        _maxWavelength = maxWavelength;
        _alphaParticlesIntensity = DEFAULT_ALPHA_PARTICLE_INTENSITY;
      
        _randomWavelengthType = new Random();
        _randomTransitionWavelength = new Random();
        _randomVisibleWavelength = new Random();
        _randomPosition = new Random();
        _randomCenterFire = new Random();
        
        _dtSinceGunFired = 0;
        setMaxParticles( 20 );
        
        _listenerList = new EventListenerList();
        
        _transform = new AffineTransform();
        
        // Get transition wavelengths for state 1, which are all UV.
        _transitionWavelengths = BohrModel.getTransitionWavelengths( _minWavelength, VisibleColor.MIN_WAVELENGTH );
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
    
    public void setMode( GunMode mode ) {
        if ( mode != _mode ) {
            _mode = mode;
            notifyObservers( PROPERTY_MODE );
        }
    }
    
    public GunMode getMode() {
        return _mode;
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

    public void setLightType( LightType lightType ) {
        if ( lightType != LightType.WHITE && lightType != LightType.MONOCHROMATIC ) {
            throw new IllegalArgumentException( "invalid lightType: " + lightType );
        }
        if ( lightType != _lightType ) {
            _lightType = lightType;
            notifyObservers( PROPERTY_LIGHT_TYPE );
        }
    }
    
    public LightType getLightType() {
        return _lightType;
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
    
    public void setMaxParticles( int maxParticles ) {
        _maxParticles = maxParticles;
        _dtPerGunFired = ( HAConstants.ANIMATION_BOX_SIZE.height / HAConstants.PHOTON_INITIAL_SPEED ) / maxParticles;
    }
    
    public int getMaxParticles() {
        return _maxParticles;
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
        return ( _mode == GunMode.PHOTONS );
    }
    
    /**
     * Is the gun in the mode to fire alpha particles?
     * 
     * @return true or false
     */
    public boolean isAlphaParticlesMode() {
        return ( _mode == GunMode.ALPHA_PARTICLES );
    }
    
    /**
     * Is the gun configured to fire white light?
     * 
     * @return true or false
     */
    public boolean isWhiteLightType() {
        return ( _lightType == LightType.WHITE );
    }
    
    /**
     * Is the gun configured to fire monochromatic light?
     * 
     * @return true or false
     */
    public boolean isMonochromaticLightType() {
        return ( _lightType == LightType.MONOCHROMATIC );   
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
            if ( _mode == GunMode.PHOTONS ) {
                if ( _lightType == LightType.WHITE ) {
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
                double intensity = ( _mode == GunMode.PHOTONS ) ? _lightIntensity : _alphaParticlesIntensity;
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
     * Gets a wavelength that would be appropriate for a new photon.
     * <p>
     * For monochromatic light, we simply use the value of the gun's
     * monochromatic wavelength.
     * <p>
     * For white light, the wavelength is randomly chosen.
     * Instead of simply picking a wavelength from the gun's entire range,
     * we give a higher weight to those wavelengths that are would cause 
     * a transition from state 1 to some other state.  We consider only
     * the wavelengths relevant to state=1 because all of the other 
     * transitions are very improbably in practice. This increases the
     * probability that the photon we fire will interact with the atom.
     * 
     * @return double
     */
    private double getRandomWavelength() {

        double wavelength = 0;

        if ( _lightType == LightType.MONOCHROMATIC ) {
            wavelength = _wavelength;
        }
        else { 
            /* white light */
            if ( _randomWavelengthType.nextDouble() < TRANSITION_WAVELENGTHS_WEIGHT ) {
                // choose a random transition wavelength
                int i = (int) ( _randomTransitionWavelength.nextDouble() * _transitionWavelengths.length );
                wavelength = _transitionWavelengths[i];
            }
            else {
                // choose a random visible wavelength
                wavelength = ( _randomVisibleWavelength.nextDouble() * ( VisibleColor.MAX_WAVELENGTH - VisibleColor.MIN_WAVELENGTH ) ) + VisibleColor.MIN_WAVELENGTH;
            }
        }
        
        assert( wavelength >= _minWavelength && wavelength <= _maxWavelength );
        return wavelength;
    }
    
    /*
     * Gets a random point along the gun's nozzle.
     * This is based on the nozzle width, gun position, and gun orientation.
     * 
     * @return Point2D
     */
    private Point2D getRandomNozzlePoint() {
        
        // Start with the gun's origin at zero, gun pointing to the right
        double x = 1;
        double y = 0;
        if ( _randomCenterFire.nextDouble() > CENTER_FIRE_PROBABILITY ) {
            y = ( _randomPosition.nextDouble() * _nozzleWidth ) - ( _nozzleWidth / 2 );
        }

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
     * Fires photons or alpha particles each time that time advances.
     * If the gun is disabled, do nothing.
     */
    public void stepInTime( double dt ) {
        if ( _enabled ) {
            if ( _mode == GunMode.PHOTONS && _lightIntensity > 0 ) {
                firePhoton( dt );
            }
            else if ( _mode == GunMode.ALPHA_PARTICLES && _alphaParticlesIntensity > 0 ) {
                fireAlphaParticle( dt );
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Fire photons and alpha particles
    //----------------------------------------------------------------------------
    
    /**
     * Fires one photon from the center of the gun.
     * 
     * @param wavelength
     */
    public void fireOnePhotonFromCenter( final double wavelength ) {
        
        // Fire from the center of the gun's nozzle
        Point2D position = new Point2D.Double( 1, 0 );
        
        // Rotate by the gun's orientation
        _transform.setToIdentity();
        _transform.rotate( getOrientation() );
        _transform.transform( position, position );
        
        // Translate by the gun's position
        position.setLocation( position.getX() + getX(), position.getY() + getY() );
        
        // Other photon properties
        double orientation = getOrientation();
        double speed = HAConstants.PHOTON_INITIAL_SPEED;

        // Create the photon
        Photon photon = new Photon( wavelength, position, orientation, speed );

        // Fire the photon
        GunFiredEvent event = new GunFiredEvent( this, photon );
        firePhotonFiredEvent( event );
    }
    
    /*
     * Fires a photon when it's time to do so.
     * Each photon is fired from a random location along the gun's nozzle.
     */
    private void firePhoton( double dt ) {

        _dtSinceGunFired += ( _lightIntensity * dt );
        
        // Fire a photon?
        if ( _dtSinceGunFired >= _dtPerGunFired ) {
            
            _dtSinceGunFired = _dtSinceGunFired % _dtPerGunFired;
            
            // Photon properties
            Point2D position = getRandomNozzlePoint();
            double orientation = getOrientation();
            double speed = HAConstants.PHOTON_INITIAL_SPEED;
            double wavelength = getRandomWavelength();

            // Create the photon
            Photon photon = new Photon( wavelength, position, orientation, speed );

            // Fire the photon
            GunFiredEvent event = new GunFiredEvent( this, photon );
            firePhotonFiredEvent( event );
        }
    }
    
    /*
     * Fires an alpha particle when it's time to do so.
     * Each alpha particle is fired from a random location along the gun's nozzle.
     */
    private void fireAlphaParticle( double dt ) {

        _dtSinceGunFired += ( _alphaParticlesIntensity * dt );

        if ( _dtSinceGunFired >= _dtPerGunFired ) {

            _dtSinceGunFired = _dtSinceGunFired % _dtPerGunFired;
            
            // Pick a randon location along the gun's nozzle width
            Point2D position = getRandomNozzlePoint();

            // Direction of alpha particle is same as gun's orientation.
            double orientation = getOrientation();
            
            double speed = HAConstants.ALPHA_PARTICLE_INITIAL_SPEED;

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
     * Fires a GunFiredEvent when a photon is fired.
     *
     * @param event the event
     */
    private void firePhotonFiredEvent( GunFiredEvent event ) {
        assert( event.getPhoton() != null );
        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == GunFiredListener.class ) {
                ( (GunFiredListener)listeners[i + 1] ).photonFired( event );
            }
        }
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
