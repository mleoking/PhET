// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;

/**
 * Author: Sam Reid
 * Apr 6, 2007, 9:52:32 AM
 */
public class BumpUpSplines {
    private final EnergySkateParkModel model;
    private final ImmutableRectangle2D bounds;

    //Treat the ground specially to make sure no track goes below y=0, since that causes problems for showing PE in pie chart.
    public static final double MIN_SPLINE_Y = 0.1;

    public BumpUpSplines( EnergySkateParkModel model, ImmutableRectangle2D bounds ) {
        this.model = model;
        this.bounds = bounds;
    }

    public void bumpUpSplines() {
        for ( int i = 0; i < model.getNumSplines(); i++ ) {
            EnergySkateParkSpline spline = model.getSpline( i );
            if ( spline.getMinY() < MIN_SPLINE_Y ) {
                spline.translate( 0, Math.abs( spline.getMinY() ) + MIN_SPLINE_Y );
            }
            else if ( spline.getMaxY() > bounds.getMaxY() ) {
                spline.translate( 0, -spline.getMaxY() + bounds.getMaxY() );
            }

            if ( spline.getMinX() < bounds.x ) {
                spline.translate( -spline.getMinX() + bounds.x, 0 );
            }
            else if ( spline.getMaxX() > bounds.getMaxX() ) {
                spline.translate( -spline.getMaxX() + bounds.getMaxX(), 0 );
            }
        }
    }
}