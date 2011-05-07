// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.util;

/**
 * The option pattern is an alternative to using null return values.
 * See: http://www.codecommit.com/blog/scala/the-option-pattern
 * <p/>
 * Sample usage:
 * <pre>
 * {@code
 * setText(value.isNone() ? "?" : new DecimalFormat( "0.0" ).format(value.get()))
 * }
 * </pre>
 *
 * @author Sam Reid
 */
public abstract class Option<T> {
    public abstract T get();

    public abstract boolean isSome();

    public boolean isNone() {
        return !isSome();
    }

    //if Some, gets the option value, otherwise gets the elseValue
    public T getOrElse( T elseValue ) {
        return isSome() ? get() : elseValue;
    }

    public static class None<T> extends Option<T> {
        public T get() {
            throw new UnsupportedOperationException( "Cannot get value on none." );
        }

        public boolean isSome() {
            return false;
        }

        @Override public String toString() {
            return "None";
        }
    }

    public static class Some<T> extends Option<T> {
        private final T value;

        public Some( T value ) {
            this.value = value;
        }

        public T get() {
            return value;
        }

        public boolean isSome() {
            return true;
        }

        @Override public String toString() {
            return value.toString();
        }
    }
}
