package edu.colorado.phet.fluidpressureandflow.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

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
    private Property<Double> standardAirPressure = new Property<Double>( EARTH_AIR_PRESSURE );//air pressure at y=0
    private Function.LinearFunction pressureFunction = new Function.LinearFunction( 0, 500, standardAirPressure.getValue(), EARTH_AIR_PRESSURE_AT_500_FT );//see http://www.engineeringtoolbox.com/air-altitude-pressure-d_462.html
    private ConstantDtClock clock = new ConstantDtClock( 30 );
    public static final Double EARTH_GRAVITY = 9.8;
    public static final Double MOON_GRAVITY = EARTH_GRAVITY / 6.0;
    public static final Double JUPITER_GRAVITY = EARTH_GRAVITY * 2.364;
    public final Property<Units.PressureUnit> pressureUnitProperty = new Property<Units.PressureUnit>( Units.PressureUnit.ATMOSPHERE );
    private ArrayList<PressureSensor> pressureSensors = new ArrayList<PressureSensor>();

    public void addPressureSensor( PressureSensor sensor ) {
        pressureSensors.add( sensor );
    }

    public ConstantDtClock getClock() {
        return clock;
    }

    public double getPressure( Point2D position ) {
        return 0;
    }

    public void addFluidChangeObserver( SimpleObserver updatePressure ) {
        gravityProperty.addObserver( updatePressure );
        standardAirPressure.addObserver( updatePressure );
    }

    public Property<Double> getGravityProperty() {
        return gravityProperty;
    }

    public Property<Units.PressureUnit> getPressureUnitProperty() {
        return pressureUnitProperty;
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
}
