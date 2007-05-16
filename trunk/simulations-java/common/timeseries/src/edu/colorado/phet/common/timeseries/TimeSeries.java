package edu.colorado.phet.common.timeseries;

/**
 * Author: Sam Reid
 * May 15, 2007, 7:39:55 PM
 */
public interface TimeSeries {
    void stepInTime( double simulationTimeChange );

    Object getState();

    void setState( Object o );

    void resetTime();
}
