// Copyright 2002-2011, University of Colorado

/*PhET, 2004.*/
package edu.colorado.phet.common.timeseries.model;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:38:31 AM
 */
public class TimeStateSeries {
    private ArrayList<TimeState> pts = new ArrayList<TimeState>();

    public void addPoint( Object value, double time ) {
        pts.add( new TimeState( value, time ));
    }

    public TimeState getLastPoint() {
        if ( pts.size() > 0 ) {
            return lastPointAt( 0 );
        }
        else {
            return null;
        }
    }

    public int size() {
        return pts.size();
    }

    public void clear() {
        this.pts = new ArrayList<TimeState>();
    }

    public TimeState lastPointAt( int i ) {
        return pointAt( pts.size() - 1 - i );
    }

    public TimeState pointAt( int i ) {
        return pts.get( i );
    }

    public int numPoints() {
        return pts.size();
    }

    public Object getTimeStateValue( double time ) {
        TimeState timeState = getTimeState( time );
        return timeState != null ? timeState.getValue() : null;
    }

    public TimeState getTimeState( double time ) {
        if ( numPoints() == 0 ) {
            return new TimeState( null, 0 );
        }
        TimeState[] timeStates = getNeighborsForTime( time, 0, numPoints() - 1 );
        TimeState lowerBound = timeStates[0];
        TimeState upperSample = timeStates[1];
        boolean valid = lowerBound.getTime() <= time && upperSample.getTime() >= time;
        if ( !valid ) {
            System.out.println( "requested time=" + time + ", lower bound=" + lowerBound.getTime() + ", upper bound=" + upperSample.getTime() + ", valid=" + valid );
        }

        double lowerDist = Math.abs( lowerBound.getTime() - time );
        double upperDist = Math.abs( upperSample.getTime() - time );
        if ( lowerDist <= upperDist ) {
            return new TimeState( lowerBound.getValue(), time );
        }
        else {
            return new TimeState( upperSample.getValue(), time );
        }
    }

    private TimeState[] getNeighborsForTime( double time, int minIndex, int maxIndex ) {
        return new TimeState[]{getLowerSample( time, minIndex, maxIndex, 0 ), getUpperSample( time, minIndex, maxIndex, 0 )};
    }

    private TimeState getLowerSample( double time, int min, int max, int depth ) {
        if ( depth > 1000 ) {
            new RuntimeException( "Lower Sample recursed 1000 times." ).printStackTrace();
            return new TimeState( null, 0 );
        }
        if ( min == max || min == max - 1 ) {
            return pointAt( min );
        }
        int midIndex = ( max + min ) / 2;
        TimeState mid = pointAt( midIndex );
        if ( mid.getTime() > time ) {
            return getLowerSample( time, min, midIndex, depth + 1 );
        }
        else {
            return getLowerSample( time, midIndex, max, depth + 1 );
        }
    }

    private TimeState getUpperSample( double time, int min, int max, int depth ) {
        if ( depth > 1000 ) {
            new RuntimeException( "Lower Sample recursed 1000 times." ).printStackTrace();
            return new TimeState( null, 0 );
        }
        if ( min == max || min == max - 1 ) {
            return pointAt( max );
        }
        int midIndex = ( max + min ) / 2;
        TimeState mid = pointAt( midIndex );
        if ( mid.getTime() > time ) {
            return getUpperSample( time, min, midIndex, depth + 1 );
        }
        else {
            return getUpperSample( time, midIndex, max, depth + 1 );
        }
    }

    public double getStartTime() {
        return pointAt( 0 ).getTime();
    }
}
