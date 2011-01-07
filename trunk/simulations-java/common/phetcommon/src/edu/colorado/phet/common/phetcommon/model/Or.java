// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model;

import java.util.List;

import edu.colorado.phet.common.phetcommon.util.Function2;

/**
 * Returns a boolean AND over Property arguments.  This provides read-only access;
 * calling setValue on this AndProperty doesn't propagate back to the original properties.
 *
 * @author Sam Reid
 */
public class Or extends BinaryBooleanProperty {
    public Or( Property<Boolean> a, Property<Boolean> b ) {
        super( a, b, new Function2<Boolean, Boolean, Boolean>() {
            public Boolean apply( Boolean x, Boolean y ) {
                return x || y;
            }
        } );
    }

    public static Boolean or( List<Property<Boolean>> p ) {
        for ( Property<Boolean> booleanProperty : p ) {
            if ( booleanProperty.getValue() ) { return true; }
        }
        return false;
    }
}
