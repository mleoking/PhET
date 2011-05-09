// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property3;

/**
 * Observable property that computes the quotient of its arguments.
 *
 * @author Sam Reid
 */
public class DivideDouble extends CompositeProperty<Double> {//No set defined on ValueEquals since undefined what to set the value to if "false"
    private final GettableObservable0<Double> numerator;
    private final GettableObservable0<Double> denominator;

    //Keep track of state and don't send out notifications unless the values changes
    private final Notifier<Double> notifier;

    public DivideDouble( GettableObservable0<Double> numerator, GettableObservable0<Double> denominator ) {
        this.numerator = numerator;
        this.denominator = denominator;
        notifier = new Notifier<Double>( get() );
        new RichListener() {
            public void apply() {
                if ( notifier.set( get() ) ) {
                    notifyListeners();
                }
            }
        }.observe( numerator, denominator );
    }

    //Returns the quotient (numerator / denominator)
    public Double get() {
        return numerator.get() / denominator.get();
    }
}