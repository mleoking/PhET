// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.model;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.CompositeBooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.common.phetcommon.util.Option.Some;
import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.fluidpressureandflow.pressure.view.FaucetPool;

/**
 * A pair of trapezoidal pools with a connecting passageway.
 *
 * @author Sam Reid
 */
public class TrapezoidPool implements FaucetPool {

    //Units in meters, describes the leftmost chamber and is used to create both
    private final double widthAtTop = 1;
    private final double widthAtBottom = 4;
    public final double centerAtLeftChamberOpening = -2.9;
    private final double separation = 3.9;//Between centers

    private final double yAtTop = 0;
    public final double height = 3;
    public final Property<Double> inputFlowRatePercentage = new Property<Double>( 0.0 );
    public final ObservableProperty<Boolean> inputFaucetEnabled;

    private final CompositeProperty<Shape> waterShape;
    public final Property<Double> waterVolume = new Property<Double>( height / 2 );
    public final ObservableProperty<Boolean> drainFaucetEnabled;
    public final Property<Double> drainFlowRate = new Property<Double>( 0.0 );

    public TrapezoidPool() {
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
    public double getWaterHeight() {
        return Math.min( waterVolume.get(), height );
    }

    public Shape getContainerShape() {
        return new Area( leftChamber() ) {{
            add( new Area( rightChamber() ) );
            add( new Area( passage() ) );
        }};
    }

    public Shape fromLines( final ImmutableVector2D... pts ) {
        if ( pts.length == 0 ) { return new Rectangle2D.Double( 0, 0, 0, 0 ); }
        return new DoubleGeneralPath( pts[0] ) {{
            for ( int i = 1; i < pts.length; i++ ) {
                lineTo( pts[i] );
            }
        }}.getGeneralPath();
    }

    public Rectangle2D.Double passage() {
        final double passageHeight = 0.25;
        return new Rectangle2D.Double( centerAtLeftChamberOpening, -height, separation, passageHeight );
    }

    private Shape leftChamber() {
        final ImmutableVector2D topLeft = topLeftOfLeftChamber();
        final ImmutableVector2D topRight = topLeft.plus( widthAtTop, 0 );
        final ImmutableVector2D bottomLeft = new ImmutableVector2D( centerAtLeftChamberOpening - widthAtBottom / 2, yAtTop - height );
        final ImmutableVector2D bottomRight = bottomLeft.plus( widthAtBottom, 0 );

        return fromLines( topLeft, bottomLeft, bottomRight, topRight );
    }

    private ImmutableVector2D topLeftOfLeftChamber() {return new ImmutableVector2D( centerAtLeftChamberOpening - widthAtTop / 2, yAtTop );}

    private Shape rightChamber() {
        final ImmutableVector2D topLeft = getTopLeftOfRightChamber();
        final ImmutableVector2D topRight = topLeft.plus( widthAtBottom, 0 );
        final ImmutableVector2D bottomLeft = new ImmutableVector2D( centerAtLeftChamberOpening + separation - widthAtTop / 2, yAtTop - height );
        final ImmutableVector2D bottomRight = bottomLeft.plus( widthAtTop, 0 );

        return fromLines( topLeft, bottomLeft, bottomRight, topRight );
    }

    private ImmutableVector2D getTopLeftOfRightChamber() {return new ImmutableVector2D( centerAtLeftChamberOpening + separation - widthAtBottom / 2, yAtTop );}

    public double getHeight() {
        return getContainerShape().getBounds2D().getHeight();
    }

    public ObservableProperty<Shape> getWaterShape() {
        return waterShape;
    }

    public Option<Double> getPressure( final double x, final double y, final boolean atmosphere, final double standardAirPressure, final double liquidDensity, final double gravity ) {
        if ( y >= 0 ) {
            return new Some<Double>( Pool.getPressureAboveGround( y, atmosphere, standardAirPressure, gravity ) );
        }
        else {
            //Under the ground
            final Shape containerShape = getContainerShape();
            final Shape waterShape = getWaterShape().get();

            //In the ground, return 0.0 (no reading)
            if ( !containerShape.contains( x, y ) ) {
                return new None<Double>();
            }

            //in the container but not the water
            else if ( containerShape.contains( x, y ) && !waterShape.contains( x, y ) ) {
                return new Some<Double>( Pool.getPressureAboveGround( y, atmosphere, standardAirPressure, gravity ) );
            }

            //In the water, but the container may not be completely full
            else {// if ( containerShape.contains( x, y ) && waterShape.contains( x, y ) ) {

                //Y value at the top of the water to compute the air pressure there
                final double waterHeight = getWaterHeight();
                double y0 = -height + waterHeight;
                double p0 = Pool.getPressureAboveGround( y0, atmosphere, standardAirPressure, gravity );
                double distanceBelowWater = Math.abs( -y + y0 );
                return new Some<Double>( p0 + liquidDensity * gravity * distanceBelowWater );
            }
        }
    }

    public void stepInTime( final double dt ) {
        waterVolume.set( MathUtil.clamp( 0, waterVolume.get() + inputFlowRatePercentage.get() * dt - drainFlowRate.get() * dt, height ) );
    }

    public void addPressureChangeObserver( final SimpleObserver updatePressure ) {
        waterVolume.addObserver( updatePressure );
    }

    public boolean isAbbreviatedUnits( final ImmutableVector2D sensorPosition, final double value ) {
        return getWaterShape().get().contains( sensorPosition.getX(), sensorPosition.getY() );
    }

    public ArrayList<Pair<Double, Double>> getGrassSegments() {
        return new ArrayList<Pair<Double, Double>>() {{
            add( new Pair<Double, Double>( topLeftOfLeftChamber().getX() - 100, topLeftOfLeftChamber().getX() ) );
            add( new Pair<Double, Double>( topLeftOfLeftChamber().plus( widthAtTop, 0 ).getX(), getTopLeftOfRightChamber().getX() ) );
            add( new Pair<Double, Double>( getContainerShape().getBounds2D().getMaxX(), getContainerShape().getBounds2D().getMaxX() + 100 ) );
        }};
    }

    public ArrayList<ArrayList<ImmutableVector2D>> getEdges() {

        //Left chamber
        final ImmutableVector2D topLeftA = topLeftOfLeftChamber();
        final ImmutableVector2D topRightA = topLeftA.plus( widthAtTop, 0 );
        final ImmutableVector2D bottomLeftA = new ImmutableVector2D( centerAtLeftChamberOpening - widthAtBottom / 2, yAtTop - height );
        final ImmutableVector2D bottomRightA = bottomLeftA.plus( widthAtBottom, 0 );

        final ImmutableVector2D topLeftB = getTopLeftOfRightChamber();
        final ImmutableVector2D topRightB = topLeftB.plus( widthAtBottom, 0 );
        final ImmutableVector2D bottomLeftB = new ImmutableVector2D( centerAtLeftChamberOpening + separation - widthAtTop / 2, yAtTop - height );
        final ImmutableVector2D bottomRightB = bottomLeftB.plus( widthAtTop, 0 );

        final Point2D.Double leftIntersection = MathUtil.getLineSegmentsIntersection( new Line2D.Double( topRightA.toPoint2D(), bottomRightA.toPoint2D() ),
                                                                                      new Line2D.Double( new Point2D.Double( passage().getX(), passage().getMaxY() ), new Point2D.Double( passage().getMaxX(), passage().getMaxY() ) ) );
        final Point2D.Double rightIntersection = MathUtil.getLineSegmentsIntersection( new Line2D.Double( topLeftB.toPoint2D(), bottomLeftB.toPoint2D() ),
                                                                                       new Line2D.Double( new Point2D.Double( passage().getX(), passage().getMaxY() ), new Point2D.Double( passage().getMaxX(), passage().getMaxY() ) ) );
        return new ArrayList<ArrayList<ImmutableVector2D>>() {{
            add( new ArrayList<ImmutableVector2D>() {{
                addAll( Arrays.asList( topLeftA,
                                       bottomLeftA,
                                       bottomRightB,
                                       topRightB ) );
            }} );
            add( new ArrayList<ImmutableVector2D>() {{
                addAll( Arrays.asList( topRightA,
                                       new ImmutableVector2D( leftIntersection ),
                                       new ImmutableVector2D( rightIntersection ),
                                       topLeftB ) );
            }} );
        }};
    }

    public void reset() {
        inputFlowRatePercentage.reset();
        waterVolume.reset();
    }

    public double getWaterOutputCenterX() { return passage().getBounds2D().getCenterX() + 0.55; }

    public ObservableProperty<Double> getWaterVolume() { return waterVolume; }

    public ObservableProperty<Boolean> getDrainFaucetEnabled() { return drainFaucetEnabled; }

    public Property<Double> getDrainFlowRate() { return drainFlowRate; }

    public Property<Double> getInputFlowRatePercentage() { return inputFlowRatePercentage; }

    public ObservableProperty<Boolean> getInputFaucetEnabled() { return inputFaucetEnabled; }

    public double getInputFaucetX() { return centerAtLeftChamberOpening; }
}