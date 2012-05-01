// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.model;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.CompositeBooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.fluidpressureandflow.pressure.view.FaucetPool;

import static java.lang.Math.min;

/**
 * A simple square pool, starts half full, copied from Trapezoidal pool.
 *
 * @author Sam Reid
 */
public class SquarePool implements FaucetPool {

    //Units in meters
    public final double height = 3;
    public final Property<Double> inputFlowRatePercentage = new Property<Double>( 0.0 );
    public final ObservableProperty<Boolean> inputFaucetEnabled;

    private final CompositeProperty<Shape> waterShape;
    public final Property<Double> waterVolume = new Property<Double>( height / 2 );
    public final ObservableProperty<Boolean> drainFaucetEnabled;
    public final Property<Double> drainFlowRate = new Property<Double>( 0.0 );

    public SquarePool() {
        this.waterShape = new CompositeProperty<Shape>( new Function0<Shape>() {
            public Shape apply() {
                final Shape containerShape = getContainerShape();
                double height = getWaterHeight();
                Rectangle2D whole = containerShape.getBounds2D();

                //just keep the bottom part that is occupied by water
                final Rectangle2D part = new Rectangle2D.Double( whole.getX(), whole.getY(), whole.getWidth(), height );
                return new Area( containerShape ) {{
                    intersect( new Area( part ) );
                }};
            }
        }, waterVolume );
        inputFaucetEnabled = new CompositeBooleanProperty( new Function0<Boolean>() {
            public Boolean apply() {
                return getWaterHeight() < height;
            }
        }, waterVolume );
        drainFaucetEnabled = new CompositeBooleanProperty( new Function0<Boolean>() {
            public Boolean apply() {
                return waterVolume.get() > 0.0;
            }
        }, waterVolume );
    }

    //Find out how high the water will rise given a volume of water.
    //This is tricky because of the connecting passage which has nonzero volume
    //It is used to subtract out the part of the water that is not
    public double getWaterHeight() { return min( waterVolume.get(), height ); }

    public Shape getContainerShape() { return new Rectangle2D.Double( -3.2, -3, 4, 3 ); }

    public double getHeight() { return getContainerShape().getBounds2D().getHeight(); }

    public ObservableProperty<Shape> getWaterShape() { return waterShape; }

    public double getPressure( final double x, final double y, final boolean atmosphere, final double standardAirPressure, final double liquidDensity, final double gravity ) {
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
                final double waterHeight = getWaterHeight();
                double y0 = -height + waterHeight;
                double p0 = Pool.getPressureAboveGround( y0, atmosphere, standardAirPressure, gravity );
                double distanceBelowWater = Math.abs( -y + y0 );
                return p0 + liquidDensity * gravity * distanceBelowWater;
            }
        }
    }

    public void stepInTime( final double dt ) {
        waterVolume.set( MathUtil.clamp( 0, waterVolume.get() + inputFlowRatePercentage.get() * dt - drainFlowRate.get() * dt, height ) );
    }

    public void addPressureChangeObserver( final SimpleObserver updatePressure ) { waterVolume.addObserver( updatePressure ); }

    public Point2D clampSensorPosition( final Point2D pt ) { return pt; }

    public boolean isAbbreviatedUnits( final ImmutableVector2D sensorPosition, final double value ) {
        return getWaterShape().get().contains( sensorPosition.getX(), sensorPosition.getY() );
    }

    @Override public ArrayList<Pair<Double, Double>> getGrassSegments() {
        return new ArrayList<Pair<Double, Double>>() {{
            add( new Pair<Double, Double>( getContainerShape().getBounds2D().getMinX() - 100, getContainerShape().getBounds2D().getMinX() ) );
            add( new Pair<Double, Double>( getContainerShape().getBounds2D().getMaxX(), getContainerShape().getBounds2D().getMaxX() + 100 ) );
        }};
    }

    @Override public ArrayList<ArrayList<ImmutableVector2D>> getEdges() {
        return new ArrayList<ArrayList<ImmutableVector2D>>() {{
            add( new ArrayList<ImmutableVector2D>() {{
                add( new ImmutableVector2D( getContainerShape().getBounds2D().getMinX(), getContainerShape().getBounds2D().getMaxY() ) );
                add( new ImmutableVector2D( getContainerShape().getBounds2D().getMinX(), getContainerShape().getBounds2D().getMinY() ) );
                add( new ImmutableVector2D( getContainerShape().getBounds2D().getMaxX(), getContainerShape().getBounds2D().getMinY() ) );
                add( new ImmutableVector2D( getContainerShape().getBounds2D().getMaxX(), getContainerShape().getBounds2D().getMaxY() ) );
            }} );
        }};
    }

    public void reset() {
        inputFlowRatePercentage.reset();
        waterVolume.reset();
    }

    public double getMinX() { return getContainerShape().getBounds2D().getMinX(); }

    public double getMinY() { return getContainerShape().getBounds2D().getMinY(); }

    public double getMaxX() { return getContainerShape().getBounds2D().getMaxX(); }

    public double getMaxY() { return getContainerShape().getBounds2D().getMaxY(); }

    public double getWidth() { return getContainerShape().getBounds2D().getHeight(); }

    public Point2D getTopRight() { return new Point2D.Double( getMaxX(), getMaxY() ); }

    @Override public double getWaterOutputCenterX() { return getWaterShape().get().getBounds2D().getCenterX(); }

    @Override public ObservableProperty<Double> getWaterVolume() {return waterVolume;}

    @Override public ObservableProperty<Boolean> getDrainFaucetEnabled() { return drainFaucetEnabled; }

    @Override public Property<Double> getDrainFlowRate() { return drainFlowRate; }

    @Override public Property<Double> getInputFlowRatePercentage() { return inputFlowRatePercentage; }

    @Override public ObservableProperty<Boolean> getInputFaucetEnabled() { return inputFaucetEnabled; }

    @Override public double getInputFaucetX() { return -3 + 0.2; }
}