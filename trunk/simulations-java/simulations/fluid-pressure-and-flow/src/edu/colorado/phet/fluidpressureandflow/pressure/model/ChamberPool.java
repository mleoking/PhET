// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.model;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.CompositeBooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

/**
 * Pool with separate chambers where the fluid can flow.  Weights can be added to either side.
 *
 * @author Sam Reid
 */
public class ChamberPool implements IPool {

    //Units in meters, describes the leftmost chamber and is used to create both
    public final double centerAtLeftChamberOpening = -2.9;
    private final double separation = 3.9;//Between centers

    public final double height = 3;
    public final Property<Double> flowRatePercentage = new Property<Double>( 0.0 );
    public final ObservableProperty<Boolean> faucetEnabled;

    private final CompositeProperty<Shape> waterShape;
    public final Property<Double> waterVolume = new Property<Double>( 2.0 );

    private final double passageHeight = 0.5;
    private double rightOpeningWidth = 3;

    public final Property<ObservableList<Mass>> masses = new Property<ObservableList<Mass>>( new ObservableList<Mass>() {{
        double massOffset = -4.9;
        add( new Mass( new Rectangle2D.Double( massOffset + 0, 0, passageHeight, passageHeight ), false, 0.0, 2 ) );
        add( new Mass( new Rectangle2D.Double( massOffset + passageHeight, 0, passageHeight, passageHeight / 2 ), false, 0.0, 1 ) );
        add( new Mass( new Rectangle2D.Double( massOffset + passageHeight * 2, 0, passageHeight, passageHeight / 2 ), false, 0.0, 1 ) );
    }} );
    private final Property<Double> gravity;

    public ChamberPool( Property<Double> gravity ) {
        this.gravity = gravity;

        //just keep the bottom part that is occupied by water
        this.waterShape = new CompositeProperty<Shape>( new Function0<Shape>() {
            @Override public Shape apply() {
                return createWaterShape( getContainerShape() );
            }
        }, waterVolume );
        faucetEnabled = new CompositeBooleanProperty( new Function0<Boolean>() {
            @Override public Boolean apply() {
                return getWaterHeight() < height;
            }
        }, waterVolume );
    }

    private Shape createWaterShape( Shape shape ) {
        final Shape containerShape = getContainerShape();
        double height = getWaterHeight();
        Rectangle2D whole = containerShape.getBounds2D();

        final Rectangle2D part = new Rectangle2D.Double( whole.getX(), whole.getY(), whole.getWidth(), height );
        return new Area( shape ) {{
            intersect( new Area( part ) );
        }};
    }

    //Find out how high the water will rise given a volume of water.
    //This is tricky because of the connecting passage which has nonzero volume
    //It is used to subtract out the part of the water that is not
    public double getWaterHeight() {
        return Math.min( waterVolume.get(), height );
    }

    @Override public Shape getContainerShape() {
        return new Area( leftOpening() ) {{
            add( new Area( leftChamber() ) );
            add( new Area( horizontalPassage() ) );
            add( new Area( rightChamber() ) );
            add( new Area( rightOpening() ) );
        }};
    }

    private Shape rightOpening() {
        return new Rectangle2D.Double( rightChamber().getBounds2D().getCenterX() - rightOpeningWidth / 2, -height / 2, rightOpeningWidth, height / 2 );
    }

    private Shape leftOpening() {
        return new Rectangle2D.Double( leftChamber().getBounds2D().getCenterX() - passageHeight / 2, -height, passageHeight, height );
    }

    private Shape horizontalPassage() {
        return new Rectangle2D.Double( centerAtLeftChamberOpening, -height + passageHeight, separation, passageHeight );
    }

    private Shape leftChamber() {
        return new Rectangle2D.Double( -4.5, -3, 3, 1.5 );
    }

    private Shape rightChamber() {
        return new Rectangle2D.Double( 0, -3, 1.5, 1.5 );
    }

    @Override public double getHeight() {
        return getContainerShape().getBounds2D().getHeight();
    }

    @Override public ObservableProperty<Shape> getWaterShape() {
        return waterShape;
    }

    @Override public double getPressure( final double x, final double y, final boolean atmosphere, final double standardAirPressure, final double liquidDensity, final double gravity ) {
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
        waterVolume.set( waterVolume.get() + flowRatePercentage.get() * dt );
        masses.set( updateMasses( masses.get(), dt ) );
    }

    @Override public void addPressureChangeObserver( final SimpleObserver updatePressure ) {
        waterShape.addObserver( updatePressure );
        masses.addObserver( updatePressure );
    }

    @Override public Point2D clampSensorPosition( final Point2D pt ) {
        return pt;
    }

    @Override public boolean isAbbreviatedUnits( final ImmutableVector2D sensorPosition, final double value ) {
        return getWaterShape().get().contains( sensorPosition.getX(), sensorPosition.getY() );
    }

    private ObservableList<Mass> updateMasses( final ObservableList<Mass> masses, final double dt ) {
        return masses.map( new Function1<Mass, Mass>() {
            @Override public Mass apply( final Mass mass ) {
                if ( mass.getMinY() > 0.0 && !mass.dragging ) {
                    double force = -mass.mass * gravity.get();
                    double acceleration = force / mass.mass;
                    double newVelocity = mass.velocity + acceleration * dt;
                    double newPosition = mass.getMinY() + newVelocity * dt;
                    return mass.withMinY( Math.max( newPosition, 0.0 ) ).withVelocity( newVelocity );
                }
                else {
                    return mass;
                }
            }
        } );
    }

    public void reset() {
        flowRatePercentage.reset();
        waterVolume.reset();
    }

    public Shape getLeftOpeningWaterShape() {
        //Assumes non-empty
        return createWaterShape( leftOpening() );
    }
}