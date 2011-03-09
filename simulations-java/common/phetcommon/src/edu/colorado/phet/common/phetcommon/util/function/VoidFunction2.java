// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.util.function;

/**
 * A 'function' with 2 parameters which returns void.
 * This cannot be a function in the mathematical sense, just in the programming sense
 * (since it returns nothing and only causes side effects).
 * This class is required since Function2<T,T,Void> doesn't work properly in Java.
 *
 * @author Chris Malley
 */
public interface VoidFunction2<U, T> {
    void apply( U u, T t );

    /**
     * A VoidFunction2 which causes no side effects.
     *
     * @param <U,T>
     */
    public static class Null<U, T> implements VoidFunction2<U, T> {
        public void apply( U u, T t ) {
        }
    }
}
