// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property3;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * We considered making this class Observable<T> where T is the observer type, but the problem that you cannot have
 * class x implements Test<A>, Test<B> prevented it
 *
 * @author Sam Reid
 */
public interface Observable1<T> {
    void addObserver( VoidFunction1<T> observer );

    void removeObserver( VoidFunction1<T> observer );
}
