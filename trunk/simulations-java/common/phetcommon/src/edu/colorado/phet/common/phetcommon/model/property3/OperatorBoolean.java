// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property3;

import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;

/**
 * Combines values from two binary parents using a combination operator such as AND or OR.
 *
 * @author Sam Reid
 */
public class OperatorBoolean
        <T extends Gettable<Boolean> & Observable0>//parents are gettable and observable but not settable
        implements Gettable<Boolean>, Observable0 {//Nor can this generally be set because the semantics of synchronizing with parents are undefined
    private Gettable<Boolean> a;
    private Gettable<Boolean> b;
    private final Function2<Boolean, Boolean, Boolean> operator;

    private Notifier<Boolean> notificationHelper;
    private ListenerList<VoidFunction0> listeners = new ListenerList<VoidFunction0>();

    public OperatorBoolean( T a, T b, Function2<Boolean, Boolean, Boolean> operator ) {
        this.a = a;
        this.b = b;
        this.operator = operator;
        notificationHelper = new Notifier<Boolean>( get() );
        new RichListener() {
            public void apply() {
                if ( notificationHelper.change( get() ) ) {
                    listeners.notifyListeners();
                }
            }
        }.observe( a, b );
    }

    public Boolean get() {
        return operator.apply( a.get(), b.get() );
    }

    public void addObserver( VoidFunction0 observer ) {
        listeners.add( observer );
    }

    public void removeObserver( VoidFunction0 observer ) {
        listeners.remove( observer );
    }

}