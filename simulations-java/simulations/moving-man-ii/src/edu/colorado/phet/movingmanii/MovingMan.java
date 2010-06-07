package edu.colorado.phet.movingmanii;

import java.util.ArrayList;

/**
 * @author Sam Reid
 */
public class MovingMan {
    private MotionStrategy motionStrategy = new PositionBased();
    private ArrayList<Listener> listeners = new ArrayList<Listener>();
    private double position;
    private double velocity;
    private double acceleration;

    public void setPosition(double x) {
        this.position = x;
        System.out.println("x = " + x);
        for (Listener listener : listeners) {
            listener.changed();
        }
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public double getPosition() {
        return position;
    }

    static interface Listener {
        void changed();
    }

    public static class MotionStrategy {
    }

    private class PositionBased extends MotionStrategy {
    }
}
