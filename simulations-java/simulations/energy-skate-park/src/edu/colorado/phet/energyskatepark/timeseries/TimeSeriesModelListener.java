/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.timeseries;

/**
 * User: Sam Reid
 * Date: Mar 24, 2005
 * Time: 11:36:22 AM
 * Copyright (c) Mar 24, 2005 by Sam Reid
 */
public interface TimeSeriesModelListener {
    void liveModeStarted();

    void recordingStarted();

    void recordingPaused();

    void recordingFinished();

    void playbackStarted();

    void playbackPaused();

    void playbackFinished();

    void reset();

    void rewind();

    void liveModePaused();

    void seriesPointAdded();
}
