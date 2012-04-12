// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.view;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;

/**
 * @author Sam Reid
 */
public abstract class RichVoidFunction0 implements VoidFunction0 {
    public VoidFunction0 andThen( final VoidFunction0 after ) {
        return new VoidFunction0() {
            public void apply() {
                RichVoidFunction0.this.apply();
                after.apply();
            }
        };
    }
}