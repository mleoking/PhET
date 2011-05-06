// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property3;

import edu.colorado.phet.common.phetcommon.util.function.Function2;

/**
 * Returns a boolean OR over its arguments, and signifying changes when its value changes.  This provides read-only access to the computed value.
 *
 * @author Sam Reid
 */
//TODO not general, limited to 2 booleans
public class Or extends OperatorBoolean {
    public Or( GettableObservable0<Boolean> a, GettableObservable0<Boolean> b ) {
        super( a, b, new Function2<Boolean, Boolean, Boolean>() {
            public Boolean apply( Boolean a, Boolean b ) {
                return a || b;
            }
        } );
    }
}