// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.model;

import java.awt.Shape;

/**
 * Abstraction for different kinds of pools (square, trapezoidal, chambers)
 *
 * @author Sam Reid
 */
public interface IPool {
    Shape getContainerShape();

    double getHeight();

    Shape getWaterShape();
}