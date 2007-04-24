/*  */
package edu.colorado.phet.common_sound.util;

/**
 * Utility class for timing activity:
 * <p/>
 * Sample usage:
 * <p/>
 * QuickTimer paintTime=new QuickTimer();
 * paintComponent(g2d);
 * System.out.println("paintTime="+paintTime);
 */

public class QuickTimer {
    private long startTime;

    public QuickTimer() {
        this.startTime = System.currentTimeMillis();
    }

    public long getTime() {
        long now = System.currentTimeMillis();
        long dt = now - startTime;
        return dt;
    }

    public String toString() {
        return String.valueOf( getTime() );
    }
}
