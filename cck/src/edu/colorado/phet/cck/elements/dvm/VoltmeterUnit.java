/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.dvm;


import edu.colorado.phet.common.model.simpleobservable.SimpleObservable;

/**
 * User: Sam Reid
 * Date: Oct 26, 2003
 * Time: 2:06:51 AM
 * Copyright (c) Oct 26, 2003 by Sam Reid
 */
public class VoltmeterUnit extends SimpleObservable {
    double x;
    double y;

    public VoltmeterUnit(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setLocation(double x, double y) {
        this.x = x;
        this.y = y;
        updateObservers();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void translate(double dx, double dy) {
        x += dx;
        y += dy;
        updateObservers();
    }
}
