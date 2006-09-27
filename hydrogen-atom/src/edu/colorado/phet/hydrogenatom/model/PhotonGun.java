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
import java.util.EventListener;
import java.util.EventObject;

import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.ClockListener;

/**
 * PhotonGun is the model of the photon gun.
 * It is located at a point in space with a specific orientation.
 * It shoots a beam of light that has width, wavelength and intensity.
 * The width of the beam is immutable; all other properties are mutable.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PhotonGun extends StationaryObject implements ClockListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_ORIENTATION = "orientation";
    
    private static final int PHOTONS_PER_CLOCK_TICK = 1;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private boolean _enabled; // turns the gun on and off
    private double _orientation; // direction the gun points, in degrees
    private double _beamWidth; // width of the beam
    private double _wavelength; // wavelength of the beam
    private double _intensity; // intensity of the beam, 0.0-1.0
    private EventListenerList _listenerList;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public PhotonGun( Point2D position, double orientation, double beamWidth, double wavelength, double intensity ) {
        super( position );
        
        if ( beamWidth <= 0 ) {
            throw new IllegalArgumentException( "invalid beamWidth: " + beamWidth );
        }
        if ( wavelength < 0 ) {
            throw new IllegalArgumentException( "invalid wavelength: " + wavelength );
        }
        if ( intensity < 0 || intensity > 1 ) {
            throw new IllegalArgumentException( "invalid intensity: " + intensity );
        }
        
        _orientation = orientation;
        _beamWidth = beamWidth;
        _wavelength = wavelength;
        _intensity = intensity;
        
        _listenerList = new EventListenerList();
    }
    
    //----------------------------------------------------------------------------
    // Mutators & accessors
    //----------------------------------------------------------------------------
    
    public double getOrientation() {
        return _orientation;
    }
    
    public void setOrientation( double orientation ) {
        if ( orientation != _orientation ) {
            _orientation = orientation;
            notifyObservers( PROPERTY_ORIENTATION );
        }
    }
    
    public double getBeamWidth() {
        return _beamWidth;
    }
    
    public void setEnabled( boolean enabled ) {
        _enabled = enabled;
    }
    
    public boolean isEnabled() {
        return _enabled;
    }
    
    public void setWavelength( double wavelength ) {
        if ( wavelength < 0 ) {
            throw new IllegalArgumentException( "invalid wavelength: " + wavelength );
        }
        _wavelength = wavelength;
    }
    
    public double getWavelength() {
        return _wavelength;
    }
    
    public void setIntensity( double intensity ) {
        if ( intensity < 0 || intensity > 1 ) {
            throw new IllegalArgumentException( "invalid intensity: " + intensity );
        }
        _intensity = intensity;
    }
    
    public double getIntensity() {
        return _intensity;
    }
    
    //----------------------------------------------------------------------------
    // ClockListener implementation
    //----------------------------------------------------------------------------
    
    public void simulationTimeChanged( ClockEvent clockEvent ) {
        if ( _enabled && _intensity != 0 ) {
            
            // Determine how many photons to fire.
            double ticks = clockEvent.getSimulationTimeChange();
            int numberOfPhotons = (int) ( _intensity * ticks / PHOTONS_PER_CLOCK_TICK );
            if ( numberOfPhotons == 0 ) {
                numberOfPhotons = 1;
            }
            
            // Fire photons
            System.out.println( "firing " + numberOfPhotons + " photons" );//XXX
            for ( int i = 0; i < numberOfPhotons; i++ ) {
                //XXX pick a randon location along the gun's nozzle width
                double x = getPositionRef().getX();
                double y = getPositionRef().getY();
                Point2D position = new Point2D.Double( x, y );
                Photon photon = new Photon( position, _orientation, _wavelength );
                PhotonFiredEvent event = new PhotonFiredEvent( this, photon );
                firePhotonFiredEvent( event );
            }
        }
    }

    public void clockTicked( ClockEvent clockEvent ) {}

    public void clockStarted( ClockEvent clockEvent ) {}

    public void clockPaused( ClockEvent clockEvent ) {}

    public void simulationTimeReset( ClockEvent clockEvent ) {}
    
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
}
