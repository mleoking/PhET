/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model.spline;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.umd.cs.piccolo.util.PBounds;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Sep 26, 2005
 * Time: 9:22:30 PM
 * Copyright (c) Sep 26, 2005 by Sam Reid
 */

public class Segment {
    double x0;
    double y0;
    double x1;
    double y1;

    public Segment( double x0, double y0, double x1, double y1 ) {
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
    }

    public Segment( Point2D a, Point2D b ) {
        this( a.getX(), a.getY(), b.getX(), b.getY() );
    }

    public double getLength() {
        double a = y1 - y0;
        double b = x1 - x0;
        return Math.sqrt( a * a + b * b );
    }

    public Point2D evaluate( double scalarDist ) {
        AbstractVector2D vec = toVector();
        vec = vec.getInstanceOfMagnitude( scalarDist );
        return vec.getDestination( new Point2D.Double( x0, y0 ) );
    }

    private AbstractVector2D toVector() {
        AbstractVector2D vec = new Vector2D.Double( new Point2D.Double( x0, y0 ), new Point2D.Double( x1, y1 ) );
        return vec;
    }

    public Line2D.Double toLine2D() {
        return new Line2D.Double( x0, y0, x1, y1 );
    }

    public Shape getShape() {
        return new BasicStroke( 4 ).createStrokedShape( toLine2D() );
    }

    public Point2D getCenter2D() {
        return new PBounds( toLine2D().getBounds2D() ).getCenter2D();
    }

    public double getAngle() {
        return toVector().getAngle();
    }

    public AbstractVector2D getUnitNormalVector() {
        return toVector().getNormalVector().getNormalizedInstance();
    }

    public AbstractVector2D getUnitDirectionVector() {
        return toVector().getNormalizedInstance();
    }
}
