package edu.colorado.phet.recordandplayback.test;

import java.util.ArrayList;

/**
 * Example Particle model class for sample application.
 *
 * @author Sam Reid
 */
public class Particle {
    private double x;
    private double y;
    private ArrayList<Listener> listeners = new ArrayList<Listener>();

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void translate(double dx, double dy) {
        this.x += dx;
        this.y += dy;
        notifyListeners();
    }

    private void notifyListeners() {
        for (Listener listener : listeners) {
            listener.moved();
        }
    }

    public void setPosition(double x, double y) {
        setX(x);
        setY(y);
    }

    static interface Listener {
        void moved();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
        notifyListeners();
    }

    public void setY(double y) {
        this.y = y;
        notifyListeners();
    }
}
