/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model.spline;

/**
 * User: Sam Reid
 * Date: Oct 21, 2005
 * Time: 2:56:43 PM
 * Copyright (c) Oct 21, 2005 by Sam Reid
 */

public class SplineSurface {
    private AbstractSpline spline;
    private boolean interactive;

    public SplineSurface( AbstractSpline spline ) {
        this( spline, true );
    }

    public SplineSurface( AbstractSpline spline, boolean interactive ) {
        this.spline = spline;
        this.interactive = interactive;
    }

    public SplineSurface copy() {
        return new SplineSurface( spline.copySpline() );
    }

    public boolean isUserControlled() {
        return spline.isUserControlled();
    }

    public AbstractSpline getSpline() {
        return spline;
    }

    public void printControlPointCode() {
        spline.printControlPointCode();
    }

    public double getLength() {
        return spline.getSegmentPath().getLength();
    }

    public boolean equals( Object obj ) {
        return obj instanceof SplineSurface && ( (SplineSurface)obj ).getSpline().equals( getSpline() );
    }

    public void translate( double x, double y ) {
        getSpline().translate( x, y );
    }

    public void setInteractive( boolean b ) {
        this.interactive = b;
    }

    public boolean isInteractive() {
        return interactive;
    }

    public boolean contains( AbstractSpline spline ) {
        return this.spline == spline;
    }
}
