// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property3;

/**
 * Keeps track of when values have changed, used in Observable instances to make sure that notifications are only sent out when necessary.
 * This class is intended for us internally by the property3 package, but left public for reuse in other packages where necessary.
 *
 * @author Sam Reid
 */
public class Notifier<T> implements Gettable<T> {
    private T value;
    private T initialValue;

    public Notifier( T value ) {
        this.value = value;
        this.initialValue = value;
    }

    //Sets the specified value and returns true if the value changed
    public boolean set( T value ) {
        boolean changed = !value.equals( this.value );
        this.value = value;
        return changed;
    }

    public T get() {
        return value;
    }

    //Determine the initial value
    public T getInitialValue() {
        return initialValue;
    }
}
