package edu.colorado.phet.fluidpressureandflow.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * @author Sam Reid
 */
public class FluidPressureAndFlowModel {
    private static final double EARTH_AIR_PRESSURE = 101325;//Pascals is MKS, see http://en.wikipedia.org/wiki/Atmospheric_pressure
    private static final double EARTH_AIR_PRESSURE_AT_500_FT = 99490;

    public static double GASOLINE_DENSITY = 700;
    public static double WATER_DENSITY = 1000;
    public static double HONEY_DENSITY = 1420;

    private Property<Double> gravityProperty = new Property<Double>( EARTH_GRAVITY );
    private Property<Double> standardAirPressure = new Property<Double>( EARTH_AIR_PRESSURE );
    private Function.LinearFunction pressure = new Function.LinearFunction( 0, 500, standardAirPressure.getValue(), EARTH_AIR_PRESSURE_AT_500_FT );//see http://www.engineeringtoolbox.com/air-altitude-pressure-d_462.html
    private ConstantDtClock clock = new ConstantDtClock( 30 );
    private Pool pool = new Pool();
    private PressureSensor pressureSensor0 = new PressureSensor( this, 0, 0 );
    private PressureSensor pressureSensor1 = new PressureSensor( this, -4, 1 );
    public static final Double EARTH_GRAVITY = 9.8;
    public static final Double MOON_GRAVITY = EARTH_GRAVITY / 6.0;
    public static final Double JUPITER_GRAVITY = EARTH_GRAVITY * 2.364;
    public final Property<Units.PressureUnit> pressureUnitProperty = new Property<Units.PressureUnit>( Units.PressureUnit.ATMOSPHERE );

    public ConstantDtClock getClock() {
        return clock;
    }

    public PressureSensor getPressureSensor0() {
        return pressureSensor0;
    }

    public PressureSensor getPressureSensor1() {
        return pressureSensor1;
    }

    public double getPressure( Point2D position ) {
        if ( position.getY() > 0 ) {
            return pressure.evaluate( position.getY() );
        }
        else {
            return standardAirPressure.getValue() + pool.getLiquidDensity() * gravityProperty.getValue() * Math.abs( 0 - position.getY() );
        }
    }

    public Pool getPool() {
        return pool;
    }

    public void addFluidChangeObserver( SimpleObserver updatePressure ) {
        pool.addDensityListener( updatePressure );
        gravityProperty.addObserver( updatePressure );
        standardAirPressure.addObserver( updatePressure );
    }

    public Property<Double> getGravityProperty() {
        return gravityProperty;
    }

    public Property<Units.PressureUnit> getPressureUnitProperty() {
        return pressureUnitProperty;
    }
}
