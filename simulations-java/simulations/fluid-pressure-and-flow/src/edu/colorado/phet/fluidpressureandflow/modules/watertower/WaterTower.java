package edu.colorado.phet.fluidpressureandflow.modules.watertower;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;

/**
 * @author Sam Reid
 */
public class WaterTower {
    private Property<ImmutableVector2D> tankBottomCenter = new Property<ImmutableVector2D>( new ImmutableVector2D() );
    private static double TANK_RADIUS = 5;
    private static double TANK_HEIGHT = 15;

    public Rectangle2D.Double getTank() {
        return new Rectangle2D.Double( tankBottomCenter.getValue().getX() - TANK_RADIUS, tankBottomCenter.getValue().getY(), TANK_RADIUS * 2, TANK_HEIGHT );
    }

    public Property<ImmutableVector2D> getTankBottomCenter() {
        return tankBottomCenter;
    }
}
