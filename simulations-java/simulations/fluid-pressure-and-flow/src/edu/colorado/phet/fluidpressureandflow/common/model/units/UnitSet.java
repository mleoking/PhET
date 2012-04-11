// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.model.units;

import java.text.DecimalFormat;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.*;
import static edu.colorado.phet.fluidpressureandflow.common.model.units.Units.*;
import static edu.colorado.phet.fluidpressureandflow.common.model.units.Units.PSI;

/**
 * Set of units, such as Metric or English which contains units for pressure, velocity, distance, etc.
 *
 * @author Sam Reid
 */
public class UnitSet {

    //Common units for density
    private static final Unit FLUID_DENSITY_METRIC = new LinearUnit( DENSITY_UNITS_METRIC, 1, new DecimalFormat( "0.00" ) );
    private static final Unit FLUID_DENSITY_ENGLISH = new LinearUnit( DENSITY_UNITS_ENGLISH, 16.0184634, new DecimalFormat( "0.00" ) );

    //units for flow, see volumetric flux: http://en.wikipedia.org/wiki/Flux
    private static final Unit FLUX_METRIC = new LinearUnit( FLUX_UNITS_METRIC, 1E3, new DecimalFormat( "0.00" ) );//m3 m-2 s-1
    private static final Unit FLUX_ENGLISH = new LinearUnit( FLUX_UNITS_ENGLISH, FEET_PER_METER, new DecimalFormat( "0.00" ) );

    //Units for area
    private static final Unit AREA_METRIC = new LinearUnit( AREA_UNITS_METRIC, 1, new DecimalFormat( "0.00" ) );
    private static final Unit AREA_ENGLISH = new LinearUnit( AREA_UNITS_ENGLISH, FEET_PER_METER * FEET_PER_METER, new DecimalFormat( "0.00" ) );

    //Units for rates
    private static final Unit RATE_METRIC = new LinearUnit( RATE_UNITS_METRIC, 1E3, new DecimalFormat( "0.00" ) );
    private static final Unit RATE_ENGLISH = new LinearUnit( RATE_UNITS_ENGLISH, FEET_PER_METER * FEET_PER_METER * FEET_PER_METER, new DecimalFormat( "0.00" ) );

    //Common units for gravity
    private static final Unit GRAVITY_METRIC = new LinearUnit( M_PER_S_PER_S, 1, new DecimalFormat( "0.00" ) );
    private static final Unit GRAVITY_ENGLISH = new LinearUnit( FT_PER_S_PER_S, 32.16 / 9.80665, new DecimalFormat( "0.00" ) ); //http://evaosd.fartoomuch.info/library/units.htm

    //To convert ft3 / ft2 / s into m3 / m2 / s
    //Same as ft/s -> m/s, same as converting feet to meters

    //Common unit sets which the user can select
    public static final UnitSet ATMOSPHERES = new UnitSet( Units.ATMOSPHERE, METERS_PER_SECOND, FEET, FLUID_DENSITY_METRIC, FLUX_METRIC, AREA_METRIC, RATE_METRIC, GRAVITY_METRIC );//Metric units but with atmospheres for pressure instead of psi
    public static final UnitSet ENGLISH = new UnitSet( PSI, FEET_PER_SECOND, FEET, FLUID_DENSITY_ENGLISH, FLUX_ENGLISH, AREA_ENGLISH, RATE_ENGLISH, GRAVITY_ENGLISH );
    public static final UnitSet METRIC = new UnitSet( KILOPASCAL, METERS_PER_SECOND, METERS, FLUID_DENSITY_METRIC, FLUX_METRIC, AREA_METRIC, RATE_METRIC, GRAVITY_METRIC );

    public final Unit pressure;
    public final Unit velocity;
    public final Unit distance;
    public final Unit density;
    public final Unit flux;
    public final Unit area;
    public final Unit flowRate;
    public final Unit gravity;

    public UnitSet( Unit pressure, Unit velocity, Unit distance, Unit density, Unit flux, Unit area, Unit flowRate, Unit gravity ) {
        this.pressure = pressure;
        this.velocity = velocity;
        this.distance = distance;
        this.density = density;
        this.flux = flux;
        this.area = area;
        this.flowRate = flowRate;
        this.gravity = gravity;
    }
}