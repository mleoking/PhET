// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction2;

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
    private ArrayList<VoidFunction1<T>> newValueObservers = new ArrayList<VoidFunction1<T>>();//Listeners that receive the new value in the callback
    private ArrayList<VoidFunction2<T,T>> newAndOldValueObservers = new ArrayList<VoidFunction2<T,T>>();//Listeners that receive the new and old values in the callback

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
            T oldValue = this.value;
            this.value = value;
            notifyObservers();//Notify SimpleObservers
            notifyNewValueObservers( value );//Notify typed callback listeners
            notifyNewAndOldValueObservers( value, oldValue );
        }
    }

    /*
     * Notify observers that receive new value in the callback.
     */
    private void notifyNewValueObservers( T newValue ) {
        for ( VoidFunction1<T> observer : new ArrayList<VoidFunction1<T>>( newValueObservers ) ) {//Iterate on a copy of the observer list to avoid ConcurrentModificationException, see #2741
            observer.apply( newValue );
        }
    }

    /**
     * Adds an observer that will receive the new value in the callback.
     * Unlike SimpleObservers, this observer does not receive immediate notification when it's added,
     * because there is no notion of "new value".
     * @param observer
     */
    public void addObserver( VoidFunction1<T> observer ) {
        newValueObservers.add( observer );
    }

    public void removeObserver( VoidFunction1<T> observer ) {
        newValueObservers.remove( observer );
    }

    /*
     * Notify observers that receive the new and old values in the callback.
     */
    private void notifyNewAndOldValueObservers( T newValue, T oldValue ) {
        for ( VoidFunction2<T,T> observer : new ArrayList<VoidFunction2<T,T>>( newAndOldValueObservers ) ) {//Iterate on a copy of the observer list to avoid ConcurrentModificationException, see #2741
            observer.apply( newValue, oldValue );
        }
    }

    /**
     * Adds an observer that will receive the new and old value in the callback.
     * Unlike SimpleObservers, this observer does not receive immediate notification when it's added,
     * because there is no notion of "new value" or "old value".
     * @param observer
     */
    public void addObserver( VoidFunction2<T,T> observer ) {
        newAndOldValueObservers.add( observer );
    }

    public void removeObserver( VoidFunction2<T,T> observer ) {
        newAndOldValueObservers.remove( observer );
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

    // test
    public static void main( String[] args ) {
        final Property<Boolean> enabled = new Property<Boolean>( true );
        enabled.addObserver( new SimpleObserver() {
            public void update() {
                System.out.println( "SimpleObserver enabled=" + enabled.getValue() );
            }
        } );
        enabled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean newValue ) {
                System.out.println( "VoidFunction1 newValue=" + newValue );
            }
        } );
        enabled.addObserver( new VoidFunction2<Boolean,Boolean>() {
            public void apply( Boolean newValue, Boolean oldValue ) {
                System.out.println( "VoidFunction1 newValue=" + newValue + " oldValue=" + oldValue );
            }
        } );
        enabled.setValue( !enabled.getValue() );
    }
}