// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property3;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;

/**
 * TODO: Consider making this class Observable<T> where T is the observer type
 *
 * @author Sam Reid
 */
public interface Observable0 {
    void addObserver( VoidFunction0 observer );

    void removeObserver( VoidFunction0 observer );
}
