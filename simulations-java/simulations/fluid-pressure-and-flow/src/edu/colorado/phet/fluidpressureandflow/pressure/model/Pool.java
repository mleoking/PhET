// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.model;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.fluidpressureandflow.common.model.units.Units;

/**
 * The pool is the region of water in which the sensors can be submerged.
 *
 * @author Sam Reid
 */
public class Pool implements IPool {

    //10 foot deep pool, a customary depth for the deep end in the United States
    public static final double DEFAULT_HEIGHT = new Units().feetToMeters( 10 );
    private static final double HEIGHT = DEFAULT_HEIGHT;
    private static final double WIDTH = 4;

    public Pool() {
    }

    public double getHeight() {
        return HEIGHT;
    }

    public double getWidth() {
        return WIDTH;
    }

    public Shape getShape() {
        return new Rectangle2D.Double( -WIDTH / 2, -HEIGHT, WIDTH, HEIGHT );
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
        return -getHeight();
    }

    public Point2D getTopRight() {
        return new Point2D.Double( getMaxX(), getMaxY() );
    }
}