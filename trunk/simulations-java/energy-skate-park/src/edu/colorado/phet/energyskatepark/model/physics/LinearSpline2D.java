package edu.colorado.phet.energyskatepark.model.physics;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.timeseries.SPoint2D;

import java.awt.geom.Point2D;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Mar 4, 2007
 * Time: 12:04:21 AM
 * Copyright (c) Mar 4, 2007 by Sam Reid
 */

public class LinearSpline2D extends ControlPointParametricFunction2D {

    public LinearSpline2D( Point2D[] points ) {
        super( points );
    }

    public String toStringSerialization() {
        return "points=" + Arrays.asList( getControlPoints() );
    }

    public Point2D evaluate( double alpha ) {

        int aIndex = (int)( alpha * ( getControlPoints().length - 1 ) );

        if( alpha < 0 ) {//todo: this may break lots of things, may need an extrapolation
//            alpha = 0;
            aIndex = 0;
        }
        if( alpha > 1 - 1E-6 ) {
//            alpha = 1 - 1E-6;
            aIndex = getControlPoints().length - 2;
        }

        Point2D a = getControlPoints()[aIndex];
        Point2D b = getControlPoints()[aIndex + 1];
        Function.LinearFunction x = new Function.LinearFunction( aIndex, aIndex + 1, a.getX(), b.getX() );
        Function.LinearFunction y = new Function.LinearFunction( aIndex, aIndex + 1, a.getY(), b.getY() );
        return new SPoint2D.Double( x.evaluate( alpha * ( getControlPoints().length - 1 ) ), y.evaluate( alpha * ( getControlPoints().length - 1 ) ) );
    }

    public static void main( String[] args ) {
        Point2D[] pts = new Point2D[]{
                new SPoint2D.Double( 0, 0 ),
                new SPoint2D.Double( 10, 0 ),
                new SPoint2D.Double( 10, 10 )
        };
        double dAlpha = 0.1;
        LinearSpline2D linearSpline2D = new LinearSpline2D( pts );
        for( double alpha = -1; alpha <= 2; alpha += dAlpha ) {
            System.out.println( "alpha = " + alpha + ", location=" + linearSpline2D.evaluate( alpha ) );
        }
    }
}
