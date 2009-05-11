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
    private double bottomOfStratumY;
    private double height;

    public Stratum( double bottomOfStratumY, double height ) {
        this.bottomOfStratumY = bottomOfStratumY;
        this.height = height;
    }

    public double getBottomOfStratumY() {
        return bottomOfStratumY;
    }

    public double getHeight() {
        return height;
    }

    public Shape getTopLine() {
        return new Line2D.Double(-1000,bottomOfStratumY+height,1000,bottomOfStratumY+height);
    }

    public Shape getBottomLine() {
        return new Line2D.Double(-1000,bottomOfStratumY,1000,bottomOfStratumY);
    }
}
