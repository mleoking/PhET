package edu.colorado.phet.fluidpressureandflow.modules.watertower;

import java.awt.geom.Rectangle2D;

/**
 * @author Sam Reid
 */
public class WaterTower {
    private Rectangle2D.Double tank = new Rectangle2D.Double( 0, 0, 1, 1 );

    public Rectangle2D.Double getTank() {
        return tank;
    }
}
