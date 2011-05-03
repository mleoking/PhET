// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property3;

/**
 * Keeps track of when values have changed, used in Observable instances to make sure that notifications are only sent out when necessary.
 *
 * @author Sam Reid
 */
public class Notifier<T> implements Gettable<T> {
    private T value;

    public Notifier( T value ) {
        this.value = value;
    }

    public boolean change( T value ) {
        boolean changed = !value.equals( this.value );
        this.value = value;
        return changed;
    }

    public T get() {
        return value;
    }
}
