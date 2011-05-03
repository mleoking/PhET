// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property3;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;

/**
 * Provides reusable implementation of a list of listeners that can be notified on any kind of event.
 * This should be used as an inner implementation for Observable classes
 *
 * @author Sam Reid
 */
public class ListenerList<T extends VoidFunction0> {
    private ArrayList<T> listeners = new ArrayList<T>();

    public void notifyListeners() {
        for ( T listener : new ArrayList<T>( listeners ) ) {
            notifyListener( listener );
        }
    }

    protected void notifyListener( T listener ) {
        listener.apply();
    }

    //Add a listener and send out notifications to the listener
    public void add( T observer ) {
        listeners.add( observer );
        notifyListener( observer );
    }

    public void remove( T observer ) {
        listeners.remove( observer );
    }
}
