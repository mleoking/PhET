// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property3;

import edu.colorado.phet.common.phetcommon.model.property5.ChangeObserver;

/**
 * Observable class that uses ChangeEvents to signify both old and new values.
 * We considered making this class Observable<T> where T is the observer type, but the problem that you cannot have
 * class x implements Test<A>, Test<B> prevented it
 *
 * @author Sam Reid
 */
public interface Observable2<T> {
    void addObserver( ChangeObserver<T> observer );

    void removeObserver( ChangeObserver<T> observer );
}
