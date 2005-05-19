/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.model;

/**
 * User: Sam Reid
 * Date: Mar 24, 2005
 * Time: 11:36:22 AM
 * Copyright (c) Mar 24, 2005 by Sam Reid
 */
public interface TimeListener {
    void recordingStarted();

    void recordingPaused();

    void recordingFinished();

    void playbackStarted();

    void playbackPaused();

    void playbackFinished();

    void reset();

    void rewind();
}
