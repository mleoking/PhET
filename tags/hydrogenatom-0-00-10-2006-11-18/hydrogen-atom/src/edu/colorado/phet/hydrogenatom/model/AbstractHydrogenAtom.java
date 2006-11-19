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

import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.hydrogenatom.view.particle.PhotonNode;

/**
 * AbstractHydrogenAtom is the base class for all hydrogen atom models.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractHydrogenAtom extends FixedObject implements ModelElement {

    //----------------------------------------------------------------------------
    // Public class data
    //----------------------------------------------------------------------------
    
    /* how close a photon and electron must be for the photon to be absorbed */
    public static int ABSORPTION_CLOSENESS = (int)( PhotonNode.DIAMETER / 2 );
    
    public static final String PROPERTY_ELECTRON_STATE = "electronState";
    public static final String PROPERTY_ELECTRON_OFFSET = "electronOffset";
    public static final String PROPERTY_ATOM_DESTROYED = "atomDestroyed";
    public static final String PROPERTY_ATOM_IONIZED = "atomIonized";
    
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
    // Particle motion
    //----------------------------------------------------------------------------

    /**
     * Moves a photon.
     * In the default implementation, the atom has no influence on the photon's movement.
     * 
     * @param photon
     * @param dt
     */
    public void movePhoton( Photon photon, double dt ) {
        double speed = photon.getSpeed();
        double distance = speed * dt;
        double direction = photon.getOrientation();
        double dx = Math.cos( direction ) * distance;
        double dy = Math.sin( direction ) * distance;
        double x = photon.getX() + dx;
        double y = photon.getY() + dy;
        photon.setPosition( x, y );
    }

    /**
     * Moves an alpha particle.
     * In the default implementation, the atom has no influence on the alpha particle's movement.
     * 
     * @param alphaParticle
     * @param dt
     */
    public void moveAlphaParticle( AlphaParticle alphaParticle, double dt ) {
        double speed = alphaParticle.getSpeed();
        double distance = speed * dt;
        double direction = alphaParticle.getOrientation();
        double dx = Math.cos( direction ) * distance;
        double dy = Math.sin( direction ) * distance;
        double x = alphaParticle.getX() + dx;
        double y = alphaParticle.getY() + dy;
        alphaParticle.setPosition( x, y );
    }
    
    /**
     * Determines if two points collide.
     * Any distance between the points that is <= threshold
     * is considered a collision.
     * 
     * @param p1
     * @param p2
     * @param threshold
     * @return true or false
     */
    protected static boolean pointsCollide( Point2D p1, Point2D p2, double threshold ) {
        return p1.distance( p2 ) <= threshold;
    }

    //----------------------------------------------------------------------------
    // ModelElement default implementation
    //----------------------------------------------------------------------------

    /**
     * Called when time has advanced by some delta.
     * The default implementation does nothing.
     */
    public void stepInTime( double dt ) {}
    
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
