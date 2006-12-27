/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.common.util.SimpleObservable;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 1:37:51 AM
 * Copyright (c) May 24, 2004 by Sam Reid
 */
public class Junction extends SimpleObservable {
    private double x;
    private double y;
    int label = 0;
    private static int nextLabel = 0;

    public Junction( double x, double y ) {
        this.x = x;
        this.y = y;
        this.label = nextLabel++;
    }

    public String toString() {
        return "Junction_" + label + "[" + x + "," + y + "]";
    }

    public Point2D.Double getPosition() {
        return new Point2D.Double( x, y );
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void translate( double dx, double dy ) {
        x += dx;
        y += dy;
        notifyObservers();
    }

    public void setPosition( double x, double y ) {
        this.x = x;
        this.y = y;
        notifyObservers();
    }

    public double getDistance( Junction dragging ) {
        return getPosition().distance( dragging.getPosition() );
    }

    public double getDistance( Point2D pt ) {
        return pt.distance( getX(), getY() );
    }

    public int getLabel() {
        return label;
    }

}
