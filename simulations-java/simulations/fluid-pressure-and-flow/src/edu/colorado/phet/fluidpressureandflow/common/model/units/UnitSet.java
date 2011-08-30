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
    private static final Unit FLOW_METRIC = new LinearUnit( "flow", "L / (m<sup>2</sup>s)", 1E3, new DecimalFormat( "0.00" ) );//m3 m-2 s-1
    private static final Unit FLOW_ENGLISH = new LinearUnit( "flow", "ft<sup>3</sup> / (ft<sup>2</sup> s)", FEET_PER_METER, new DecimalFormat( "0.00" ) );

    //To convert ft3 / ft2 / s into m3 / m2 / s
    //Same as ft/s -> m/s, same as converting feet to meters

    //Common unit sets
    public static final UnitSet ATMOSPHERES = new UnitSet( ATMOSPHERE, FEET_PER_SECOND, FEET, FLUID_DENSITY_ENGLISH, FLOW_ENGLISH );//English units but with atmospheres for pressure instead of psi
    public static final UnitSet ENGLISH = new UnitSet( PSI, FEET_PER_SECOND, FEET, FLUID_DENSITY_ENGLISH, FLOW_ENGLISH );
    public static final UnitSet METRIC = new UnitSet( PASCAL, METERS_PER_SECOND, METERS, FLUID_DENSITY_METRIC, FLOW_METRIC );

    public final Unit pressure;
    public final Unit velocity;
    public final Unit distance;
    public final Unit density;
    public final Unit flow;

    public UnitSet( Unit pressure, Unit velocity, Unit distance, Unit density, Unit flow ) {
        this.pressure = pressure;
        this.velocity = velocity;
        this.distance = distance;
        this.density = density;
        this.flow = flow;
    }
}