package edu.colorado.phet.rotation.model;

import edu.colorado.phet.common.motion.model.*;

/**
 * Author: Sam Reid
 * Jul 26, 2007, 8:04:10 PM
 */
public class DefaultTemporalVariable implements ITemporalVariable, IVariable {
    private DefaultSimulationVariable variable = new DefaultSimulationVariable();
    private DefaultTimeSeries series = new DefaultTimeSeries();

    public DefaultTemporalVariable() {
    }

    public void updateSeriesAndState( double value, double time ) {
        variable.setValue( value );
        series.addValue( value, time );
    }

    public TimeData getData() {
        return variable.getData();
    }

    public void setValue( double value ) {
        variable.setValue( value );
    }

//    public ISimulationVariable getVariable() {
//        return variable;
//    }
//
//    public ITimeSeries getSeries() {
//        return series;
//    }

    public void setValueForTime( double time ) {
        setValue( getValueForTime( time ) );
    }

    public double getLastValue() {
        return series.getRecentData( 0 ).getValue();
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

    public TimeData getRecentData( int index ) {
        return series.getRecentData( index );
    }

    public int getSampleCount() {
        return series.getSampleCount();
    }

    public void clear() {
        series.clear();
    }

    public void addValue( double v, double time ) {
        series.addValue( v, time );
    }

    public double getTime() {
        return series.getTime();
    }

    public TimeData getMax() {
        return series.getMax();
    }

    public TimeData getMin() {
        return series.getMin();
    }

    public double getValueForTime( double time ) {
        return series.getValueForTime( time );
    }

    public void addListener( ITemporalVariable.Listener observableTimeSeriesListener ) {
        series.addListener( observableTimeSeriesListener );
    }
}
