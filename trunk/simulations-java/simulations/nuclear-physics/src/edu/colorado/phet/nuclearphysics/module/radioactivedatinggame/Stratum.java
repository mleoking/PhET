/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.Shape;
import java.awt.geom.Line2D;

/**
 * This class represents a "stratum" in the model, which means a layer of rock
 * and/or soil with internally consistent characteristics that distinguishes 
 * it from contiguous layers (definition obtained from wikipedia).
 */
public class Stratum {
    private double bottomOfLayerY;
    private double height;

    public Stratum( double bottomOfLayerY, double height ) {
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
