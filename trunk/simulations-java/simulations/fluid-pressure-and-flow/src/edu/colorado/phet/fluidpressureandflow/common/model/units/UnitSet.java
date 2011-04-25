// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.model.units;

import java.text.DecimalFormat;

import static edu.colorado.phet.fluidpressureandflow.common.FPAFStrings.KG_PER_M_3;
import static edu.colorado.phet.fluidpressureandflow.common.FPAFStrings.LB_PER_FT_3;
import static edu.colorado.phet.fluidpressureandflow.common.model.units.Units.*;

/**
 * Set of units, such as Metric or English.
 *
 * @author Sam Reid
 */
public class UnitSet {
    public final Unit pressure;
    public final Unit velocity;
    public final Unit distance;
    public final Unit density;
    private static final Unit FLUID_DENSITY_METRIC = new LinearUnit( "Fluid Density(metric)", KG_PER_M_3, 1, new DecimalFormat( "0.00" ) );
    private static final Unit FLUID_DENSITY_ENGLISH = new LinearUnit( "Fluid Density(english)", LB_PER_FT_3, 16.0184634, new DecimalFormat( "0.00" ) );
    public static final UnitSet ATMOSPHERES = new UnitSet( ATMOSPHERE, FEET_PER_SECOND, FEET, FLUID_DENSITY_ENGLISH );//English units but with atmospheres for pressure instead of psi
    public static final UnitSet ENGLISH = new UnitSet( PSI, FEET_PER_SECOND, FEET, FLUID_DENSITY_ENGLISH );
    public static final UnitSet METRIC = new UnitSet( PASCAL, METERS_PER_SECOND, METERS, FLUID_DENSITY_METRIC );

    public UnitSet( Unit pressure, Unit velocity, Unit distance, Unit density ) {
        this.pressure = pressure;
        this.velocity = velocity;
        this.distance = distance;
        this.density = density;
    }
}