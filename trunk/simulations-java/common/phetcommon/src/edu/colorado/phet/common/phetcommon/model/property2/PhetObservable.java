// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property2;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.Some;

public class PhetObservable<T> {
    private final ArrayList<PhetObserver<T>> observers = new ArrayList<PhetObserver<T>>();
    private final T initialValue;
    private T value;

    protected PhetObservable( T value ) {
        this.initialValue = value;
        this.value = value;
    }

    public T getInitialValue() {
        return initialValue;
    }

    public void reset() {
        setValue( initialValue );
    }

    public void addObserver( PhetObserver<T> observer ) {
        observers.add( observer );
        observer.update( new UpdateEvent<T>( value, new Option.None<T>() ) );
    }

    public void removeObserver( PhetObserver<T> observer ) {
        observers.remove( observer );
    }

    protected void notifyObservers( UpdateEvent<T> event ) {
        for ( PhetObserver<T> observer : new ArrayList<PhetObserver<T>>( observers ) ) {
            observer.update( event );
        }
    }

    public T getValue() {
        return value;
    }

    @Override public String toString() {
        return getValue().toString();
    }

    protected void setValue( T newValue ) {
        if ( !this.value.equals( newValue ) ) {
            T oldValue = this.value;
            this.value = newValue;
            notifyObservers( new UpdateEvent<T>( newValue, new Some<T>( oldValue ) ) );
        }
    }
}