// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.prototype;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * Base class for things that are changeable.
 * Listeners will be notified that some property changed, but won't know which property.
 * This is sufficient for our prototype.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
abstract class Changeable {
    
    private final EventListenerList listenerList;
    
    public Changeable() {
        listenerList = new EventListenerList();
    }

    public void addChangeListener( ChangeListener listener ) {
        listenerList.add( ChangeListener.class, listener );
    }
    
    public void removeChangeListener( ChangeListener listener ) {
        listenerList.remove( ChangeListener.class, listener );
    }
    
    protected void fireStateChanged() {
        ChangeEvent event = new ChangeEvent( this );
        for ( ChangeListener listener : listenerList.getListeners( ChangeListener.class ) ) {
            listener.stateChanged( event );
        }
    }
}

