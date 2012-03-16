// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.theramp.common;

import java.awt.geom.Rectangle2D;

public class Range2D {
    private double minX;
    private double minY;
    private double maxX;
    private double maxY;

    public Range2D( double minX, double minY, double maxX, double maxY ) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public Range2D( Rectangle2D rectangle ) {
        this( rectangle.getX(), rectangle.getY(), rectangle.getX() + rectangle.getWidth(), rectangle.getY() + rectangle.getHeight() );
    }

    public Range2D( Range2D range ) {
        this( range.minX, range.minY, range.maxX, range.maxY );
    }

    public Range2D union( Range2D range ) {
        Range2D union = new Range2D( Math.min( minX, range.minX ),
                                     Math.min( minY, range.minY ),
                                     Math.max( maxX, range.maxX ),
                                     Math.max( maxY, range.maxY ) );
        return union;
    }

    public void setRange( double minX, double minY, double maxX, double maxY ) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public void setRange( Range2D range ) {
        this.minX = range.getMinX();
        this.minY = range.getMinY();
        this.maxX = range.getMaxX();
        this.maxY = range.getMaxY();
    }

    public void setMinX( double minX ) {
        this.minX = minX;
    }

    public double getMinX() {
        return minX;
    }

    public void setMaxX( double maxX ) {
        this.maxX = maxX;
    }

    public double getMaxX() {
        return maxX;
    }

    public void setMinY( double minY ) {
        this.minY = minY;
    }

    public double getMinY() {
        return minY;
    }

    public void setMaxY( double maxY ) {
        this.maxY = maxY;
    }

    public double getMaxY() {
        return maxY;
    }

    public Rectangle2D.Double getBounds() {
        Rectangle2D.Double r = new Rectangle2D.Double( minX, minY, maxX - minX, maxY - minY );
        return r;
    }

    public double getWidth() {
        return maxX - minX;
    }

    public double getHeight() {
        return maxY - minY;
    }

    public String toString() {
        return "x=[" + minX + ", " + maxX + "], y=[" + minY + ", " + maxY + "]";
    }
}