/*PhET, 2004.*/
package edu.colorado.phet.movingman.plots;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:38:31 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class TimeSeries {
    private ArrayList pts = new ArrayList();
    private ArrayList observers = new ArrayList();

    public TimeSeries() {
    }

    public void addPoint( double value, double time ) {
        TimePoint timePoint = new TimePoint( value, time );
        this.pts.add( timePoint );
        notifyAdded();
    }

    private void notifyAdded() {
        for( int i = 0; i < observers.size(); i++ ) {
            Observer observer = (Observer)observers.get( i );
            observer.dataAdded( this );
        }
    }

    public TimePoint getLastPoint() {
        return lastPointAt( 0 );
    }

    public int size() {
        return pts.size();
    }

    public void reset() {
        this.pts = new ArrayList();
        notifyCleared();
    }

    private void notifyCleared() {
        for( int i = 0; i < observers.size(); i++ ) {
            Observer observer = (Observer)observers.get( i );
            observer.cleared( this );
        }
    }

    public TimePoint lastPointAt( int i ) {
        return pointAt( pts.size() - 1 - i );
    }

    public TimePoint pointAt( int i ) {
        return ( (TimePoint)pts.get( i ) );
    }

    public boolean indexInBounds( int index ) {
        return index >= 0 && index < pts.size();
    }

    public void addObserver( Observer observer ) {
        observers.add( observer );
    }

    public int numPoints() {
        return pts.size();
    }

    public double getLastTime() {
        if( numPoints() == 0 ) {
            return 0;
        }
        else {
            return getLastPoint().getTime();
        }
    }

    public static interface Observer {
        void dataAdded( TimeSeries timeSeries );

        void cleared( TimeSeries timeSeries );
    }

}
