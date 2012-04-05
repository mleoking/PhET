// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.model;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;
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
    public static final double WIDTH = 4;
    private final Property<Shape> waterShape = new Property<Shape>( getContainerShape() );

    public Pool() {
    }

    public double getHeight() {
        return HEIGHT;
    }

    @Override public Property<Shape> getWaterShape() {
        return waterShape;
    }

    @Override public double getPressure( final double x, final double y, boolean atmosphere, double standardAirPressure, double liquidDensity, double gravity ) {
        if ( y >= 0 ) {
            return Pool.getPressureAboveGround( y, atmosphere, standardAirPressure, gravity );
        }
        else {
            //Under the ground
            final Shape containerShape = getContainerShape();
            final Shape waterShape = getWaterShape().get();

            //In the ground, return 0.0 (no reading)
            if ( !containerShape.contains( x, y ) ) {
                return 0.0;
            }

            //in the container but not the water
            else if ( containerShape.contains( x, y ) && !waterShape.contains( x, y ) ) {
                return Pool.getPressureAboveGround( y, atmosphere, standardAirPressure, gravity );
            }

            //In the water, but the container may not be completely full
            else {// if ( containerShape.contains( x, y ) && waterShape.contains( x, y ) ) {

                //Y value at the top of the water to compute the air pressure there
                final double waterHeight = HEIGHT;
                double y0 = -HEIGHT + waterHeight;
                double p0 = Pool.getPressureAboveGround( y0, atmosphere, standardAirPressure, gravity );
                double distanceBelowWater = Math.abs( -y + y0 );
                return p0 + liquidDensity * gravity * distanceBelowWater;
            }
        }
    }

    @Override public void stepInTime( final double dt ) {
    }

    @Override public void addPressureChangeObserver( final SimpleObserver updatePressure ) {
    }

    @Override public Point2D clampSensorPosition( Point2D point2D ) {
        point2D = new Point2D.Double( point2D.getX(), Math.max( point2D.getY(), getMinY() ) );
        if ( point2D.getY() < 0 ) {
            point2D.setLocation( MathUtil.clamp( getMinX(), point2D.getX(), getMaxX() ), point2D.getY() );
        }
        return point2D;
    }

    @Override public boolean isAbbreviatedUnits( final ImmutableVector2D sensorPosition, final double value ) {
        return sensorPosition.getY() < 0;
    }

    //TODO: This should be a function of gravity too
    public static double getPressureAboveGround( final double y, final boolean atmosphere, final double standardAirPressure, final double gravity ) {
        LinearFunction f = new LinearFunction( 0, 500, standardAirPressure, FluidPressureAndFlowModel.EARTH_AIR_PRESSURE_AT_500_FT );//see http://www.engineeringtoolbox.com/air-altitude-pressure-d_462.html
        return atmosphere ? f.evaluate( y ) * gravity / 9.8 : 0.0;
    }

    public double getWidth() {
        return WIDTH;
    }

    public Shape getContainerShape() {
        return new Rectangle2D.Double( -WIDTH / 2, -HEIGHT, WIDTH, HEIGHT );
    }

    public double getMinX() {
        return getContainerShape().getBounds2D().getMinX();
    }

    public double getMaxX() {
        return getContainerShape().getBounds2D().getMaxX();
    }

    public double getMaxY() {
        return getContainerShape().getBounds2D().getMaxY();
    }

    public double getMinY() {
        return -getHeight();
    }

    public Point2D getTopRight() {
        return new Point2D.Double( getMaxX(), getMaxY() );
    }
}