package edu.colorado.phet.common.motion.model;

import java.util.ArrayList;

import edu.colorado.phet.common.motion.MotionMath;
import edu.colorado.phet.common.phetcommon.math.Function;

/**
 * Author: Sam Reid
 * Jul 26, 2007, 8:04:10 PM
 */
public class DefaultTemporalVariable implements ITemporalVariable {
    private DefaultVariable variable;
    private DefaultTimeSeries series;

    public DefaultTemporalVariable() {
        this( 0 );
    }

    public DefaultTemporalVariable( double value ) {
        this( new DefaultVariable( value ), new DefaultTimeSeries() );
    }

    public DefaultTemporalVariable( DefaultVariable variable, DefaultTimeSeries series ) {
        this.variable = variable;
        this.series = series;
    }

    public DefaultTemporalVariable( TimeSeriesFactory timeSeriesFactory ) {
        this( 0, timeSeriesFactory );
    }

    public DefaultTemporalVariable( double value, TimeSeriesFactory timeSeriesFactory ) {
        this( new DefaultVariable( value ), timeSeriesFactory.createTimeSeries() );
    }

    public String toString() {
        return "value=" + variable + ", series=" + series;
    }

    public void addValue( double value, double time ) {
        variable.setValue( value );
        series.addValue( value, time );
    }

    public void addValue( TimeData timeData ) {
        addValue( timeData.getValue(), timeData.getTime() );
    }

    public void setValue( double value ) {
        variable.setValue( value );
    }

    public void setPlaybackTime( double time ) {
        setValue( series.getValueForTime( time ) );
    }

    public double getValue() {
        return variable.getValue();
    }

    public void addListener( IVariable.Listener listener ) {
        variable.addListener( listener );
    }

    public void removeListener( IVariable.Listener listener ) {
        variable.removeListener( listener );
    }

    public TimeData[] getRecentSeries( int numPts ) {
        return series.getRecentSeries( numPts );
    }

    public TimeData getData( int index ) {
        return series.getData( index );
    }

    public TimeData[] getData( int index, int requestedPoints ) {
        ArrayList t = new ArrayList();
        for ( int i = index - requestedPoints / 2; t.size() <= requestedPoints && i < index + requestedPoints / 2 + 1; i++ ) {
            if ( i >= 0 && i < getSampleCount() ) {
                t.add( getData( i ) );
            }
        }
        return (TimeData[]) t.toArray( new TimeData[0] );
    }

    public TimeData getRecentData( int index ) {
        return series.getRecentData( index );
    }

    public int getSampleCount() {
        return series.getSampleCount();
    }

    public void clear() {
        series.clear();
    }

    public TimeData getMax() {
        return series.getMax();
    }

    public TimeData getMin() {
        return series.getMin();
    }

    public void addListener( ITemporalVariable.Listener listener ) {
        series.addListener( listener );
        variable.addListener( listener );
    }

    public void removeListener( ITemporalVariable.Listener listener ) {
        series.removeListener( listener );
        variable.removeListener( listener );
    }

    public double estimateAverage( double startTime, double endTime ) {
        TimeData[] data = getData( startTime, endTime );
        if ( data.length == 0 ) {
            throw new RuntimeException( "Insufficient data" );
        }
        else if ( data.length == 1 ) {
//            System.out.println( "one data point" );
            return data[0].getValue();
        }
        else {
            Function.LinearFunction linearFit = MotionMath.getLinearFit( data );
//            System.out.println( "linearFit = " + linearFit );
            if ( Double.isNaN( linearFit.getMinOutput() ) || Double.isNaN( linearFit.getMaxOutput() ) ) {//could happen if there are duplicate points in the series
                return data[0].getValue();
            }

            //evaluate at midpoint
            return linearFit.evaluate( ( endTime + startTime ) / 2.0 );
        }
    }

    /*
    * Get all data between the specified times.
     */
    public TimeData[] getData( double startTime, double endTime ) {
        return series.getData( startTime, endTime );
    }

    //computes an average using min(s,numSamples) data points
    //assumes an equal distance between all samples
    public double estimateAverage( int s ) {
        double sum = 0;
        int count = Math.min( s, getSampleCount() );
        for ( int i = 0; i < count; i++ ) {
            sum += getRecentData( i ).getValue();
        }
        return sum / count;
    }

    public int getIndexForTime( double time ) {
        double closestTime = Double.POSITIVE_INFINITY;
        int index = -1;
        for ( int i = 0; i < getSampleCount(); i++ ) {
            if ( index == -1 || Math.abs( getData( i ).getTime() - time ) < Math.abs( closestTime - time ) ) {
                closestTime = getData( i ).getTime();
                index = i;
            }
        }
        return index;
    }

    public void setTimeData( int index, double time, double value ) {
        series.removeValue( index );
        series.insertValue( index, time, value );
    }

    public int[] getIndicesForTimeInterval( double t0, double t1 ) {
        if ( t1 < t0 ) {
            return getIndicesForTimeInterval( t1, t0 );
        }
        ArrayList times = new ArrayList();
        for ( int i = 0; i < getSampleCount(); i++ ) {
            final double v = getData( i ).getTime();
            if ( v >= t0 && v <= t1 ) {
                times.add( new Integer( i ) );
            }
        }
        int[] k = new int[times.size()];
        for ( int i = 0; i < k.length; i++ ) {
            k[i] = ( (Integer) times.get( i ) ).intValue();
        }
        return k;
    }

    public void removeAll( int[] indices ) {
        for ( int i = 0; i < indices.length; i++ ) {
            series.removeValue( indices[i] );
        }
    }

    public ITemporalVariable getDerivative() {
        final DefaultTemporalVariable derivative = new DefaultTemporalVariable();
        addListener( new ITemporalVariable.ListenerAdapter() {
            public void dataAdded( TimeData data ) {
                double velocityWindow = 5;
                TimeData a = MotionMath.getDerivative( MotionMath.smooth( getRecentSeries( (int) Math.min( velocityWindow, getSampleCount() ) ), 1 ) );
                derivative.addValue( a );
            }

            public void dataCleared() {
                derivative.clear();
            }
        } );
        return derivative;
    }

    public ITemporalVariable getIntegral() {
        final DefaultTemporalVariable integral = new DefaultTemporalVariable();
        addListener( new ITemporalVariable.ListenerAdapter() {
            public void dataAdded( TimeData data ) {
                double dt = 0;
                if ( getSampleCount() > 1 ) {
                    dt = getRecentData( 0 ).getTime() - getRecentData( 1 ).getTime();
                }
                else if ( getSampleCount() == 1 ) {
                    dt = getRecentData( 0 ).getTime();
                }
                TimeData newValue = new TimeData( integral.getValue() + getValue() * dt, data.getTime() );
                integral.addValue( newValue );
            }

            public void dataCleared() {
                integral.clear();
            }
        } );
        return integral;
    }

    public void stepInTime( double dt ) {
        addValue( getValue(), getLastTime() + dt );
    }

    public double getLastTime() {
        return getSampleCount() == 0 ? 0 : getRecentData( 0 ).getTime();
    }

    public void keepRange(double t0,double t1){
        TimeData[] toKeep = getData(t0,t1);
        clear();
        for (TimeData timeData : toKeep) {
            addValue(timeData);
        }
    }
}
