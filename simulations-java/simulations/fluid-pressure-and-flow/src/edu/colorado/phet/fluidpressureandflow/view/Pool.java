package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * @author Sam Reid
 */
public class Pool {
    int height = 5;
    int width = 2;

    public Pool() {
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Shape getShape() {
        return new Rectangle2D.Double( -width, -height, width * 2, height );
    }

    public double getLiquidDensity() {
        return 1000; // kg/m^3
    }
}
