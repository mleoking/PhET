package edu.colorado.phet.ec3.test.phys1d;

import edu.colorado.phet.common.math.Function;

import java.awt.geom.Point2D;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Mar 4, 2007
 * Time: 12:04:21 AM
 * Copyright (c) Mar 4, 2007 by Sam Reid
 */

public class LinearSpline2D extends ParametricFunction2D {
    private Point2D[] points;//todo: could share with cubicspline2d

    public LinearSpline2D( Point2D[] points ) {
        this.points = points;
    }

    public String toStringSerialization() {
        return "points=" + Arrays.asList( points );
    }

    public Point2D evaluate( double alpha ) {
        if( alpha < 0 ) {//todo: this may break lots of things, may need an extrapolation
            alpha = 0;
        }
        if( alpha > 1 ) {
            alpha = 1;
        }
        int aIndex = (int)( alpha * ( points.length - 1 ) );
        int bIndex = aIndex + 1;
        Point2D a = points[aIndex];
        Point2D b = points[bIndex];
        Function.LinearFunction x = new Function.LinearFunction( aIndex, bIndex, a.getX(), b.getX() );
        Function.LinearFunction y = new Function.LinearFunction( aIndex, bIndex, a.getY(), b.getY() );
        return new Point2D.Double( x.evaluate( alpha * ( points.length - 1 ) ), y.evaluate( alpha * ( points.length - 1 ) ) );
    }

    public static void main( String[] args ) {
        Point2D[] pts = new Point2D[]{
                new Point2D.Double( 0, 0 ),
                new Point2D.Double( 10, 0 ),
                new Point2D.Double( 10, 10 )
        };
        double dAlpha = 0.1;
        LinearSpline2D linearSpline2D = new LinearSpline2D( pts );
        for( double alpha = 0; alpha <= 1.0; alpha += dAlpha ) {
            System.out.println( "alpha = " + alpha + ", location=" + linearSpline2D.evaluate( alpha ) );
        }
    }
}
