// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property3;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Factors out duplicated listener code in composite properties such as ValueEquals and OperatorBoolean
 *
 * @author Sam Reid
 */
public abstract class CompositeProperty<T> extends RichObservable<T> {
    private ListenerList<VoidFunction0> listeners = new ListenerList<VoidFunction0>( new VoidFunction1<VoidFunction0>() {
        public void apply( VoidFunction0 listener ) {
            listener.apply();
        }
    } );

    protected void notifyListeners() {
        listeners.notifyListeners();
    }

    public void addObserver( VoidFunction0 observer ) {
        listeners.add( observer );
    }

    public void removeObserver( VoidFunction0 observer ) {
        listeners.remove( observer );
    }
}