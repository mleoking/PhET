// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.plots;

import edu.colorado.phet.common.piccolophet.nodes.ZoomControlNode;
import org.jfree.chart.axis.ValueAxis;

/**
 * Author: Sam Reid
 * May 22, 2007, 3:52:05 AM
 */
public class VerticalZoomControl extends DefaultZoomControl {
    private static final int MAX_ZOOM_OUT = Integer.MAX_VALUE;
    private static final int MAX_ZOOM_IN = 13;

    public VerticalZoomControl( ValueAxis axis ) {
        super( ZoomControlNode.VERTICAL, axis );
    }

    protected void updateZoom() {
        int zoom = getZoom();
        setZoomInEnabled( zoom > -MAX_ZOOM_IN );
        setZoomOutEnabled( zoom < MAX_ZOOM_OUT );
        double range = 7000 + zoom * 1000;
        if( zoom <= -7 ) {
            int powers = Math.abs( zoom + 6 );
//            EnergySkateParkLogging.println( "powers = " + powers );
            double pow = ( Math.pow( 2, powers ) );
//            EnergySkateParkLogging.println( "pwo = " + pow );
            range = 1000 / pow;
        }
//        EnergySkateParkLogging.println( "zoom=" + zoom + ", range = " + range );
        range = Math.max( range, 0 );
        double minY = zoom < 0 ? -500 : -500 - zoom * 500;
        if( zoom <= -7 ) {
            minY = -range / 2;
        }
//        EnergySkateParkLogging.println( "minY = " + minY );
        getAxis().setRange( minY, minY + range );
    }

    public void setZoom( int zoom ) {
        super.setZoom( zoom );
        updateZoom();
    }
}
