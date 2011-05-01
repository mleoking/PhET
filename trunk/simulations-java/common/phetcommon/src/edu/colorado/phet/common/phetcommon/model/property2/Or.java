// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property2;

import edu.colorado.phet.common.phetcommon.util.function.Function2;

/**
 * @author Sam Reid
 */
public class Or extends BinaryBooleanProperty {
    public Or( final Observable<Boolean> a, final Observable<Boolean> b ) {
        super( a, b, new Function2<Boolean, Boolean, Boolean>() {
            public Boolean apply( Boolean a, Boolean b ) {
                return a || b;
            }
        } );
    }
}
