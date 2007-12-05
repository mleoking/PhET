package edu.colorado.phet.common.motion.model;

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
}
