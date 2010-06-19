package edu.colorado.phet.movingman.charts;

import java.util.ArrayList;

/**
 * @author Sam Reid
 */
public class ChartCursor {
    private double time;
    private ArrayList<Listener> listeners = new ArrayList<Listener>();
    private boolean visible;

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public static interface Listener {
        void positionChanged();

        void visibilityChanged();
    }

    public ChartCursor() {
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        if (this.time != time) {
            this.time = time;
            for (Listener listener : listeners) {
                listener.positionChanged();
            }
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        for (Listener listener : listeners) {
            listener.visibilityChanged();
        }
    }

    public static class Adapter implements Listener {
        public void positionChanged() {
        }

        public void visibilityChanged() {

        }
    }
}
