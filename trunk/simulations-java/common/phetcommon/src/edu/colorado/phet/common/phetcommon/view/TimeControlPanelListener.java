package edu.colorado.phet.common.phetcommon.view;

/**
 * Listener interface for receiving events from time controls.
 */
public interface TimeControlPanelListener {
    
    void stepPressed();

    void playPressed();

    void pausePressed();

    void restartPressed();
    
    public static class TimeControlPanelAdapter implements TimeControlPanelListener {

        public void stepPressed() {}

        public void playPressed() {}

        public void pausePressed() {}

        public void restartPressed() {}
    }
}