package edu.colorado.phet.common.motion.model;

/**
 * Author: Sam Reid
 * Jun 25, 2007, 11:31:03 PM
 */
public interface ITemporalVariable extends IVariable {
    TimeData[] getRecentSeries( int numPts );

    TimeData getData( int index );

    TimeData getRecentData( int index );

    int getSampleCount();

    void clear();

    double getValue();

    void addValue( double v, double time );

    double getTime();

    TimeData getMax();

    TimeData getMin();

    double getValueForTime( double time );

    void addListener( Listener observableTimeSeriesListener );

    void updateSeriesAndState( double magnitude, double time );

    void setPlaybackTime( double time );

    public static interface Listener {
        void dataAdded( TimeData data );

        void dataCleared();
    }
}
