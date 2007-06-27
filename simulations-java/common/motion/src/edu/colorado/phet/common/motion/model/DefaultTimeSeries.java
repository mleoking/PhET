package edu.colorado.phet.common.motion.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Sam Reid
 * Jun 25, 2007, 11:31:28 PM
 */
public class DefaultTimeSeries implements ITimeSeries {
    private ArrayList data = new ArrayList();
    private ArrayList listeners = new ArrayList();

    public DefaultTimeSeries() {
    }

    public DefaultTimeSeries( double initValue, double initTime ) {
        addValue( initValue, initTime );
    }

    public TimeData getData() {
        return getRecentData( 0 );
    }

    public TimeData getData( int index ) {
        return (TimeData)data.get( index );
    }

    public TimeData getRecentData( int index ) {
//        System.out.println( "index="+index+", getSampleCount() = " + getSampleCount() );
        return getData( data.size() - 1 - index );
    }

    public int getSampleCount() {
        return data.size();
    }

    public void clear() {
        if( data.size() > 0 ) {
            data.clear();
            for( int i = 0; i < listeners.size(); i++ ) {
                ( (Listener)listeners.get( i ) ).dataCleared();
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
            Listener observableTimeSeriesListener = (Listener)listeners.get( i );
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

    public void addListener( Listener listener ) {
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
}
