/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.movingman.elements;

import edu.colorado.phet.common.model.ModelElement;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:25:22 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class Man extends ModelElement {
    double x;
    double x0;
    boolean grabbed = false;

    public Man(double x) {
        this.x0 = x;
        this.x = x;
    }

    public void stepInTime(double dt) {
    }

    public boolean isGrabbed() {
        return grabbed;
    }

    public void setGrabbed(boolean grabbed) {
        this.grabbed = grabbed;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
        updateObservers();
    }

    public void reset() {
        this.x = x0;
        updateObservers();
    }
}
