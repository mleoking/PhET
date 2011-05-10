// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property5.doubleproperty;

import edu.colorado.phet.common.phetcommon.model.property5.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property5.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * This ObservableProperty computes the sum of its arguments
 *
 * @author Sam Reid
 */
public class Plus extends CompositeProperty<Double> {
    public Plus( final ObservableProperty<Double>... terms ) {
        super( new Function0<Double>() {
                   public Double apply() {
                       double sum = 0.0;
                       for ( ObservableProperty<Double> term : terms ) {
                           sum = sum + term.getValue();
                       }
                       return sum;
                   }
               }, terms );
    }
}
