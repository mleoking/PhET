// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Notifier class that assumes listeners are of type VoidFunction1<T>, uses composite pattern on AbstractNotifier
 * to reuse code in notifier while providing a simpler interface.
 *
 * @author Sam Reid
 */
public class Notifier<T> {
    //The wrapped AbstractNotifier used to store listeners and fire events
    private AbstractNotifier<VoidFunction1<T>> notifier = new AbstractNotifier<VoidFunction1<T>>();

    //Signify that the value has changed, passing the T value to all listeners
    public void updateListeners( final T value ) {
        notifier.updateListeners( new VoidFunction1<VoidFunction1<T>>() {
            public void apply( VoidFunction1<T> listener ) {
                listener.apply( value );
            }
        } );
    }

    public void addListener( VoidFunction1<T> listener ) {
        notifier.addListener( listener );
    }

    public void removeListener( VoidFunction1<T> listener ) {
        notifier.removeListener( listener );
    }
}
