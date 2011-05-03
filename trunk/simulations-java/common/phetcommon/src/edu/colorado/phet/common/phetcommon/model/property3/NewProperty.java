// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property3;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * This Property implementation signifies the new value on callbacks by wrapping an Observable0 property.  It is implemented by composition of a wrapped observable.
 *
 * @author Sam Reid
 */
public class NewProperty<T> implements Observable1<T> {
    //Parent value which will be observed for change events
    private final GettableObservable0<T> parent;

    //List of listeners that will receive the value in callbacks
    private final ListenerList<VoidFunction1<T>> listenerList = new ListenerList<VoidFunction1<T>>( new VoidFunction1<VoidFunction1<T>>() {
        public void apply( VoidFunction1<T> listener ) {
            listener.apply( parent.get() );
        }
    } );

    //Create a NewProperty wrapped around the specified parent
    public NewProperty( GettableObservable0<T> parent ) {
        this.parent = parent;
        parent.addObserver( new VoidFunction0() {
            public void apply() {
                listenerList.notifyListeners();
            }
        } );
    }

    //adds a listener that will receive the new value in its callback
    public void addObserver( VoidFunction1<T> observer ) {
        listenerList.add( observer );
    }

    //removes a listener that will receive the new value in its callback
    public void removeObserver( VoidFunction1<T> observer ) {
        listenerList.remove( observer );
    }

    //Test that demonstrates usage of NewProperty
    public static void main( String[] args ) {
        Property<Boolean> visible = new Property<Boolean>( true );
        Property<Boolean> selected = new Property<Boolean>( false );
        final And and = new And( visible, selected );
        selected.set( true );

        and.toNewProperty().addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean o ) {
                System.out.println( "o = " + o );
            }
        } );
        visible.set( true );
        selected.set( true );
    }
}