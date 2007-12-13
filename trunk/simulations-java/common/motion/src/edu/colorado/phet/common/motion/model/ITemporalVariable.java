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

    TimeData getMin();

    TimeData getMax();

    void addValue( double magnitude, double time );

    void setPlaybackTime( double time );

    void addListener( Listener listener );

    void removeListener( Listener listener );

    //computes an average using min(s,numSamples) data points
    double estimateAverage( int s);

    public static interface Listener extends IVariable.Listener {
        void dataAdded( TimeData data );

        void dataCleared();
    }

    public static class ListenerAdapter implements Listener {
        public void dataAdded( TimeData data ) {
        }

        public void dataCleared() {
        }

        public void valueChanged() {
        }
    }
}
