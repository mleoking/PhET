// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.util;

/**
 * A three-parameter function that takes argument of types W, V and U, and returns a value of type T.
 *
 * @author Sam Reid
 */
public interface Function3<W, V, U, T> {
    T apply( W w, V v, U u );

    public static class Constant<T, T1, T2, T3> implements Function3<T, T1, T2, T3> {

        private T3 value;

        public Constant( T3 value ) {
            this.value = value;
        }

        public T3 apply( T t, T1 t1, T2 t2 ) {
            return value;
        }
    }
}