// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property3;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Observable property that indicates whether its argument (also observable) is equal to the specified value
 *
 * @author Sam Reid
 */
public class ValueEquals<T> implements GettableObservable0<Boolean> {//No set defined on ValueEquals since undefined what to set the value to if "false"
    private final GettableObservable0<T> property;
    private final T value;

    //Keep track of state and don't send out notifications unless the values changes
    private final Notifier<Boolean> notifier;
    private ListenerList<VoidFunction0> listeners = new ListenerList<VoidFunction0>( new VoidFunction1<VoidFunction0>() {
        public void apply( VoidFunction0 listener ) {
            listener.apply();
        }
    } );

    public ValueEquals( GettableObservable0<T> property, T value ) {
        this.property = property;
        this.value = value;
        notifier = new Notifier<Boolean>( get() );
        new RichListener() {
            public void apply() {
                if ( notifier.set( get() ) ) {
                    listeners.notifyListeners();
                }
            }
        }.observe( property );
    }

    //Returns true if the wrapped observable is equal to the specified value
    public Boolean get() {
        return property.get() == value;
    }

    public void addObserver( VoidFunction0 observer ) {
        listeners.add( observer );
    }

    public void removeObserver( VoidFunction0 observer ) {
        listeners.remove( observer );
    }
}