/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.plots;

/**
 * User: Sam Reid
 * Date: Apr 2, 2005
 * Time: 8:08:44 PM
 * Copyright (c) Apr 2, 2005 by Sam Reid
 */

public class TimePoint {
    private double value;
    private double time;

    public TimePoint( double value, double time ) {
        this.value = value;
        this.time = time;
    }

    public double getValue() {
        return value;
    }

    public double getTime() {
        return time;
    }
}
