package edu.colorado.phet.common.phetcommon.util;

/**
 * A 'function' with no arguments which returns void.
 * This cannot be a function in the mathematical sense, just in the programming sense
 * (since it returns nothing and only causes side effects).
 * This class is required since Function0<Void> doesn't work properly in Java.
 *
 * @author Sam Reid
 */
public interface VoidFunction0<T> {
    void apply();
}
