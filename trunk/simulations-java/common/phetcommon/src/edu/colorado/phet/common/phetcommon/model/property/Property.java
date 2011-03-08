// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Property<T> can be used to represent a value of type T in a MVC style pattern.  It remembers its default value and can be reset.
 * The wrapped type T should be immutable, or at least protected from external modification.
 * Notifications are sent to observers when they register with addObserver, and when the value changes.
 * Listeners can alternative register for callbacks that provide the new value with addObserver(VoidFunction1<T>).
 *
 * @author Sam Reid
 * @author Chris Malley
 */
public class Property<T> extends SettableProperty<T> {
    private T value;
    private final T initialValue;
    private ArrayList<VoidFunction1<T>> valueObservers = new ArrayList<VoidFunction1<T>>();//Listeners that also receive the new value in the callback

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
            notifyObservers();//Notify SimpleObservers
            notifyValueObservers();//Notify typed callback listeners
        }
    }

    /**
     * Additionally notify observers that receive the value itself in the callback.
     */
    private void notifyValueObservers() {
        for ( VoidFunction1<T> observer : new ArrayList<VoidFunction1<T>>( valueObservers ) ) {//Iterate on a copy of the observer list to avoid ConcurrentModificationException, see #2741
            observer.apply( getValue() );
        }
    }

    public void addObserver( VoidFunction1<T> observer ) {
        valueObservers.add( observer );
    }

    public void removeObserver( VoidFunction1<T> observer ) {
        valueObservers.remove( observer );
    }

    /**
     * Getter for the initial value, protected to keep the API for Property as simple as possible for typical usage, but
     * possible to override for applications which require knowledge about the initial value such as Gravity and Orbits.
     *
     * @return the initial value
     */
    protected T getInitialValue() {
        return initialValue;
    }
}