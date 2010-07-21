package edu.colorado.phet.common.phetcommon.model;

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

/**
 * This can be used to represent a value in a MVC style pattern.  It remembers its default value and can be reset.
 * When the value changes, notifications are sent to the observers.
 *
 * @author Sam Reid
 * @author Chris Malley
 */
public class Observable<T> extends SimpleObservable {
    private T value;
    private final T defaultValue;

    public Observable( T value ) {
        this.defaultValue = value;
        this.value = value;
    }

    public void reset() {
        setValue( defaultValue );
    }

    public T getValue() {
        return value;
    }

    public void setValue( T value ) {
        if ( this.value != value ) {
            this.value = value;
            notifyObservers();
        }
    }
}
