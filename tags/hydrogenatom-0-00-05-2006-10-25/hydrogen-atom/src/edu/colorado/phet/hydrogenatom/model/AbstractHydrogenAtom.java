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
public abstract class AbstractHydrogenAtom extends DynamicObject implements ModelElement {

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
    // AbsorptionListener
    //----------------------------------------------------------------------------
    
    /**
     * AbsorptionListener is the interface implemented by all listeners
     * who wish to be informed when a photon is absorbed.
     */
    public interface AbsorptionListener extends EventListener {
        public void photonAbsorbed( AbsorptionEvent event );
    }

    /**
     * AbsorptionEvent indicates that a photon has been absorbed.
     */
    public class AbsorptionEvent extends EventObject {

        private Photon _photon;

        public AbsorptionEvent( Object source, Photon photon ) {
            super( source );
            assert( photon != null );
            _photon = photon;
        }
        
        public Photon getPhoton() {
            return _photon;
        }
    }
    
    /**
     * Adds an AbsorptionListener.
     *
     * @param listener the listener
     */
    public void addAbsorptionListener( AbsorptionListener listener ) {
        _listenerList.add( AbsorptionListener.class, listener );
    }

    /**
     * Removes an AbsorptionListener.
     *
     * @param listener the listener
     */
    public void removeAbsorptionListener( AbsorptionListener listener ) {
        _listenerList.remove( AbsorptionListener.class, listener );
    }

    /*
     * Fires an AbsorptionEvent when a photon is absorbed.
     *
     * @param event the event
     */
    protected void fireAbsorptionEvent( AbsorptionEvent event ) {
        assert( event.getPhoton() != null );
        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == AbsorptionListener.class ) {
                ( (AbsorptionListener)listeners[i + 1] ).photonAbsorbed( event );
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // EmissionListener
    //----------------------------------------------------------------------------
    
    /**
     * EmissionListener is the interface implemented by all listeners
     * who wish to be informed when a photon is emitted.
     */
    public interface EmissionListener extends EventListener {
        public void photonEmitted( EmissionEvent event );
    }

    /**
     * EmissionEvent indicates that a photon has been emitted.
     */
    public class EmissionEvent extends EventObject {

        private Photon _photon;

        public EmissionEvent( Object source, Photon photon ) {
            super( source );
            assert( photon != null );
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
    public void addEmissionListener( EmissionListener listener ) {
        _listenerList.add( EmissionListener.class, listener );
    }

    /**
     * Removes an EmissionListener.
     *
     * @param listener the listener
     */
    public void removeEmissionListener( EmissionListener listener ) {
        _listenerList.remove( EmissionListener.class, listener );
    }

    /*
     * Fires an EmissionEvent when a photon is emitted.
     *
     * @param event the event
     */
    protected void fireEmissionEvent( EmissionEvent event ) {
        assert( event.getPhoton() != null );
        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == EmissionListener.class ) {
                ( (EmissionListener)listeners[i + 1] ).photonEmitted( event );
            }
        }
    }
}
