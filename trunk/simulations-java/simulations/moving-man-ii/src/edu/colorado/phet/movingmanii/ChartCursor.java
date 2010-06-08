package edu.colorado.phet.movingmanii;

import java.util.ArrayList;

/**
 * @author Sam Reid
 */
public class ChartCursor {
    private double time;
    private ArrayList<Listener> listeners = new ArrayList<Listener>();

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public static interface Listener {
        void positionChanged();
    }

    public ChartCursor() {
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
        for (Listener listener : listeners) {
            listener.positionChanged();
        }
    }
}
