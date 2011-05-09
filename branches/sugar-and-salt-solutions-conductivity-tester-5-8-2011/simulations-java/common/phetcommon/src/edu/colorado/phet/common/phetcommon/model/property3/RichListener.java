// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property3;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;

/**
 * Follows the VoidFunction0 interface for listener, but provides an observe method for convenience to add itself as an observer to many observables.
 *
 * @author Sam Reid
 */
public class RichListener implements VoidFunction0 {
    public void apply() {
    }

    public void observe( Observable0... observables ) {
        for ( Observable0 observable : observables ) {
            observable.addObserver( this );
        }
    }
}
