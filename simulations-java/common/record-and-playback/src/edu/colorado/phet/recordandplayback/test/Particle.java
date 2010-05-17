package edu.colorado.phet.recordandplayback.test;

import java.util.ArrayList;

/**
* Created by IntelliJ IDEA.
* User: Sam
* Date: May 17, 2010
* Time: 2:45:44 PM
* To change this template use File | Settings | File Templates.
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
