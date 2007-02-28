package edu.colorado.phet.rotation.model;

/**
 * User: Sam Reid
 * Date: Dec 30, 2006
 * Time: 12:04:53 AM
 * Copyright (c) Dec 30, 2006 by Sam Reid
 */

public class TimeData {
    double value;
    double time;

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
