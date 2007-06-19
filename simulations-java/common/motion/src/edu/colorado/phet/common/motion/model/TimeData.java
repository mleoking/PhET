package edu.colorado.phet.common.motion.model;

/**
 * User: Sam Reid
 * Date: Dec 30, 2006
 * Time: 12:04:53 AM
 */

public class TimeData {
    private double value;
    private double time;

    public TimeData( double value, double time ) {
        this.value = value;
        this.time = time;
    }

    public double getValue() {
        return value;
    }

    public double getTime() {
        return time;
    }

    public String toString() {
        return "value=" + value + ", time=" + time;
    }
}
