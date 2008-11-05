/*  */
package edu.colorado.phet.theramp.timeseries;

/**
 * User: Sam Reid
 * Date: Apr 2, 2005
 * Time: 8:08:44 PM
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
