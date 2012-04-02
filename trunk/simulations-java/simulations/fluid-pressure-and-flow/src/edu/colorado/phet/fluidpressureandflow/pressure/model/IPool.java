// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.model;

import java.awt.Shape;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;

/**
 * Abstraction for different kinds of pools (square, trapezoidal, chambers)
 *
 * @author Sam Reid
 */
public interface IPool {
    Shape getContainerShape();

    double getHeight();

    ObservableProperty<Shape> getWaterShape();

    double getPressure( final double x, final double y, boolean atmosphere, double standardAirPressure, double liquidDensity, double gravity );
}