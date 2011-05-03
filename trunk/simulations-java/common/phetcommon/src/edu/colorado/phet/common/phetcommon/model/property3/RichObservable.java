// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property3;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Adds convenience methods, such as methods that expose a different listener interface or create new observable properties.
 * <p/>
 * //TODO: consider creating a lazy pattern implementation
 *
 * @author Sam Reid
 */
public abstract class RichObservable<T> implements GettableObservable0<T> {
    //Store the value of the associated NewProperty lazily so that it is only created if necessary
    private NewProperty<T> newProperty;

    //Returns a NewProperty which can be used with a callback value interface.  Value is stored so that listeners can be removed
    private NewProperty<T> toNewProperty() {
        if ( newProperty == null ) {
            newProperty = new NewProperty<T>( this );
        }
        return newProperty;
    }

    //Adds a listener that will receive the value during a callback
    public void addObserver( VoidFunction1<T> listener ) {
        toNewProperty().addObserver( listener );
    }

    //Removes a listener that will receive the value during a callback
    public void removeObserver( VoidFunction1<T> listener ) {
        toNewProperty().removeObserver( listener );
    }

    public ValueEquals<T> valueEquals( T value ) {
        return new ValueEquals<T>( this, value );
    }
}
