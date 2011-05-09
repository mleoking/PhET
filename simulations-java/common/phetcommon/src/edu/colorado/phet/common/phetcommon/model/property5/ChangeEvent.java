// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property5;

/**
 * Event that signifies both the old and new values.
 *
 * @author Sam Reid
 */
public class ChangeEvent<T> {
    public final T value;
    public final T oldValue;

    public ChangeEvent( T value, T oldValue ) {
        this.value = value;
        this.oldValue = oldValue;
    }

    @Override public String toString() {
        return "value = " + value + ", oldValue = " + oldValue;
    }
}
