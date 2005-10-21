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

    public SplineSurface( AbstractSpline top ) {
        this.top = top;
        this.bottom = top.createReverseSpline();
    }

    public SplineSurface copy() {
        SplineSurface copy = new SplineSurface( top.copySpline() );
        return copy;
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
}
