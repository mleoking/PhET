// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property3;

import edu.colorado.phet.common.phetcommon.util.function.Function2;

/**
 * Combines values from two binary parents using a combination operator such as AND or OR.
 *
 * @author Sam Reid
 */
public abstract class OperatorBoolean extends CompositeProperty<Boolean> {//Nor can this generally be set because the semantics of synchronizing with parents are undefined
    private Gettable<Boolean> a;
    private Gettable<Boolean> b;
    private final Function2<Boolean, Boolean, Boolean> operator;

    private Notifier<Boolean> notifier;

    public OperatorBoolean( GettableObservable0<Boolean> a, GettableObservable0<Boolean> b, Function2<Boolean, Boolean, Boolean> operator ) {
        this.a = a;
        this.b = b;
        this.operator = operator;
        notifier = new Notifier<Boolean>( get() );
        new RichListener() {
            public void apply() {
                if ( notifier.set( get() ) ) {
                    notifyListeners();
                }
            }
        }.observe( a, b );
    }

    public Boolean get() {
        return operator.apply( a.get(), b.get() );
    }
}