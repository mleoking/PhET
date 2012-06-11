// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.model;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Abstraction for different kinds of pools (square, trapezoidal, chambers)
 *
 * @author Sam Reid
 */
public interface IPool {

    //Shape for the entire container, whether or not there is water in it.
    Shape getContainerShape();

    //Height of the container
    double getHeight();

    //Shape of the water in the container
    ObservableProperty<Shape> getWaterShape();

    //Pressure at a specific point
    Option<Double> getPressure( final double x, final double y, boolean atmosphere, double standardAirPressure, double liquidDensity, double gravity );

    //Update the model by stepping in time.  Only used in the 3rd tab which has a dynamic model
    void stepInTime( double dt );

    //Add an observer that is notified when the pressure might have changed (due to one of its dependencies changing)
    void addPressureChangeObserver( SimpleObserver updatePressure );

    //Make sure the sensor can't be moved out of the range for the pool, only used in the square pool.
    //TODO: is this used anymore?
    Point2D clampSensorPosition( Point2D pt );

    //Use abbreviated units when submerged in some of the scenes
    boolean isAbbreviatedUnits( ImmutableVector2D sensorPosition, double value );

    //Get the list of grass segments (x dimension) to show on the ground
    ArrayList<Pair<Double, Double>> getGrassSegments();

    //Get the points making up each continuous edge segment for drawing the cement boundaries of the pool
    ArrayList<ArrayList<ImmutableVector2D>> getEdges();
}