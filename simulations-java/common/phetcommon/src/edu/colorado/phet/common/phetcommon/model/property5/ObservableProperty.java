// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.model.property5;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction2;
import edu.colorado.phet.common.phetcommon.util.logging.LoggingUtils;

//REVIEW super.removeAllObservers doesn't do what you think it does, override?
//REVIEW why 3 totally different variants of addObserver? (SimpleObserver, VoidFunction2, ChangeObserver)
//REVIEW why addObserver(ChangeObserver) and removeObserver(VoidFunction2)?
//REVIEW if we're going to have separate listener lists, then absorb functionality of SimpleObservable?

/**
 * This can be used to represent an observable model value in a MVC style pattern. Notifications are sent to observers when they
 * register with addObserver, and also when the value changes (it is up to subclasses to guarantee that notifications are sent when necessary, and not duplicated).
 * <p/>
 * ObservableProperty should be used instead of SettableProperty whenever possible, i.e. when the value needs to be observed but not set.
 *
 * @author Sam Reid
 * @author Chris Malley
 */
public abstract class ObservableProperty<T> extends SimpleObservable {
    private static final Logger LOGGER = LoggingUtils.getLogger( ObservableProperty.class.getCanonicalName() );

    static {
        // get rid of this to log all of the resource messages
        LOGGER.setLevel( Level.INFO );
    }

    private final ArrayList<VoidFunction1<T>> newValueObservers = new ArrayList<VoidFunction1<T>>();//Listeners that receive the new value in the callback
    private final ArrayList<ChangeObserver<T>> newAndOldValueObservers = new ArrayList<ChangeObserver<T>>();//Listeners that receive the new and old values in the callback

    //REVIEW I don't see this value being used for the purpose described here, and there's no interface to set/get.
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
        super.addObserver( simpleObserver );
        if ( notifyOnAdd ) {
            simpleObserver.update();
        }
    }

    protected void notifyObservers( T value, T oldValue ) {
        super.notifyObservers();//Notify SimpleObservers
        notifyNewValueObservers( value );//Notify listeners with new value
        notifyNewAndOldValueObservers( value, oldValue );//Notify listeners with both new and old values
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
     * The observer is immediately notified of the current value when it's added.
     *
     * @param observer
     */
    public void addObserver( VoidFunction1<T> observer ) {
        newValueObservers.add( observer );
        observer.apply( getValue() );
    }

    public void removeObserver( VoidFunction1<T> observer ) {
        newValueObservers.remove( observer );
    }

    /*
     * Notify observers that receive the new and old values in the callback.
     */
    private void notifyNewAndOldValueObservers( T newValue, T oldValue ) {
        for ( ChangeObserver<T> observer : new ArrayList<ChangeObserver<T>>( newAndOldValueObservers ) ) {//Iterate on a copy of the observer list to avoid ConcurrentModificationException, see #2741
            observer.update( new ChangeEvent<T>( newValue, oldValue ) ); //REVIEW change order of args
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

    public void removeObserver( VoidFunction2<T, T> observer ) {
        newAndOldValueObservers.remove( observer );
    }

    /**
     * Adds and immediately notifies a SimpleObserver.
     *
     * @param simpleObserver
     */
    @Override
    public void addObserver( SimpleObserver simpleObserver ) {
        addObserver( simpleObserver, true /* notifyOnAdd */ );
    }

    public abstract T getValue();

    @Override
    public String toString() {
        return getValue().toString();
    }

    /**
     * Debugging function that prints out the new value when it changes, along with the specified text.
     *
     * @param text the text to print before printing the new value
     */
    public void trace( final String text ) {
        addObserver( new VoidFunction1<T>() {
            public void apply( T t ) {
                LOGGER.info( text + ": " + t );
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
        p.setValue( "world" );
    }

    /**
     * Check to see if the value is different than the the value during the last notification, then send out
     * notifications if the value has changed (and storing the new value for next time).
     */
    public void notifyIfChanged() {
        T newValue = getValue();
        //TODO: handle nulls in this equality test
        if ( !newValue.equals( oldValue ) ) {
            notifyObservers( newValue, oldValue );
            oldValue = newValue;
        }
    }
}