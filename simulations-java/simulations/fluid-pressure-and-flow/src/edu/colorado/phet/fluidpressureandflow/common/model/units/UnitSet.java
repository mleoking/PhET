// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.model.units;

import java.text.DecimalFormat;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.KG_PER_M_3;
import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.LB_PER_FT_3;
import static edu.colorado.phet.fluidpressureandflow.common.model.units.Units.*;

/**
 * Set of units, such as Metric or English.
 *
 * @author Sam Reid
 */
public class UnitSet {
    //Common units for density
    private static final Unit FLUID_DENSITY_METRIC = new LinearUnit( "Fluid Density(metric)", KG_PER_M_3, 1, new DecimalFormat( "0.00" ) );
    private static final Unit FLUID_DENSITY_ENGLISH = new LinearUnit( "Fluid Density(english)", LB_PER_FT_3, 16.0184634, new DecimalFormat( "0.00" ) );

    //units for flow, see volumetric flux: http://en.wikipedia.org/wiki/Flux
    //TODO i18n
    private static final Unit FLUX_METRIC = new LinearUnit( "flow", "L / (m<sup>2</sup>s)", 1E3, new DecimalFormat( "0.00" ) );//m3 m-2 s-1
    private static final Unit FLUX_ENGLISH = new LinearUnit( "flow", "ft<sup>3</sup> / (ft<sup>2</sup> s)", FEET_PER_METER, new DecimalFormat( "0.00" ) );

    //TODO: nonlinear scale for ft^2, right?
    private static final Unit AREA_ENGLISH = new LinearUnit( "area", "ft ^ 2", FEET_PER_METER, new DecimalFormat( "0.00" ) );
    private static final Unit AREA_METRIC = new LinearUnit( "area", "m ^ 2", 1, new DecimalFormat( "0.00" ) );

    //TODO: nonlinear scale, right?
    private static final Unit RATE_ENGLISH = new LinearUnit( "rate", "ft^3/s", FEET_PER_METER, new DecimalFormat( "0.00" ) );
    private static final Unit RATE_METRIC = new LinearUnit( "rate", "L / s", 1E3, new DecimalFormat( "0.00" ) );

    //To convert ft3 / ft2 / s into m3 / m2 / s
    //Same as ft/s -> m/s, same as converting feet to meters

    //Common unit sets
    public static final UnitSet ATMOSPHERES = new UnitSet( ATMOSPHERE, FEET_PER_SECOND, FEET, FLUID_DENSITY_ENGLISH, FLUX_ENGLISH, AREA_ENGLISH, RATE_ENGLISH );//English units but with atmospheres for pressure instead of psi
    public static final UnitSet ENGLISH = new UnitSet( PSI, FEET_PER_SECOND, FEET, FLUID_DENSITY_ENGLISH, FLUX_ENGLISH, AREA_ENGLISH, RATE_ENGLISH );
    public static final UnitSet METRIC = new UnitSet( PASCAL, METERS_PER_SECOND, METERS, FLUID_DENSITY_METRIC, FLUX_METRIC, AREA_METRIC, RATE_METRIC );

    public final Unit pressure;
    public final Unit velocity;
    public final Unit distance;
    public final Unit density;
    public final Unit flux;
    public final Unit area;
    public final Unit rate;

    public UnitSet( Unit pressure, Unit velocity, Unit distance, Unit density, Unit flux, Unit area, Unit rate ) {
        this.pressure = pressure;
        this.velocity = velocity;
        this.distance = distance;
        this.density = density;
        this.flux = flux;
        this.area = area;
        this.rate = rate;
    }
}