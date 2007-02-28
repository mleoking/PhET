/* Copyright 2004, Sam Reid */
package edu.colorado.phet.timeseries;

/**
 * User: Sam Reid
 * Date: Apr 2, 2005
 * Time: 8:08:44 PM
 * Copyright (c) Apr 2, 2005 by Sam Reid
 */

public class ObjectTimePoint {
    private Object value;
    private double time;

    public ObjectTimePoint( Object value, double time ) {
        this.value = value;
        this.time = time;
    }

    public ObjectTimePoint( ObjectTimePoint timePoint ) {
        this( timePoint.value, timePoint.time );
    }

    public Object getValue() {
        return value;
    }

    public double getTime() {
        return time;
    }

    public String toString() {
        return "time=" + time + ", value=" + value;
    }

}
