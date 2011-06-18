// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.view;

/**
 * Listener interface for receiving events from anything that controls time.
 */
public interface TimeControlListener {

    void stepPressed();

    void playPressed();

    void pausePressed();

    void stepBackPressed();

    void restartPressed();

    public static class TimeControlAdapter implements TimeControlListener {

        public void stepPressed() {
        }

        public void playPressed() {
        }

        public void pausePressed() {
        }

        public void stepBackPressed() {
        }

        public void restartPressed() {
        }
    }
}