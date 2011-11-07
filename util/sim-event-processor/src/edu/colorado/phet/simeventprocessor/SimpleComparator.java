// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor;

import fj.F;
import fj.F2;
import fj.Ord;
import fj.Ordering;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;

/**
 * @author Sam Reid
 */
class SimpleComparator<T> extends F2<T, T, Ordering> implements IProguardKeepClass {

    private final F<T, Comparable> f;

    SimpleComparator( F<T, Comparable> f ) {
        this.f = f;
    }

    @Override public Ordering f( T t, T t1 ) {
        return Ord.<Comparable>comparableOrd().compare( f.f( t ), f.f( t1 ) );
    }
}
