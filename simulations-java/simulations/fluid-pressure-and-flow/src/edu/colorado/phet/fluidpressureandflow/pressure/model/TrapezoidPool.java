// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.model;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * @author Sam Reid
 */
public class TrapezoidPool implements IPool {
    @Override public Shape getShape() {
        return new Rectangle2D.Double( -1, -1, 1, 1 );
    }

    @Override public double getHeight() {
        return getShape().getBounds2D().getHeight();
    }
}