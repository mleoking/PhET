package edu.colorado.phet.common.phetcommon.model;

/**
 * Property<T> can be used to represent a value of type T in a MVC style pattern.  It remembers its default value and can be reset.
 * The wrapped type T should be immutable, or at least protected from external modification.
 * Notifications are sent to observers when they register with addObserver, and when the value changes.
 *
 * @author Sam Reid
 * @author Chris Malley
 */
public class Property<T> extends SettableProperty<T> {
    private T value;
    private final T initialValue;

    public Property( T value ) {
        this.initialValue = value;
        this.value = value;
    }

    public void reset() {
        setValue( initialValue );
    }

    @Override
    public T getValue() {
        return value;
    }

    public void setValue( T value ) {
        if ( !this.value.equals( value ) ) {
            this.value = value;
            notifyObservers();
        }
    }

    public T getInitialValue() {
        return initialValue;
    }

}
