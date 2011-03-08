// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.util.function;

/**
 * A 'function' with 1 parameter which returns void.
 * This cannot be a function in the mathematical sense, just in the programming sense
 * (since it returns nothing and only causes side effects).
 * This class is required since Function1<T,Void> doesn't work properly in Java.
 *
 * @author Sam Reid
 */
public interface VoidFunction1<T> {
    void apply( T t );

    /**
     * A VoidFunction which causes no side effects.
     *
     * @param <T>
     */
    public static class Null<T> implements VoidFunction1<T> {
        public void apply( T o ) {
        }
    }
}
