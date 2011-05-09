// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property3;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Default implementation of Gettable, Settable and Observable that stores its value and signifies events when the value changes.
 *
 * @author Sam Reid
 */
public class Property<T> extends RichObservable<T> implements GettableSettableObservable0<T> {
    private Notifier<T> notifier;
    private ListenerList<VoidFunction0> listenerList = new ListenerList<VoidFunction0>( new VoidFunction1<VoidFunction0>() {
        public void apply( VoidFunction0 listener ) {
            listener.apply();
        }
    } );

    public Property( T value ) {
        this.notifier = new Notifier<T>( value );
    }

    public T get() {
        return notifier.get();
    }

    public void addObserver( VoidFunction0 observer ) {
        listenerList.add( observer );
    }

    public void removeObserver( VoidFunction0 observer ) {
        listenerList.remove( observer );
    }

    public void set( T value ) {
        if ( notifier.set( value ) ) {
            listenerList.notifyListeners();
        }
    }

    //Sets the value of this property to be its initial value
    public void reset() {
        set( notifier.getInitialValue() );
    }
}
