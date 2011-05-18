// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.model.property;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.util.logging.LoggingUtils;

//TODO: if we're going to have separate listener lists, then absorb functionality of SimpleObservable?

/**
 * This can be used to represent an observable model value in a MVC style pattern. Notifications are sent to observers when they
 * register with addObserver, and also when the value changes (it is up to subclasses to guarantee that notifications are sent when necessary, and not duplicated).
 * <p/>
 * ObservableProperty should be used instead of SettableProperty whenever possible, i.e. when the value needs to be observed but not set.
 *
 * @author Sam Reid
 * @author Chris Malley
 */
public abstract class ObservableProperty<T> implements Cloneable {
    private static final Logger LOGGER = LoggingUtils.getLogger( ObservableProperty.class.getCanonicalName() );

    static {
        // get rid of this to log all of the resource messages
        LOGGER.setLevel( Level.INFO );
    }

    private final ArrayList<SimpleObserver> simpleObservers = new ArrayList<SimpleObserver>();
    private final ArrayList<VoidFunction1<T>> newValueObservers = new ArrayList<VoidFunction1<T>>();//Listeners that receive the new value in the callback
    private final ArrayList<ChangeObserver<T>> newAndOldValueObservers = new ArrayList<ChangeObserver<T>>();//Listeners that receive the new and old values in the callback

    //Store the value that was previously notified so we can prevent sending out notifications when the value didn't actually change
    private T oldValue;

    public ObservableProperty( T oldValue ) {
        this.oldValue = oldValue; //REVIEW discuss implications of this for notify-on-register
    }

    /**
     * Adds a SimpleObserver to observe the value of this instance.
     * If notifyOnAdd is true, also immediately notifies the SimpleObserver,
     * so that the client code is not responsible for doing so.
     * This helps the SimpleObserver to always be synchronized with this instance.
     *
     * @param simpleObserver
     * @param notifyOnAdd
     */
    public void addObserver( SimpleObserver simpleObserver, boolean notifyOnAdd ) {
        simpleObservers.add( simpleObserver );
        if ( notifyOnAdd ) {
            simpleObserver.update();
        }
    }

    //Notifies all 0, 1 and 2 arg listeners.  Clients should call notifyIfChanged
    private void notifyObservers( T value, T oldValue ) {
        notifySimpleObservers();//Notify SimpleObservers
        notifyNewValueObservers( value );//Notify listeners with new value
        notifyNewAndOldValueObservers( value, oldValue );//Notify listeners with both new and old values
    }

    private void notifySimpleObservers() {
        for ( SimpleObserver simpleObserver : new ArrayList<SimpleObserver>( simpleObservers ) ) {
            simpleObserver.update();
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

    public void removeObserver( SimpleObserver observer ) {
        simpleObservers.remove( observer );
    }

    /**
     * Adds an observer that will receive the new value in the callback.
     * The observer is immediately notified of the current value when it's added.
     *
     * @param observer
     */
    public void addObserver( VoidFunction1<T> observer ) {
        newValueObservers.add( observer );
        observer.apply( get() );
    }

    public void removeObserver( VoidFunction1<T> observer ) {
        newValueObservers.remove( observer );
    }

    /*
     * Notify observers that receive the new and old values in the callback.
     */
    private void notifyNewAndOldValueObservers( T newValue, T oldValue ) {
        for ( ChangeObserver<T> observer : new ArrayList<ChangeObserver<T>>( newAndOldValueObservers ) ) {//Iterate on a copy of the observer list to avoid ConcurrentModificationException, see #2741
            observer.update( newValue, oldValue );
        }
    }

    /**
     * Adds an observer that will receive the new and old value in the callback.
     * This observer does NOT receive immediate notification when it's added,
     * because there is no notion of "new value" or "old value".
     *
     * @param observer
     */
    public void addObserver( ChangeObserver<T> observer ) {
        newAndOldValueObservers.add( observer );
    }

    public void removeObserver( ChangeObserver<T> observer ) {
        newAndOldValueObservers.remove( observer );
    }

    /**
     * Adds and immediately notifies a SimpleObserver.
     *
     * @param simpleObserver
     */
    public void addObserver( SimpleObserver simpleObserver ) {
        addObserver( simpleObserver, true );
    }

    public abstract T get();

    @Override
    public String toString() {
        return get().toString();
    }

    /**
     * Debugging function that prints out the new value when it changes, along with the specified text.
     *
     * @param text the text to print before printing the new value
     */
    public void trace( final String text ) {
        addObserver( new VoidFunction1<T>() {
            public void apply( T t ) {
                System.out.println( text + ": " + t );
            }
        } );
    }

    /**
     * Sample demonstration of ObservableProperty features.
     *
     * @param args system args
     */
    public static void main( String[] args ) {
        Property<String> p = new Property<String>( "hello" );
        p.trace( "text" );
        p.set( "world" );
    }

    /**
     * Check to see if the value is different than the the value during the last notification, then send out
     * notifications if the value has changed (and storing the new value for next time).
     */
    public void notifyIfChanged() {
        T newValue = get();
        //TODO: handle nulls in this equality test
        if ( !newValue.equals( oldValue ) ) {
            T saveOldValue = oldValue;
            oldValue = newValue;
            notifyObservers( newValue, saveOldValue );
        }
    }

    /**
     * Removes all observers (0-parameter, 1-parameter and 2-parameter) from this ObservableProperty.
     */
    public void removeAllObservers() {
        simpleObservers.clear();
        newValueObservers.clear();
        newAndOldValueObservers.clear();
    }
}