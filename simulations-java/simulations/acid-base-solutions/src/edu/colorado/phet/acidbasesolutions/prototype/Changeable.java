package edu.colorado.phet.acidbasesolutions.prototype;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public abstract class Changeable {
    
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

