// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property2;

/**
 * @author Sam Reid
 */
public abstract class Observer<T> {
    public abstract void update( UpdateEvent<T> event );

    void observe( Observable<T>... observables ) {
        for ( Observable<T> observable : observables ) {
            observable.addObserver( this );
        }
    }
}
