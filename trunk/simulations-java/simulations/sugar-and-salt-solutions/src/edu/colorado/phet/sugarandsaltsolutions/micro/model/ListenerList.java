package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * List of listeners, which can be added/removed/notified about any kind of change.  Used in ItemList to listen for addition/removal of items.
 *
 * @author Sam Reid
 */
public class ListenerList<T> {
    private final ArrayList<VoidFunction1<T>> listeners = new ArrayList<VoidFunction1<T>>();

    public void addListener( VoidFunction1<T> listener ) {
        listeners.add( listener );
    }

    public void removeListener( VoidFunction1<T> listener ) {
        listeners.remove( listener );
    }

    public void notifyListeners( T value ) {
        //Iterate on a copy to avoid concurrentmodificationexception
        for ( VoidFunction1<T> listener : new ArrayList<VoidFunction1<T>>( listeners ) ) {
            listener.apply( value );
        }
    }
}