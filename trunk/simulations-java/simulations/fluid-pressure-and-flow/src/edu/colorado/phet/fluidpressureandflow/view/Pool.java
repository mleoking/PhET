package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * @author Sam Reid
 */
public class Pool {
    int height = 5;
    int width = 4;

    public Pool() {
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Shape getShape() {
        return new Rectangle2D.Double( -width / 2, -height, width, height );
    }

    public double getLiquidDensity() {
        return 1000; // kg/m^3
    }

    public double getMinX() {
        return getShape().getBounds2D().getMinX();
    }

    public double getMaxX() {
        return getShape().getBounds2D().getMaxX();
    }
}
