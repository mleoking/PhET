/*, 2003.*/
package edu.colorado.phet.greenhouse.common_greenhouse.model;

/**
 * I am tired of the Java API's lack of support for enum types.
 */
public class ThreadPriority {
    int value;
    public static final ThreadPriority MAX = new ThreadPriority(Thread.MAX_PRIORITY);
    public static final ThreadPriority MIN = new ThreadPriority(Thread.MIN_PRIORITY);
    public static final ThreadPriority NORMAL = new ThreadPriority(Thread.NORM_PRIORITY);

    private ThreadPriority(int value) {
        this.value = value;
    }

    public int intValue() {
        return value;
    }
}
