// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.fluidpressure;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.fluidpressureandflow.common.model.Units;

/**
 * The pool is the region of water in which the sensors can be submerged.
 *
 * @author Sam Reid
 */
public class Pool {
    public static final double DEFAULT_HEIGHT = new Units().feetToMeters( 10 );//10 foot deep pool, a customary depth for the deep end
    private final double height = DEFAULT_HEIGHT;
    private final double width = 4;

    public Pool() {
    }

    public double getHeight() {
        return height;
    }

    public Shape getShape() {
        return new Rectangle2D.Double( -width / 2, -height, width, height );
    }

    public double getMinX() {
        return getShape().getBounds2D().getMinX();
    }

    public double getMaxX() {
        return getShape().getBounds2D().getMaxX();
    }

    public double getMaxY() {
        return getShape().getBounds2D().getMaxY();
    }

    public double getMinY() {
        return -getHeight();   //TODO: why is this different than other implementations of getMin.()?
    }

    public Point2D getTopRight() {
        return new Point2D.Double( getMaxX(), getMaxY() );
    }
}
