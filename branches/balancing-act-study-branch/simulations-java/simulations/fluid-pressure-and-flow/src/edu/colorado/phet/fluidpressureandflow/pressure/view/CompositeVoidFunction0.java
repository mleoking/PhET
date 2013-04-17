// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.view;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;

/**
 * Provides function composition (one function happening after another).
 *
 * @author Sam Reid
 */
public abstract class CompositeVoidFunction0 implements VoidFunction0 {
    public VoidFunction0 andThen( final VoidFunction0 after ) {
        return new VoidFunction0() {
            public void apply() {
                CompositeVoidFunction0.this.apply();
                after.apply();
            }
        };
    }
}