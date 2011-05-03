// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property3;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Provides reusable implementation of a list of listeners that can be notified on any kind of event.
 * This should be used as an inner implementation for Observable classes
 *
 * @author Sam Reid
 */
public class ListenerList<T> {//T is the listener type
    private ArrayList<T> listeners = new ArrayList<T>();
    private final VoidFunction1<T> notifyListener;

    //Create a listener list, and pass in the function that will be used to notify the listeners
    public ListenerList( VoidFunction1<T> notifyListener ) {
        this.notifyListener = notifyListener;
    }

    //Notifies the listeners
    public void notifyListeners() {
        //Makes a copy of the list during iteration to avoid ConcurrentModificationExceptions
        for ( T listener : new ArrayList<T>( listeners ) ) {
            notifyListener.apply( listener );
        }
    }

    //Add a listener and send out notifications to the listener
    public void add( T observer ) {
        listeners.add( observer );
        notifyListener.apply( observer );
    }

    public void remove( T observer ) {
        listeners.remove( observer );
    }
}
