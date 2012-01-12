// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.timeseries.model;

/**
 * User: Sam Reid
 * Date: Apr 2, 2005
 * Time: 8:08:44 PM
 */

public class TimeState {
    private Object value;
    private double time;

    public TimeState( Object value, double time ) {
        this.value = value;
        this.time = time;
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
