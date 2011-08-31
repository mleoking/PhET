// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.model;

import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.common.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet;

/**
 * @author Sam Reid
 */
public class FluidPressureModel extends FluidPressureAndFlowModel {
    private final Pool pool = new Pool();

    public FluidPressureModel() {
        super( UnitSet.ATMOSPHERES );

        //Show pressure partly submerged in the water, but at the top of the water
        addPressureSensor( new PressureSensor( this, 0, 0 ) );

        //Show second pressure sensor at a y location that yields a different pressure value
        addPressureSensor( new PressureSensor( this, -4, 2 ) );
    }

    @Override
    public double getPressure( double x, double y ) {
        if ( y < 0 ) {
            return getStandardAirPressure() + liquidDensity.get() * gravity.get() * Math.abs( 0 - y );
        }
        else {
            return super.getPressure( x, y );
        }
    }

    public Pool getPool() {
        return pool;
    }
}