/*  */
package edu.colorado.phet.theramp.timeseries;

/**
 * User: Sam Reid
 * Date: Apr 2, 2005
 * Time: 8:08:44 PM
 */

public class TimePoint {
    private double value;
    private double time;

    public TimePoint( double value, double time ) {
        this.value = value;
        this.time = time;
    }

    public TimePoint( TimePoint timePoint ) {
        this( timePoint.value, timePoint.time );
    }

    public double getValue() {
        return value;
    }

    public double getTime() {
        return time;
    }

    public String toString() {
        return "time=" + time + ", value=" + value;
    }

    public static TimePoint average( TimePoint[] timePoints ) {
        double valSum = 0;
        double timeSum = 0;
        for( int i = 0; i < timePoints.length; i++ ) {
            TimePoint timePoint = timePoints[i];
            valSum += timePoint.value;
            timeSum += timePoint.time;
        }
        return new TimePoint( valSum / timePoints.length, timeSum / timePoints.length );
    }
}
