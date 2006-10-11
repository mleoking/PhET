/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model.spline;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;

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
    private double x0;
    private double y0;
    private double x1;
    private double y1;
    private boolean shapeDirty = true;
    private Shape shape;
    private float thickness;
    private static int static_index = 0;
    private int index = 0;

    public Segment( double x0, double y0, double x1, double y1, float thickness ) {
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        this.thickness = thickness;
        this.index = static_index++;
    }

    public Segment( Point2D a, Point2D b, float thickness ) {
        this( a.getX(), a.getY(), b.getX(), b.getY(), thickness );
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
        return (AbstractVector2D)new Vector2D.Double( new Point2D.Double( x0, y0 ), new Point2D.Double( x1, y1 ) );
    }

    public Line2D.Double toLine2D() {
        return new Line2D.Double( x0, y0, x1, y1 );
    }

    public Shape getShape() {
        if( shapeDirty ) {
            this.shape = new BasicStroke( thickness ).createStrokedShape( toLine2D() );
            shapeDirty = false;
        }
        return shape;
    }

    public Point2D getCenter2D() {
        return new Point2D.Double( ( x1 + x0 ) / 2, ( y1 + y0 ) / 2 );
    }

    public double getAngle() {
        return toVector().getAngle();
    }

    public AbstractVector2D getUnitNormalVector() {
        return getUnitDirectionVector().getNormalVector();
    }

    public AbstractVector2D getUnitDirectionVector() {
        return toVector().getNormalizedInstance();
    }

    public float getThickness() {
        return thickness;
    }

    public String toString() {
        return "<Seg_" + index + ">: x0=" + x0 + ", y0=" + y0;
    }

    public int getID() {
        return index;
    }

    public Point2D getP0() {
        return new Point2D.Double( x0, y0 );
    }

    public Point2D getP1() {
        return new Point2D.Double( x1, y1 );
    }

//    public Point2D getMidpoint() {
//        return new Point2D.Double( (x1+x0)/2,(y1+y0)/2); 
//    }
}
