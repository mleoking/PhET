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
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.model.ModelElement;

/**
 * AbstractHydrogenAtom is the base class for all hydrogen atom models.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractHydrogenAtom extends FixedObject implements ModelElement {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private EventListenerList _listenerList;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     * 
     * @param position
     * @param orientation
     */
    public AbstractHydrogenAtom( Point2D position, double orientation ) {
        super( position, orientation );
        _listenerList = new EventListenerList();
    }

    //----------------------------------------------------------------------------
    // Overrides
    //----------------------------------------------------------------------------
    
    public void setPosition( double x, double y ) {
        throw new UnsupportedOperationException( "model does not support moving atoms!" );
    }
    
    //----------------------------------------------------------------------------
    // Collision detection
    //----------------------------------------------------------------------------

    /**
     * Detects a collision with a photon.
     * The default implementation does nothing.
     * It is up to the implementer to determine if there is a collision,
     * and (if so) to take the proper action.
     */
    public void detectCollision( Photon photon ) {}

    /**
     * Detects a collision with an alpha particle.
     * The default implementation does nothing.
     * It is up to the implementer to determine if there is a collision,
     * and (if so) to take the proper action.
     */
    public void detectCollision( AlphaParticle alphaParticle ) {}

    //----------------------------------------------------------------------------
    // ModelElement default implementation
    //----------------------------------------------------------------------------

    /**
     * Called when time has advanced by some delta.
     * The default implementation does nothing.
     */
    public void stepInTime( double dt ) {}

    //----------------------------------------------------------------------------
    // Alpha particle movement
    //----------------------------------------------------------------------------
    
    /**
     * Moves the alpha particle in time.
     * In the default implementation, the atom has no influence on the
     * alpha particle's speed or orientation.
     * 
     * @param alphaParticle
     * @param dt
     */
    public void move( AlphaParticle alphaParticle, double dt ) {
        double speed = alphaParticle.getSpeed();
        double distance = speed * dt;
        double direction = alphaParticle.getOrientation();
        double dx = Math.cos( direction ) * distance;
        double dy = Math.sin( direction ) * distance;
        double x = alphaParticle.getX() + dx;
        double y = alphaParticle.getY() + dy;
        alphaParticle.setPosition( x, y );
    }
    
    //----------------------------------------------------------------------------
    // PhotonAbsorbedListener
    //----------------------------------------------------------------------------

    /**
     * PhotonAbsorbedListener is the interface implemented by all listeners
     * who wish to be informed when a photon is absorbed.
     */
    public interface PhotonAbsorbedListener extends EventListener {

        public void photonAbsorbed( PhotonAbsorbedEvent event );
    }

    /**
     * PhotonAbsorbedEvent indicates that a photon has been absorbed.
     */
    public class PhotonAbsorbedEvent extends EventObject {

        private Photon _photon;

        public PhotonAbsorbedEvent( Object source, Photon photon ) {
            super( source );
            assert ( photon != null );
            _photon = photon;
        }

        public Photon getPhoton() {
            return _photon;
        }
    }

    /**
     * Adds an PhotonAbsorbedListener.
     *
     * @param listener the listener
     */
    public void addPhotonAbsorbedListener( PhotonAbsorbedListener listener ) {
        _listenerList.add( PhotonAbsorbedListener.class, listener );
    }

    /**
     * Removes an PhotonAbsorbedListener.
     *
     * @param listener the listener
     */
    public void removePhotonAbsorbedListener( PhotonAbsorbedListener listener ) {
        _listenerList.remove( PhotonAbsorbedListener.class, listener );
    }

    /*
     * Fires a PhotonAbsorbedEvent when a photon is absorbed.
     *
     * @param event the event
     */
    protected void firePhotonAbsorbedEvent( PhotonAbsorbedEvent event ) {
        assert ( event.getPhoton() != null );
        Object[] listeners = _listenerList.getListenerList();
        for ( int i = 0; i < listeners.length; i += 2 ) {
            if ( listeners[i] == PhotonAbsorbedListener.class ) {
                ( (PhotonAbsorbedListener) listeners[i + 1] ).photonAbsorbed( event );
            }
        }
    }

    //----------------------------------------------------------------------------
    // PhotonEmittedListener
    //----------------------------------------------------------------------------

    /**
     * PhotonEmittedListener is the interface implemented by all listeners
     * who wish to be informed when a photon is emitted.
     */
    public interface PhotonEmittedListener extends EventListener {

        public void photonEmitted( PhotonEmittedEvent event );
    }

    /**
     * PhotonEmittedEvent indicates that a photon has been emitted.
     */
    public class PhotonEmittedEvent extends EventObject {

        private Photon _photon;

        public PhotonEmittedEvent( Object source, Photon photon ) {
            super( source );
            assert ( photon != null );
            _photon = photon;
        }

        public Photon getPhoton() {
            return _photon;
        }
    }

    /**
     * Adds an EmissionListener.
     *
     * @param listener the listener
     */
    public void addPhotonEmittedListener( PhotonEmittedListener listener ) {
        _listenerList.add( PhotonEmittedListener.class, listener );
    }

    /**
     * Removes an EmissionListener.
     *
     * @param listener the listener
     */
    public void removePhotonEmittedListener( PhotonEmittedListener listener ) {
        _listenerList.remove( PhotonEmittedListener.class, listener );
    }

    /*
     * Fires a PhotonEmittedEvent when a photon is emitted.
     *
     * @param event the event
     */
    protected void firePhotonEmittedEvent( PhotonEmittedEvent event ) {
        assert ( event.getPhoton() != null );
        Object[] listeners = _listenerList.getListenerList();
        for ( int i = 0; i < listeners.length; i += 2 ) {
            if ( listeners[i] == PhotonEmittedListener.class ) {
                ( (PhotonEmittedListener) listeners[i + 1] ).photonEmitted( event );
            }
        }
    }
}
