// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.model;

import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.common.model.PressureSensor;

import static edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet.ATMOSPHERES;
import static java.lang.Math.abs;

/**
 * Model for the "pressure" tab
 *
 * @author Sam Reid
 */
public class FluidPressureModel extends FluidPressureAndFlowModel {

    //Pool within which the user can measure the pressure
    public final Pool pool = new Pool();

    public FluidPressureModel() {
        super( ATMOSPHERES );

        //Show pressure partly submerged in the water, but at the top of the water
        addPressureSensor( new PressureSensor( this, 0, 0 ) );

        //Show second pressure sensor at a y location that yields a different pressure value
        addPressureSensor( new PressureSensor( this, -4, 2 ) );
    }

    //Gets the pressure at the specified location.
    @Override public double getPressure( double x, double y ) {
        if ( y < 0 ) {
            return getStandardAirPressure() + liquidDensity.get() * gravity.get() * abs( -y );
        }
        else {
            return super.getPressure( x, y );
        }
    }
}