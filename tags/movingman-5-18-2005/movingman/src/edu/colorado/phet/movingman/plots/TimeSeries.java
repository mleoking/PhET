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
//    private TreeMap timeToTimePointMap=new TreeMap( );
    private ArrayList observers = new ArrayList();

    public TimeSeries() {
    }

    public void addPoint( double value, double time ) {
        TimePoint timePoint = new TimePoint( value, time );
        this.pts.add( timePoint );
//        this.timeToTimePointMap.put(new Double(time),timePoint );
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
//        timeToTimePointMap.clear();
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

    public TimePoint getValueForTime( double time ) {
//        timeToTimePointMap.
        if( numPoints() == 0 ) {
            return new TimePoint( 0, 0 );
        }
        TimePoint[] n = getNeighborsForTime( time, 0, numPoints() - 1, 0 );
        TimePoint lowerBound = n[0];
        TimePoint upperSample = n[1];

        boolean useAverage = false;
        if( useAverage ) {
            TimePoint average = TimePoint.average( new TimePoint[]{lowerBound, upperSample} );
            TimePoint fakeAverage = new TimePoint( average.getValue(), time );//fudge the time, pretend we were exact.
//        System.out.println( "Requested time: " + time + ", lower=" + lowerBound + ", upper=" + upperSample + ", actualAvg=" + average + ", fakeAverage=" + fakeAverage );
            System.out.println( "Requested time: " + time + ", lower=" + lowerBound.getTime() + ", upper=" + upperSample.getTime() + ", actualAvg=" + average.getTime() + ", fakeAverage=" + fakeAverage );

            return fakeAverage;
        }
        else {//use nearest neighbor.
            double lowerDist = Math.abs( lowerBound.getTime() - time );
            double upperDist = Math.abs( upperSample.getTime() - time );
            if( lowerDist <= upperDist ) {
                return new TimePoint( lowerBound.getValue(), time );
            }
            else {
                return new TimePoint( upperSample.getValue(), time );
            }
        }
    }

//    private TimePoint getLowerSample( double time ) {
//        return getLowerSample( time, 0, numPoints(), 0 );
//    }

    private TimePoint[] getNeighborsForTime( double time, int minIndex, int maxIndex, int debugDepth ) {
        return new TimePoint[]{getLowerSample( time, minIndex, maxIndex, 0 ), getUpperSample( time, minIndex, maxIndex, 0 )};
//        System.out.println( "LowerSample:recursive.debugDepth=" + debugDepth );
//        if( debugDepth > 1000 ) {
//            System.out.println( "debugDepth = " + debugDepth );
//        }
//        if( minIndex == maxIndex || minIndex == maxIndex - 1 ) {
//            return new TimePoint[]{pointAt( minIndex ), pointAt( maxIndex )};
////            return pointAt( minIndex );
//        }
////        else if( minIndex == maxIndex - 1 ) {
////            return pointAt( minIndex );
////        }
//        int centerIndex = ( maxIndex + minIndex ) / 2;
//        TimePoint mid = pointAt( centerIndex );
//        if( mid.getTime() > time ) {
//            return getNeighborsForTime( time, minIndex, centerIndex, debugDepth + 1 );
//        }
//        else {
//            return getNeighborsForTime( time, centerIndex, maxIndex, debugDepth + 1 );
//        }
    }

    private TimePoint getLowerSample( double time, int min, int max, int depth ) {
//        System.out.println( "LowerSample:recursive.depth=" + depth );
        if( depth > 1000 ) {
            new RuntimeException( "Lower Sample recursed 1000 times." ).printStackTrace();
            return new TimePoint( 0, 0 );
        }
        if( min == max || min == max - 1 ) {
            return pointAt( min );
        }
        int midIndex = ( max + min ) / 2;
        TimePoint mid = pointAt( midIndex );
        if( mid.getTime() > time ) {
            return getLowerSample( time, min, midIndex, depth + 1 );
        }
        else {
            return getLowerSample( time, midIndex, max, depth + 1 );
        }
    }
//
    private TimePoint getUpperSample( double time, int min, int max, int depth ) {
//        System.out.println( "LowerSample:recursive.depth=" + depth );
        if( depth > 1000 ) {
            new RuntimeException( "Lower Sample recursed 1000 times." ).printStackTrace();
            return new TimePoint( 0, 0 );
        }
        if( min == max || min == max - 1 ) {
            return pointAt( max );
        }
        int midIndex = ( max + min ) / 2;
        TimePoint mid = pointAt( midIndex );
        if( mid.getTime() > time ) {
            return getUpperSample( time, min, midIndex, depth + 1 );
        }
        else {
            return getUpperSample( time, midIndex, max, depth + 1 );
        }
    }

    public TimePoint getLatestDerivative( double dummy ) {
        if( size() > 2 ) {
            double x1 = lastPointAt( 0 ).getValue();
            double x0 = lastPointAt( 2 ).getValue();
            double dx = x1 - x0;
            double dt = lastPointAt( 0 ).getTime() - lastPointAt( 2 ).getTime();
//            double diffValue = dx / dt / 2; //centered differentiation
            double diffValue = dx / dt; //centered differentiation
//            System.out.println( "dx = " + dx + ", dt=" + dt );

            double middleTime = ( lastPointAt( 0 ).getTime() + lastPointAt( 2 ).getTime() ) / 2.0;
            return new TimePoint( diffValue, middleTime );
        }
        else {
            return null;
        }
    }

//    private TimePoint getLowerSample( double time ) {
//        int testCount = 0;
//        for( int i = pts.size() - 1; i >= 0; i-- ) {//assume points are time-ordered.  This could be asserted in the add point function.
//            if( pointAt( i ).getTime() <= time ) {
//                System.out.println( "Lower.testCount = " + testCount );
//                return pointAt( i );
//            }
//            testCount++;
//        }
//        return new TimePoint( 0, 0 );
//    }
//
//    private TimePoint getUpperSample( double time ) {
//        int upperTestCount=0;
//        for( int i = 0; i < pts.size(); i++ ) {//assume points are time-ordered.  This could be asserted in the add point function.
//            if( pointAt( i ).getTime() >= time ) {
//                System.out.println( "upperTestCount = " + upperTestCount );
//                return pointAt( i );
//            }
//            upperTestCount++;
//        }
//        return new TimePoint( 0, 0 );
//    }

    public static interface Observer {
        void dataAdded( TimeSeries timeSeries );

        void cleared( TimeSeries timeSeries );
    }

}
