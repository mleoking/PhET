// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.util;

/**
 * @author Sam Reid
 */
public interface Option<T> {
    T get();

    public static class None<T> implements Option<T> {
        public T get() {
            throw new UnsupportedOperationException( "Cannot get value on none." );
        }
    }

    public static class Some<T> implements Option<T> {
        private final T value;

        public Some( T value ) {
            this.value = value;
        }

        public T get() {
            return value;
        }
    }
}
