/**
 * Class: Containment
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Oct 6, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.common.util.SimpleObservable;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Containment extends SimpleObservable {
    //public class Containment extends Box2D {
    //    private Rectangle2D shape;
    Shape shape;
    double opacity = 1;

    public Containment( Shape shape ) {
        //    public Containment( Rectangle2D shape ) {
        this.shape = shape;
    }

    public Shape geShape() {
        return shape;
    }

    public Rectangle2D getBounds2D() {
        return shape.getBounds2D();
    }

    public Point2D.Double getNeutronLaunchPoint() {
        return new Point2D.Double( shape.getBounds2D().getMinX(),
                                   shape.getBounds2D().getMinY() + shape.getBounds2D().getHeight() / 2 );
    }

    public void dissolve() {
        double decr = 0.05;
        opacity = Math.max( opacity - decr, 0 );
        notifyObservers();
    }

    public double getOpacity() {
        return opacity;
    }
}
