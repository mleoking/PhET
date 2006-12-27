package edu.colorado.phet.bernoulli.spline.segments;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Aug 24, 2003
 * Time: 4:22:54 AM
 * Copyright (c) Aug 24, 2003 by Sam Reid
 */
public class SlopeInterceptLine {
    double slope;
    double intercept;

    public SlopeInterceptLine( double slope, double intercept ) {
        this.slope = slope;
        this.intercept = intercept;
    }

    public double getSlope() {
        return slope;
    }

    public double getIntercept() {
        return intercept;
    }

    public Point2D.Double getIntersectionPoint( SlopeInterceptLine line2 ) {
        if( line2.slope == slope ) {
            throw new RuntimeException( "Intersection is not a point for parallel lines." );
        }
        double x = -( line2.intercept - intercept ) / ( line2.slope - slope );
        double y = slope * x + intercept;
        return new Point2D.Double( x, y );
    }

    public static void main( String[] args ) {
        SlopeInterceptLine a = new SlopeInterceptLine( 1, 0 );
        SlopeInterceptLine b = new SlopeInterceptLine( -1, 0 );
        System.out.println( "a.getIntersectionPoint(b) = " + a.getIntersectionPoint( b ) );
        SlopeInterceptLine c = new SlopeInterceptLine( 0, 2 );
        SlopeInterceptLine d = new SlopeInterceptLine( 1, -1 );
        System.out.println( "c.getIntersectionPoint(d) = " + c.getIntersectionPoint( d ) );
    }
}
