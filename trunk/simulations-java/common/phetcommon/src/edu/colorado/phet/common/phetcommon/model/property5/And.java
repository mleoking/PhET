// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property5;

import edu.colorado.phet.common.phetcommon.util.function.Function2;

/**
 * Returns a boolean AND over Property arguments.  This provides read-only access;
 * calling setValue on this AndProperty doesn't propagate back to the original properties.
 *
 * @author Sam Reid
 */
public class And extends BinaryBooleanProperty {

    public And( final ObservableProperty<Boolean> a, final ObservableProperty<Boolean> b ) {
        super( a, b, new Function2<Boolean, Boolean, Boolean>() {
            public Boolean apply( Boolean x, Boolean y ) {
                return x && y;
            }
        } );
    }

    public static And and( Property<Boolean> a, final Property<Boolean> b ) {
        return new And( a, b );
    }
}
