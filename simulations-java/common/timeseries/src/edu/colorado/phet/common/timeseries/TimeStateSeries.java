/*PhET, 2004.*/
package edu.colorado.phet.common.timeseries;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:38:31 AM
 */
public class TimeStateSeries {
    private ArrayList pts = new ArrayList();
    private ArrayList observers = new ArrayList();

    public void addPoint( Object value, double time ) {
        TimeState timePoint = new TimeState( value, time );
        this.pts.add( timePoint );
        notifyAdded();
    }

    private void notifyAdded() {
        for( int i = 0; i < observers.size(); i++ ) {
            Observer observer = (Observer)observers.get( i );
            observer.dataAdded( this );
        }
    }

    public TimeState getLastPoint() {
        if( pts.size() > 0 ) {
            return lastPointAt( 0 );
        }
        else {
            return null;
        }
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

    public TimeState lastPointAt( int i ) {
        return pointAt( pts.size() - 1 - i );
    }

    public TimeState pointAt( int i ) {
        return ( (TimeState)pts.get( i ) );
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

    public TimeState getValueForTime( double time ) {
        if( numPoints() == 0 ) {
            return new TimeState( null, 0 );
        }
        TimeState[] timeStates = getNeighborsForTime( time, 0, numPoints() - 1, 0 );
        TimeState lowerBound = timeStates[0];
        TimeState upperSample = timeStates[1];

        double lowerDist = Math.abs( lowerBound.getTime() - time );
        double upperDist = Math.abs( upperSample.getTime() - time );
        if( lowerDist <= upperDist ) {
            return new TimeState( lowerBound.getValue(), time );
        }
        else {
            return new TimeState( upperSample.getValue(), time );
        }
    }

    private TimeState[] getNeighborsForTime( double time, int minIndex, int maxIndex, int debugDepth ) {
        return new TimeState[]{getLowerSample( time, minIndex, maxIndex, 0 ), getUpperSample( time, minIndex, maxIndex, 0 )};
    }

    private TimeState getLowerSample( double time, int min, int max, int depth ) {
        if( depth > 1000 ) {
            new RuntimeException( "Lower Sample recursed 1000 times." ).printStackTrace();
            return new TimeState( null, 0 );
        }
        if( min == max || min == max - 1 ) {
            return pointAt( min );
        }
        int midIndex = ( max + min ) / 2;
        TimeState mid = pointAt( midIndex );
        if( mid.getTime() > time ) {
            return getLowerSample( time, min, midIndex, depth + 1 );
        }
        else {
            return getLowerSample( time, midIndex, max, depth + 1 );
        }
    }

    private TimeState getUpperSample( double time, int min, int max, int depth ) {
        if( depth > 1000 ) {
            new RuntimeException( "Lower Sample recursed 1000 times." ).printStackTrace();
            return new TimeState( null, 0 );
        }
        if( min == max || min == max - 1 ) {
            return pointAt( max );
        }
        int midIndex = ( max + min ) / 2;
        TimeState mid = pointAt( midIndex );
        if( mid.getTime() > time ) {
            return getUpperSample( time, min, midIndex, depth + 1 );
        }
        else {
            return getUpperSample( time, midIndex, max, depth + 1 );
        }
    }

    public void remove( int i ) {
        pts.remove( i );
    }

    public static interface Observer {
        void dataAdded( TimeStateSeries timeStateSeries );

        void cleared( TimeStateSeries timeStateSeries );

        void dataRemoved( TimeStateSeries timeStateSeries );
    }

}
