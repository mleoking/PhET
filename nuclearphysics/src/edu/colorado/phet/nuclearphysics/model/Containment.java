/**
 * Class: Containment
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Oct 6, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.common.util.SimpleObservable;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Containment extends SimpleObservable {
    //public class Containment extends Box2D {
    //    private Rectangle2D shape;
    Shape shape;
    double opacity = 1;
    private ArrayList resizeListeners = new ArrayList();

    public interface ResizeListener {
        void resized( Containment containment );
    }

    public Containment( Point2D center, double radius ) {
        Shape shape = new Ellipse2D.Double( center.getX() - radius, center.getY() - radius,
                                            radius * 2, radius * 2 );
        this.shape = shape;
    }

    public void adjustRadius( double dr ) {
        Ellipse2D containmentShape = (Ellipse2D)getShape();
        containmentShape.setFrame( containmentShape.getX() + dr, containmentShape.getY() + dr,
                                   containmentShape.getWidth() - dr * 2, containmentShape.getHeight() - dr * 2 );
        notifyResizeListeners();
    }

    private void notifyResizeListeners() {
        for( int i = 0; i < resizeListeners.size(); i++ ) {
            ResizeListener resizeListener = (ResizeListener)resizeListeners.get( i );
            resizeListener.resized( this );
        }
    }

    public void addResizeListener( ResizeListener listener ) {
        resizeListeners.add( listener );
    }

    public void removeResizeListener( ResizeListener listener ) {
        resizeListeners.remove( listener );
    }

    public Shape getShape() {
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
