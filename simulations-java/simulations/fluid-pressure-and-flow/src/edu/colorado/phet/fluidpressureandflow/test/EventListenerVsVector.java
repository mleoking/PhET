// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.test;

import java.util.Vector;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * @author Sam Reid
 */
public class EventListenerVsVector {
    public static class UseEventListener {
        private final EventListenerList listeners = new EventListenerList();

        public void addChangeListener( ChangeListener listener ) {
            listeners.add( ChangeListener.class, listener );
        }

        public void removeChangeListener( ChangeListener listener ) {
            listeners.remove( ChangeListener.class, listener );
        }

        private void fireStateChanged() {
            ChangeEvent event = new ChangeEvent( this );
            for ( ChangeListener listener : listeners.getListeners( ChangeListener.class ) ) {
                listener.stateChanged( event );
            }
        }
    }

    public static class UseVector {
        private final Vector<ChangeListener> listeners = new Vector<ChangeListener>();

        public void addChangeListener( ChangeListener listener ) {
            listeners.add( listener );
        }

        public void removeChangeListener( ChangeListener listener ) {
            listeners.remove( listener );
        }

        private void fireStateChanged() {
            ChangeEvent event = new ChangeEvent( this );
            for ( ChangeListener listener : listeners ) {
                listener.stateChanged( event );
            }
        }
    }

    public static class UseSimpleObservable {
        private final SimpleObservable listeners = new SimpleObservable();

        public void addChangeListener( SimpleObserver listener ) {
            listeners.addObserver( listener );
        }

        public void removeChangeListener( SimpleObserver listener ) {
            listeners.removeObserver( listener );
        }

        private void fireStateChanged() {
            listeners.notifyObservers();
        }
    }

    public static class UseEventSimpleObservable {
        private final EventSimpleObservable listeners = new EventSimpleObservable();

        public void addChangeListener( ChangeListener listener ) {
            listeners.addObserver( listener );
        }

        public void removeChangeListener( ChangeListener listener ) {
            listeners.removeObserver( listener );
        }

        private void fireStateChanged() {
            listeners.notifyObservers( new ChangeEvent( this ) );
        }
    }
}
