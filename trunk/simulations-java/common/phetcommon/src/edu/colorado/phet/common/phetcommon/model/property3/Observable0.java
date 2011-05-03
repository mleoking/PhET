// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property3;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;

/**
 * Interface that signifies an instance can be observed for changes, callbacks provide no arguments.
 * I considered making this class Observable<T> where T is the observer type, but java's inability to support implements Obserable<A>, Observable<B> prevented it.
 *
 * @author Sam Reid
 */
public interface Observable0 {
    void addObserver( VoidFunction0 observer );

    void removeObserver( VoidFunction0 observer );
}
