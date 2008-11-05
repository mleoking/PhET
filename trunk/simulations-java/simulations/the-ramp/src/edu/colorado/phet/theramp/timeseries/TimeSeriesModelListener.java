/*  */
package edu.colorado.phet.theramp.timeseries;

/**
 * User: Sam Reid
 * Date: Mar 24, 2005
 * Time: 11:36:22 AM
 */
public interface TimeSeriesModelListener {
    void recordingStarted();

    void recordingPaused();

    void recordingFinished();

    void playbackStarted();

    void playbackPaused();

    void playbackFinished();

    void reset();

    void rewind();
}
