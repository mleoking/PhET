/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.ammeter;

import edu.colorado.phet.cck.common.SimpleObservable;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Nov 16, 2003
 * Time: 12:56:43 AM
 * Copyright (c) Nov 16, 2003 by Sam Reid
 */
public class Ammeter extends SimpleObservable {
    double x;
    double y;

    public Ammeter(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point2D.Double getLocation() {
        return new Point2D.Double(x, y);
    }

    public void translate(double dx, double dy) {
        x += dx;
        y += dy;
        updateObservers();
    }

    public double getCurrent() {
        return 0;
    }
}
