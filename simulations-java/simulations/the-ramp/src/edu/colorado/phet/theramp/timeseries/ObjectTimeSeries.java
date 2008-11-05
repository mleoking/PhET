/*PhET, 2004.*/
package edu.colorado.phet.theramp.timeseries;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:38:31 AM
 */
public class ObjectTimeSeries {
    private ArrayList pts = new ArrayList();
    private ArrayList observers = new ArrayList();

    public void addPoint( Object value, double time ) {
        ObjectTimePoint timePoint = new ObjectTimePoint( value, time );
        this.pts.add( timePoint );
        notifyAdded();
    }

    private void notifyAdded() {
        for( int i = 0; i < observers.size(); i++ ) {
            Observer observer = (Observer)observers.get( i );
            observer.dataAdded( this );
        }
    }

    public ObjectTimePoint getLastPoint() {
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

    public ObjectTimePoint lastPointAt( int i ) {
        return pointAt( pts.size() - 1 - i );
    }

    public ObjectTimePoint pointAt( int i ) {
        return ( (ObjectTimePoint)pts.get( i ) );
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

    public ObjectTimePoint getValueForTime( double time ) {
        if( numPoints() == 0 ) {
            return new ObjectTimePoint( null, 0 );
        }
        ObjectTimePoint[] n = getNeighborsForTime( time, 0, numPoints() - 1, 0 );
        ObjectTimePoint lowerBound = n[0];
        ObjectTimePoint upperSample = n[1];

        double lowerDist = Math.abs( lowerBound.getTime() - time );
        double upperDist = Math.abs( upperSample.getTime() - time );
        if( lowerDist <= upperDist ) {
            return new ObjectTimePoint( lowerBound.getValue(), time );
        }
        else {
            return new ObjectTimePoint( upperSample.getValue(), time );
        }
    }

    private ObjectTimePoint[] getNeighborsForTime( double time, int minIndex, int maxIndex, int debugDepth ) {
        return new ObjectTimePoint[]{getLowerSample( time, minIndex, maxIndex, 0 ), getUpperSample( time, minIndex, maxIndex, 0 )};
    }

    private ObjectTimePoint getLowerSample( double time, int min, int max, int depth ) {
        if( depth > 1000 ) {
            new RuntimeException( "Lower Sample recursed 1000 times." ).printStackTrace();
            return new ObjectTimePoint( null, 0 );
        }
        if( min == max || min == max - 1 ) {
            return pointAt( min );
        }
        int midIndex = ( max + min ) / 2;
        ObjectTimePoint mid = pointAt( midIndex );
        if( mid.getTime() > time ) {
            return getLowerSample( time, min, midIndex, depth + 1 );
        }
        else {
            return getLowerSample( time, midIndex, max, depth + 1 );
        }
    }

    private ObjectTimePoint getUpperSample( double time, int min, int max, int depth ) {
        if( depth > 1000 ) {
            new RuntimeException( "Lower Sample recursed 1000 times." ).printStackTrace();
            return new ObjectTimePoint( null, 0 );
        }
        if( min == max || min == max - 1 ) {
            return pointAt( max );
        }
        int midIndex = ( max + min ) / 2;
        ObjectTimePoint mid = pointAt( midIndex );
        if( mid.getTime() > time ) {
            return getUpperSample( time, min, midIndex, depth + 1 );
        }
        else {
            return getUpperSample( time, midIndex, max, depth + 1 );
        }
    }

    public static interface Observer {
        void dataAdded( ObjectTimeSeries timeSeries );

        void cleared( ObjectTimeSeries timeSeries );
    }

}
