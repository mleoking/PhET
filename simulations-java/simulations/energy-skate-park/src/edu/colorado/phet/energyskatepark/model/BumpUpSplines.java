// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.model;

/**
 * Author: Sam Reid
 * Apr 6, 2007, 9:52:32 AM
 */
public class BumpUpSplines {
    private EnergySkateParkModel model;
    public static final double MIN_SPLINE_Y = 0.1;

    public BumpUpSplines( EnergySkateParkModel model ) {
        this.model = model;
    }

    public void bumpUpSplines() {
        for( int i = 0; i < model.getNumSplines(); i++ ) {
            EnergySkateParkSpline spline = model.getSpline( i );
            double y = spline.getMinY();
//            EnergySkateParkLogging.println( "y = " + y );
            if( y < MIN_SPLINE_Y ) {
                spline.translate( 0, Math.abs( y ) + MIN_SPLINE_Y );
            }
        }
    }
}
