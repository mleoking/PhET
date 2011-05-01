// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property2;

import edu.colorado.phet.common.phetcommon.util.function.Function2;

/**
 * @author Sam Reid
 */
public class BinaryBooleanProperty extends Observable<Boolean> {
    public BinaryBooleanProperty( final Observable<Boolean> a, final Observable<Boolean> b, final Function2<Boolean, Boolean, Boolean> predicate ) {
        super( predicate.apply( a.getValue(), b.getValue() ) );
        new Observer<Boolean>() {
            public void update( UpdateEvent<Boolean> event ) {
                setValue( predicate.apply( a.getValue(), b.getValue() ) );
            }
        }.observe( a, b );
    }
}