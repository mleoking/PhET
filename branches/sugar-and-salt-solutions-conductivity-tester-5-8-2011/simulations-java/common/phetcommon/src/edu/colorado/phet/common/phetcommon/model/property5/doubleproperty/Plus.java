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
    public Plus( final ObservableProperty<Double> a, final ObservableProperty<Double> b ) {
        super( new Function0<Double>() {
                   public Double apply() {
                       return a.getValue() + b.getValue();
                   }
               }, a, b );
    }
}
