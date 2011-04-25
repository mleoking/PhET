// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.model.ResetModel;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.VelocitySensor;
import edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet;

/**
 * Main model class for FluidPressureAndFlow.  Units for this sim are by default in MKS, and conversions through class
 * Units are used to convert to different units systems.
 *
 * @author Sam Reid
 */
public class FluidPressureAndFlowModel implements PressureSensor.Context, ResetModel {
    private static final double EARTH_AIR_PRESSURE = 101325;//Pascals is MKS, see http://en.wikipedia.org/wiki/Atmospheric_pressure
    private static final double EARTH_AIR_PRESSURE_AT_500_FT = 99490;

    public static final double GASOLINE_DENSITY = 700;
    public static final double WATER_DENSITY = 1000;
    public static final double HONEY_DENSITY = 1420;
    public static final Double EARTH_GRAVITY = 9.8;

    private final ConstantDtClock clock = new ConstantDtClock( 30 );
    private final ArrayList<PressureSensor> pressureSensors = new ArrayList<PressureSensor>();
    private final ArrayList<VelocitySensor> velocitySensors = new ArrayList<VelocitySensor>();
    public final Property<Double> gravity = new Property<Double>( EARTH_GRAVITY );
    public final Property<Double> standardAirPressure = new Property<Double>( EARTH_AIR_PRESSURE );//air pressure at y=0
    public final Property<Double> liquidDensity = new Property<Double>( 1000.0 );//SI
    public final Property<UnitSet> units;//Set of units for the sim (distance, velocity, pressure).

    private final Function.LinearFunction pressureFunction = new Function.LinearFunction( 0, 500, standardAirPressure.getValue(), EARTH_AIR_PRESSURE_AT_500_FT );//see http://www.engineeringtoolbox.com/air-altitude-pressure-d_462.html
    private ArrayList<VoidFunction0> resetListeners = new ArrayList<VoidFunction0>();
    public final Property<Double> simulationTimeStep = new Property<Double>( clock.getDt() );//Property<Double> that indicates (and can be used to set) the clock's dt time step (in seconds)

    //Construct a FluidPressureAndFlow model with the specified set of units (such as metric)
    public FluidPressureAndFlowModel( UnitSet unitSet ) {
        units = new Property<UnitSet>( unitSet );
        //Wire up the clock to the Property<Double> that identifies the dt value
        simulationTimeStep.addObserver( new VoidFunction1<Double>() {
            public void apply( Double dt ) {
                clock.setDt( dt );
            }
        } );
        clock.addConstantDtClockListener( new ConstantDtClock.ConstantDtClockAdapter() {
            @Override public void dtChanged( ConstantDtClock.ConstantDtClockEvent event ) {
                simulationTimeStep.setValue( event.getClock().getDt() );
            }
        } );
    }

    public void addPressureSensor( PressureSensor sensor ) {
        pressureSensors.add( sensor );
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

    /*
     * Add a listener to identify when the fluid has changed, for purposes of updating pressure sensors.
     */
    public void addFluidChangeObserver( SimpleObserver updatePressure ) {
        gravity.addObserver( updatePressure );
        standardAirPressure.addObserver( updatePressure );
        liquidDensity.addObserver( updatePressure );
    }

    public Function.LinearFunction getPressureFunction() {
        return pressureFunction;
    }

    public double getStandardAirPressure() {
        return standardAirPressure.getValue();
    }

    public PressureSensor[] getPressureSensors() {
        return pressureSensors.toArray( new PressureSensor[pressureSensors.size()] );
    }

    public VelocitySensor[] getVelocitySensors() {
        return velocitySensors.toArray( new VelocitySensor[velocitySensors.size()] );
    }

    public void reset() {
        gravity.reset();
        standardAirPressure.reset();
        units.reset();
        liquidDensity.reset();
        for ( VelocitySensor velocitySensor : velocitySensors ) {
            velocitySensor.reset();
        }
        for ( PressureSensor pressureSensor : pressureSensors ) {
            pressureSensor.reset();
        }
        clock.resetSimulationTime();
        for ( VoidFunction0 resetListener : resetListeners ) {
            resetListener.apply();
        }
        clock.start();
    }

    public void addResetListener( VoidFunction0 resetAction ) {
        resetListeners.add( resetAction );
    }
}