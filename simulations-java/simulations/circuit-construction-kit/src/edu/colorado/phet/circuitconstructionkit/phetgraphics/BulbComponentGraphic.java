package edu.colorado.phet.circuitconstructionkit.phetgraphics;

import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: May 25, 2004
 * Time: 8:34:54 PM
 */
public class BulbComponentGraphic {

    private static final double WIDTH = 100;
    private static final double HEIGHT = 100;


    public static double determineTilt() {
        LightBulbGraphic lbg = new LightBulbGraphic( new Rectangle2D.Double( 0, 0, WIDTH, HEIGHT ) );
        double w = lbg.getCoverShape().getBounds().getWidth() / 2;
        double h = lbg.getCoverShape().getBounds().getHeight();
        return -Math.atan2( w, h );
    }

}
