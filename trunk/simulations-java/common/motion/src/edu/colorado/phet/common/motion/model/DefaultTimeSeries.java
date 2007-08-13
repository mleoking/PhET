package edu.colorado.phet.common.motion.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Sam Reid
 * Jun 25, 2007, 11:31:28 PM
 */
public class DefaultTimeSeries implements ITemporalVariable {
    private ArrayList data = new ArrayList();
    private ArrayList listeners = new ArrayList();

    //todo: debugging only; should be removed, possible GC prevention 
//    public static final ArrayList instances = new ArrayList();

    public DefaultTimeSeries() {
//        instances.add( this );
    }

    public DefaultTimeSeries( double initValue, double initTime ) {
        this();
        addValue( initValue, initTime );
    }

    public TimeData getData() {
        return getRecentData( 0 );
    }

    public TimeData getData( int index ) {
        return (TimeData)data.get( index );
    }

    public TimeData getRecentData( int index ) {
        return getData( data.size() - 1 - index );
    }

    public int getSampleCount() {
        return data.size();
    }

    public void clear() {
        if( data.size() > 0 ) {
            data.clear();
            for( int i = 0; i < listeners.size(); i++ ) {
                ( (ITemporalVariable.Listener)listeners.get( i ) ).dataCleared();
            }
        }
    }

    public double getValue() {
        return getRecentData( 0 ).getValue();
    }

    public void addValue( double v, double time ) {
        TimeData o = new TimeData( v, time );
        data.add( o );
        notifyObservers( o );
    }

    private void notifyObservers( TimeData o ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            ITemporalVariable.Listener observableTimeSeriesListener = (ITemporalVariable.Listener)listeners.get( i );
            observableTimeSeriesListener.dataAdded( o );
        }
    }

    public double getTime() {
        return getRecentData( 0 ).getTime();
    }

    public TimeData getMax() {
        TimeData max = new TimeData( Double.NEGATIVE_INFINITY, 0.0 );
        for( int i = 0; i < getSampleCount(); i++ ) {
            if( getData( i ).getValue() > max.getValue() ) {
                max = getData( i );
            }
        }
        return max;
    }

    public TimeData getMin() {
        TimeData min = new TimeData( Double.POSITIVE_INFINITY, 0.0 );
        for( int i = 0; i < getSampleCount(); i++ ) {
            if( getData( i ).getValue() < min.getValue() ) {
                min = getData( i );
            }
        }
        return min;
    }

    public void addListener( ITemporalVariable.Listener listener ) {
        listeners.add( listener );
    }

    public TimeData[] getRecentSeries( int numPts ) {
        List subList = data.subList( data.size() - numPts, data.size() );
        return (TimeData[])subList.toArray( new TimeData[0] );
    }

    //todo: could interpolate
    public double getValueForTime( double time ) {
        return getDataForTime( time ).getValue();
    }

    private TimeData getDataForTime( double time ) {
        TimeData closest = getData();
        for( int i = 0; i < getSampleCount(); i++ ) {
            if( Math.abs( getData( i ).getTime() - time ) < Math.abs( closest.getTime() - time ) ) {
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
}
