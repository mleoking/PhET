// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Main model class for FluidPressureAndFlow.  Units for this sim are by default in MKS, and conversions through class
 * Units are used to convert to different units systems.
 *
 * @author Sam Reid
 */
public class FluidPressureAndFlowModel implements PressureSensor.Context {
    private static final double EARTH_AIR_PRESSURE = 101325;//Pascals is MKS, see http://en.wikipedia.org/wiki/Atmospheric_pressure
    private static final double EARTH_AIR_PRESSURE_AT_500_FT = 99490;

    public static final double GASOLINE_DENSITY = 700;
    public static final double WATER_DENSITY = 1000;
    public static final double HONEY_DENSITY = 1420;

    public static final Double EARTH_GRAVITY = 9.8;
    public static final Double MOON_GRAVITY = EARTH_GRAVITY / 6.0; //TODO: not currently used, but should be used in future version
    public static final Double JUPITER_GRAVITY = EARTH_GRAVITY * 2.364;

    private final Property<Double> gravityProperty = new Property<Double>( EARTH_GRAVITY );
    private final Property<Double> standardAirPressure = new Property<Double>( EARTH_AIR_PRESSURE );//air pressure at y=0
    private final Function.LinearFunction pressureFunction = new Function.LinearFunction( 0, 500, standardAirPressure.getValue(), EARTH_AIR_PRESSURE_AT_500_FT );//see http://www.engineeringtoolbox.com/air-altitude-pressure-d_462.html
    private final ConstantDtClock clock = new ConstantDtClock( 30 );
    private final Property<Units.Unit> pressureUnitProperty = new Property<Units.Unit>( Units.ATMOSPHERE );
    private final Property<Units.Unit> velocityUnitProperty = new Property<Units.Unit>( Units.METERS_PER_SECOND );
    private final Property<Units.Unit> distanceUnitProperty = new Property<Units.Unit>( Units.FEET );
    private final ArrayList<PressureSensor> pressureSensors = new ArrayList<PressureSensor>();
    private final ArrayList<Balloon> balloons = new ArrayList<Balloon>();
    private final ArrayList<VelocitySensor> velocitySensors = new ArrayList<VelocitySensor>();
    private final Property<Double> liquidDensityProperty = new Property<Double>( 1000.0 );//SI

    public void addPressureSensor( PressureSensor sensor ) {
        pressureSensors.add( sensor );
    }

    public void addBalloon( Balloon balloon ) {
        balloons.add( balloon );
    }

    public void addVelocitySensor( VelocitySensor sensor ) {
        velocitySensors.add( sensor );
    }

    public ConstantDtClock getClock() {
        return clock;
    }

    /**
     * Gets the pressure the specified location, overriden in subclasses to account for other water structures, etc.
     * The implementation here just returns the air pressure, or Double.NaN if the sample point is under y=0.
     */
    public double getPressure( double x, double y ) {
        if ( y >= 0 ) {
            return getPressureFunction().evaluate( y );
        }
        else {
            return Double.NaN;
        }
    }

    public double getPressure( Point2D position ) {
        return getPressure( position.getX(), position.getY() );
    }

    /*
     * Add a listener to identify when the fluid has changed, for purposes of updating pressure sensors.
     */
    public void addFluidChangeObserver( SimpleObserver updatePressure ) {
        gravityProperty.addObserver( updatePressure );
        standardAirPressure.addObserver( updatePressure );
        liquidDensityProperty.addObserver( updatePressure );
    }

    public Property<Double> getGravityProperty() {
        return gravityProperty;
    }

    public Property<Units.Unit> getPressureUnitProperty() {
        return pressureUnitProperty;
    }

    public Property<Units.Unit> getVelocityUnitProperty() {
        return velocityUnitProperty;
    }

    public Function.LinearFunction getPressureFunction() {
        return pressureFunction;
    }

    public double getStandardAirPressure() {
        return standardAirPressure.getValue();
    }

    public double getGravity() {
        return gravityProperty.getValue();
    }

    public PressureSensor[] getPressureSensors() {
        return pressureSensors.toArray( new PressureSensor[0] );
    }

    public Balloon[] getBalloons() {
        return balloons.toArray( new Balloon[0] );
    }

    public VelocitySensor[] getVelocitySensors() {
        return velocitySensors.toArray( new VelocitySensor[0] );
    }

    public Property<Units.Unit> getDistanceUnitProperty() {
        return distanceUnitProperty;
    }

    public void setLiquidDensity( double value ) {
        liquidDensityProperty.setValue( value );
    }

    public void addDensityListener( SimpleObserver simpleObserver ) {
        liquidDensityProperty.addObserver( simpleObserver );
    }

    public double getLiquidDensity() {
        return liquidDensityProperty.getValue();
    }

    public Property<Double> getLiquidDensityProperty() {
        return liquidDensityProperty;
    }

    public void reset() {
        gravityProperty.reset();
        standardAirPressure.reset();
        pressureUnitProperty.reset();
        velocityUnitProperty.reset();
        distanceUnitProperty.reset();
        liquidDensityProperty.reset();
        for ( VelocitySensor velocitySensor : velocitySensors ) {
            velocitySensor.reset();
        }
        for ( PressureSensor pressureSensor : pressureSensors ) {
            pressureSensor.reset();
        }
        for ( Balloon balloon : balloons ) {
            balloon.reset();
        }
        clock.resetSimulationTime();
        clock.start();
    }
}
