// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.util;

/**
 * A 'function' with no arguments which returns void.
 * This cannot be a function in the mathematical sense, just in the programming sense
 * (since it returns nothing and only causes side effects).
 * This class is required since Function0<Void> doesn't work properly in Java.
 *
 * @author Sam Reid
 */
public interface VoidFunction0 {
    void apply();

    /**
     * A VoidFunction which causes no side effects.
     */
    public static class Null implements VoidFunction0 {
        public void apply() {
        }
    }
}
