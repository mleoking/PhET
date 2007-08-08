package edu.colorado.phet.rotation.model;

import edu.colorado.phet.common.motion.model.*;

/**
 * Author: Sam Reid
 * Jul 26, 2007, 8:04:10 PM
 */
public class SeriesVariable {
    private ISimulationVariable variable = new DefaultSimulationVariable();
    private ITimeSeries series = new DefaultTimeSeries();

    public SeriesVariable() {
    }

    public void clear() {
        series.clear();
    }

    public void updateSeriesAndState( double value, double time ) {
        variable.setValue( value );
        series.addValue( value, time );
    }

    public int getSampleCount() {
        return series.getSampleCount();
    }

    public void setValue( double value ) {
        variable.setValue( value );
    }

    public double getValueForTime( double time ) {
        return series.getValueForTime( time );
    }

    public ISimulationVariable getVariable() {
        return variable;
    }

    public ITimeSeries getSeries() {
        return series;
    }

    public void setValueForTime( double time ) {
        setValue( getValueForTime( time ) );
    }

    public double getLastValue() {
        return series.getRecentData( 0 ).getValue();
    }

    public TimeData[] getRecentSeries( int numSamples ) {
        return series.getRecentSeries( numSamples );
    }

    public double getValue() {
        return getVariable().getValue();
    }
}
