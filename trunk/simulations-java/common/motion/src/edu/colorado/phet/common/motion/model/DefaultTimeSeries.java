package edu.colorado.phet.common.motion.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author: Sam Reid
 * Jun 25, 2007, 11:31:28 PM
 */
public class DefaultTimeSeries implements ITimeSeries {
    private ArrayList data = new ArrayList();

    public DefaultTimeSeries() {
    }

    public DefaultTimeSeries( double initValue, double initTime ) {
        addValue( initValue, initTime );
    }

    public void setValue( double value ) {
        getRecentData( 0).setValue(value);
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
        data.clear();
    }

    public double getValue() {
        return getRecentData( 0).getValue();
    }

    public void addValue( double v, double time ) {
        data.add( new TimeData( v, time ) );
    }

    public double getTime() {
        return getRecentData( 0).getTime();
    }

    public TimeData[] getRecentSeries( int numPts ) {
//        System.out.println( "DefaultTimeSeries.getRecentSeries: numPts="+numPts+", sampleCount="+getSampleCount() );
        List subList = data.subList( data.size() -numPts, data.size() );
        return (TimeData[])subList.toArray( new TimeData[0] );
    }
}
