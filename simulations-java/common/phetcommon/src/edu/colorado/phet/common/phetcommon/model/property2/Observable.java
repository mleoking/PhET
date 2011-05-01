// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property2;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.Some;

public class Observable<T> {
    private final ArrayList<Observer<T>> observers = new ArrayList<Observer<T>>();
    private final T initialValue;
    private T value;

    public Observable( T value ) {
        this.initialValue = value;
        this.value = value;
    }

    public T getInitialValue() {
        return initialValue;
    }

    public void reset() {
        setValue( initialValue );
    }

    public void addObserver( Observer<T> observer ) {
        observers.add( observer );
        observer.update( new UpdateEvent<T>( value, new Option.None<T>() ) );
    }

    public void removeObserver( Observer<T> observer ) {
        observers.remove( observer );
    }

    protected void notifyObservers( UpdateEvent<T> event ) {
        for ( Observer<T> observer : new ArrayList<Observer<T>>( observers ) ) {
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

    public static void main( String[] args ) {
        final Observable<Integer> age = new Observable<Integer>( 25 );
        final BooleanProperty b = new BooleanProperty( true );
        age.addObserver( new Observer<Integer>() {
            @Override public void update( UpdateEvent<Integer> integerUpdateEvent ) {
                System.out.println( b.getValue() );
            }
        } ); // 1st listener to fire
        ValueEquals<Integer> x = new ValueEquals<Integer>( age, 25 );
        age.setValue( 1 ); // will print true since b will not update before our first listener runs.
    }

    public static class Sum extends Observable<Integer> {
        public Sum( final Observable<Integer> a, final Observable<Integer> b ) {
            super( a.getValue() + b.getValue() );
            new Observer<Integer>() {
                @Override public void update( UpdateEvent<Integer> e ) {
                    setValue( a.getValue() + b.getValue() );
                }
            }.observe( a, b );
        }
    }

    public static class Twice extends Observable<Integer> {
        public Twice( final Observable<Integer> a ) {
            super( a.getValue() * 2 );
            a.addObserver( new Observer<Integer>() {
                @Override public void update( UpdateEvent<Integer> integerUpdateEvent ) {
                    setValue( a.getValue() * 2 );
                }
            } );
        }
    }

    public static class Test2 {
        public static void main( String[] args ) {
            final Property<Integer> yourWages = new Property<Integer>( 100 );
            final Observable<Integer> myWages = new Twice( yourWages );
            final Observable<Integer> totalWages = new Sum( myWages, yourWages );
            totalWages.addObserver( new Observer<Integer>() {
                @Override public void update( UpdateEvent<Integer> e ) {
                    System.out.println( "total wages = " + e.value );
                }
            } );
            yourWages.setValue( 200 );
        }
    }
}