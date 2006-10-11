/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model.spline;

/**
 * User: Sam Reid
 * Date: Oct 21, 2005
 * Time: 2:56:43 PM
 * Copyright (c) Oct 21, 2005 by Sam Reid
 */

public class SplineSurface {
    private AbstractSpline top;
    private AbstractSpline bottom;
    private boolean interactive = true;

    public SplineSurface( AbstractSpline top ) {
        this.top = top;
        this.bottom = top.createReverseSpline();
    }

    public SplineSurface copy() {
        SplineSurface copy = new SplineSurface( top.copySpline() );
        return copy;
    }

    public boolean isUserControlled() {
        return top.isUserControlled() || bottom.isUserControlled();
    }

    public AbstractSpline getTop() {
        return top;
    }

    public AbstractSpline getBottom() {
        return bottom;
    }

    public void printControlPointCode() {
        top.printControlPointCode();
    }

    public double getLength() {
        return top.getSegmentPath().getLength();
    }

    public boolean equals( Object obj ) {
        return obj instanceof SplineSurface && ( (SplineSurface)obj ).getTop().equals( getTop() );
    }

    public void translate( double x, double y ) {
        getTop().translate( x, y );
        getBottom().translate( x, y );
    }

    public void setInteractive( boolean b ) {
        this.interactive = b;
    }

    public boolean isInteractive() {
        return interactive;
    }
}
