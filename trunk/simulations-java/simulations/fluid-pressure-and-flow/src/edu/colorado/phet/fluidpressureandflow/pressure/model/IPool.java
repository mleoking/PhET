// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.model;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

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

    void stepInTime( double dt );

    void addPressureChangeObserver( SimpleObserver updatePressure );

    Point2D clampSensorPosition( Point2D pt );

    boolean isAbbreviatedUnits( ImmutableVector2D sensorPosition, double value );

    //Get the list of grass segments (x dimension) to show on the ground
    ArrayList<Pair<Double, Double>> getGrassSegments();

    //Get the points making up each continuous edge segment for drawing the cement boundaries of the pool
    ArrayList<ArrayList<ImmutableVector2D>> getEdges();
}