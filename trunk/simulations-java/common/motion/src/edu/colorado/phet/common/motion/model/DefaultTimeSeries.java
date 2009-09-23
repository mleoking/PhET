package edu.colorado.phet.common.motion.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Sam Reid
 * Jun 25, 2007, 11:31:28 PM
 */
public class DefaultTimeSeries {
    private ArrayList<TimeData> data = new ArrayList<TimeData>();
    private ArrayList<ITemporalVariable.Listener> listeners = new ArrayList<ITemporalVariable.Listener>();

    public DefaultTimeSeries() {
    }

    public String toString() {
        return data.toString();
    }

    public TimeData getData() {
        return getRecentData( 0 );
    }

    public TimeData getData( int index ) {
        return data.get( index );
    }

    public TimeData getRecentData( int index ) {
        return getData( data.size() - 1 - index );
    }

    public int getSampleCount() {
        return data.size();
    }

    public void clear() {
        if ( data.size() > 0 ) {
            data.clear();
            for ( int i = 0; i < listeners.size(); i++ ) {
                listeners.get( i ).dataCleared();
            }
        }
    }

    public void addValue( double v, double time ) {
        TimeData o = new TimeData( v, time );
        data.add( o );
        notifyObservers( o );
    }

    private void notifyObservers( TimeData o ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            listeners.get( i ).dataAdded( o );
        }
    }

    public TimeData getMax() {
        TimeData max = new TimeData( Double.NEGATIVE_INFINITY, 0.0 );
        for ( int i = 0; i < getSampleCount(); i++ ) {
            if ( getData( i ).getValue() > max.getValue() ) {
                max = getData( i );
            }
        }
        return max;
    }

    public TimeData getMin() {
        TimeData min = new TimeData( Double.POSITIVE_INFINITY, 0.0 );
        for ( int i = 0; i < getSampleCount(); i++ ) {
            if ( getData( i ).getValue() < min.getValue() ) {
                min = getData( i );
            }
        }
        return min;
    }

    public void addListener( ITemporalVariable.Listener listener ) {
        listeners.add( listener );
    }

    public TimeData[] getRecentSeries( int numPts ) {
        List<TimeData> subList = data.subList( data.size() - numPts, data.size() );
        return subList.toArray( new TimeData[0] );
    }

    //todo: could interpolate
    public double getValueForTime( double time ) {
        return getDataForTime( time ).getValue();
    }

    private TimeData getDataForTime( double time ) {
        TimeData closest = getData();
        for ( int i = 0; i < getSampleCount(); i++ ) {
            if ( Math.abs( getData( i ).getTime() - time ) < Math.abs( closest.getTime() - time ) ) {
                closest = getData( i );
            }
        }
        return closest;
    }

    public static void verifySeriesCleared() {
//        for( int i = 0; i < instances.size(); i++ ) {
//            DefaultTimeSeries defaultTimeSeries = (DefaultTimeSeries)instances.get( i );
//            System.out.println( "defaultTimeSeries = " + defaultTimeSeries + ", dataSize=" + defaultTimeSeries.getSampleCount() );
//        }
    }

    public void removeListener( ITemporalVariable.Listener listener ) {
        listeners.remove( listener );
    }

    protected void removeValue( int index ) {
        data.remove( index );
    }

    public void insertValue( int index, double time, double value ) {
        data.add( index, new TimeData( value, time ) );
    }

    public TimeData[] getData( double startTime, double endTime ) {
        ArrayList<TimeData> inrange = new ArrayList<TimeData>();
        for ( int i = 0; i < data.size(); i++ ) {
            TimeData timeData = data.get( i );
            if ( timeData.getTime() >= startTime && timeData.getTime() <= endTime ) {
                inrange.add( timeData );
            }
        }
        return inrange.toArray( new TimeData[inrange.size()] );
    }
}
