// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.model.units;

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
    public static final UnitSet ATMOSPHERES = new UnitSet( ATMOSPHERE, FEET_PER_SECOND, FEET );//English units but with atmospheres for pressure instead of psi
    public static final UnitSet ENGLISH = new UnitSet( PSI, FEET_PER_SECOND, FEET );
    public static final UnitSet METRIC = new UnitSet( PASCAL, METERS_PER_SECOND, METERS );

    public UnitSet( Unit pressure, Unit velocity, Unit distance ) {
        this.pressure = pressure;
        this.velocity = velocity;
        this.distance = distance;
    }
}
