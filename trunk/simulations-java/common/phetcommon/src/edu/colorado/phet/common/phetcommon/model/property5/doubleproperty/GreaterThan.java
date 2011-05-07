// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property5.doubleproperty;

import edu.colorado.phet.common.phetcommon.model.property5.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property5.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property5.Or;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * ObservableProperty that is true if the specified ObservableProperty is greater than another specified value.
 *
 * @author Sam Reid
 */
public class GreaterThan extends CompositeProperty<Boolean> {
    public GreaterThan( final ObservableProperty<Double> a, final double b ) {
        super( new Function0<Boolean>() {
                   public Boolean apply() {
                       return a.getValue() > b;
                   }
               }, a );
    }

    public ObservableProperty<Boolean> or( ObservableProperty<Boolean> b ) {
        return new Or( this, b );
    }
}