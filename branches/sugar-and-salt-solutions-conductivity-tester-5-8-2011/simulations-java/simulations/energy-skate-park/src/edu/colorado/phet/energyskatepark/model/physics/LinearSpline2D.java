// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.model.physics;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.SerializablePoint2D;
import edu.colorado.phet.common.spline.ControlPointParametricFunction2D;
import edu.colorado.phet.energyskatepark.util.EnergySkateParkLogging;

import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Mar 4, 2007
 * Time: 12:04:21 AM
 */

public class LinearSpline2D extends ControlPointParametricFunction2D {

    public LinearSpline2D( SerializablePoint2D[] points ) {
        super( points );
    }

    public String toStringSerialization() {
        return "points=" + Arrays.asList( getControlPoints() );
    }

    public SerializablePoint2D evaluate( double alpha ) {

        int aIndex = (int)( alpha * ( getControlPoints().length - 1 ) );

        if( alpha < 0 ) {//todo: this may break lots of things, may need an extrapolation
//            alpha = 0;
            aIndex = 0;
        }
        if( alpha > 1 - 1E-6 ) {
//            alpha = 1 - 1E-6;
            aIndex = getControlPoints().length - 2;
        }

        SerializablePoint2D a = getControlPoints()[aIndex];
        SerializablePoint2D b = getControlPoints()[aIndex + 1];
        Function.LinearFunction x = new Function.LinearFunction( aIndex, aIndex + 1, a.getX(), b.getX() );
        Function.LinearFunction y = new Function.LinearFunction( aIndex, aIndex + 1, a.getY(), b.getY() );
        return new SerializablePoint2D( x.evaluate( alpha * ( getControlPoints().length - 1 ) ), y.evaluate( alpha * ( getControlPoints().length - 1 ) ) );
    }

    public static void main( String[] args ) {
        SerializablePoint2D[] pts = new SerializablePoint2D[]{
                new SerializablePoint2D( 0, 0 ),
                new SerializablePoint2D( 10, 0 ),
                new SerializablePoint2D( 10, 10 )
        };
        double dAlpha = 0.1;
        LinearSpline2D linearSpline2D = new LinearSpline2D( pts );
        for( double alpha = -1; alpha <= 2; alpha += dAlpha ) {
            EnergySkateParkLogging.println( "alpha = " + alpha + ", location=" + linearSpline2D.evaluate( alpha ) );
        }
    }
}
