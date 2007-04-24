package edu.colorado.phet.energyskatepark.model;

/**
 * Author: Sam Reid
 * Apr 6, 2007, 9:52:32 AM
 */
public class BumpUpSplines {
    private EnergySkateParkModel model;

    public BumpUpSplines( EnergySkateParkModel model ) {
        this.model = model;
    }

    public void bumpUpSplines() {
        for( int i = 0; i < model.getNumSplines(); i++ ) {
            EnergySkateParkSpline spline = model.getSpline( i );
            double y = spline.getMinY();
//            System.out.println( "y = " + y );
            if( y < 0 ) {
                spline.translate( 0, Math.abs( y ) );
            }
        }
    }
}
