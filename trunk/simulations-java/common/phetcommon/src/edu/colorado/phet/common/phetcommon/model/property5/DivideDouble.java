// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property5;

import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * Observable property that computes the quotient of its arguments.
 *
 * @author Sam Reid
 */
public class DivideDouble extends CompositeProperty<Double> {
    public DivideDouble( final ObservableProperty<Double> numerator, final ObservableProperty<Double> denominator ) {
        super( new Function0<Double>() {
                   public Double apply() {
                       return numerator.getValue() / denominator.getValue();
                   }
               }, numerator, denominator );
    }
}