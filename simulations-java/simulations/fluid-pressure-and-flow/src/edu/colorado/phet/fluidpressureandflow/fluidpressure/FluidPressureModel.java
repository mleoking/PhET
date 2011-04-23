// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.fluidpressure;

import edu.colorado.phet.fluidpressureandflow.common.model.Balloon;
import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.common.model.PressureSensor;

/**
 * @author Sam Reid
 */
public class FluidPressureModel extends FluidPressureAndFlowModel {
    private Pool pool = new Pool();

    public FluidPressureModel() {
        addPressureSensor( new PressureSensor( this, 1, 0 ) );
        addPressureSensor( new PressureSensor( this, -4, 2 ) );//Show second pressure sensor at a y location that yields a different pressure value
        addBalloon( new Balloon( this, 3, 2 ) );
    }

    @Override
    public double getPressure( double x, double y ) {
        if ( y < 0 ) {
            return getStandardAirPressure() + liquidDensity.getValue() * gravity.getValue() * Math.abs( 0 - y );
        }
        else {
            return super.getPressure( x, y );
        }
    }

    public Pool getPool() {
        return pool;
    }
}