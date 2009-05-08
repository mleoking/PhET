package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: May 8, 2009
 * Time: 4:42:03 PM
 */
public class Layer {
    private double bottomOfLayerY;
    private double height;

    public Layer( double bottomOfLayerY, double height ) {
        this.bottomOfLayerY = bottomOfLayerY;
        this.height = height;
    }

    public double getBottomOfLayerY() {
        return bottomOfLayerY;
    }

    public double getHeight() {
        return height;
    }

    public Shape getTopLine() {
        return new Line2D.Double(-1000,bottomOfLayerY+height,1000,bottomOfLayerY+height);
    }

    public Shape getBottomLine() {
        return new Line2D.Double(-1000,bottomOfLayerY,1000,bottomOfLayerY);
    }
}
