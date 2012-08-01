// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.common.util;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * VoidFunction1 implementation that support chaining with the method "andThen".
 *
 * @author Sam Reid
 */
public abstract class RichVoidFunction1<T> implements VoidFunction1<T> {
    public VoidFunction1<T> andThen( final VoidFunction0 after ) {
        return new VoidFunction1<T>() {
            public void apply( final T t ) {
                RichVoidFunction1.this.apply( t );
                after.apply();
            }
        };
    }
}