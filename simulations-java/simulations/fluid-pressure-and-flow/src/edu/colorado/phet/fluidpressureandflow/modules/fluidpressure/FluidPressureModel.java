// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.fluidpressure;

import edu.colorado.phet.fluidpressureandflow.model.Balloon;
import edu.colorado.phet.fluidpressureandflow.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.model.Pool;
import edu.colorado.phet.fluidpressureandflow.model.PressureSensor;

/**
 * @author Sam Reid
 */
public class FluidPressureModel extends FluidPressureAndFlowModel {
    private Pool pool = new Pool();

    public FluidPressureModel() {
        addPressureSensor( new PressureSensor( this, 1, 0 ) );
        addPressureSensor( new PressureSensor( this, -4, 1 ) );
        addBalloon( new Balloon( this, 3, 2 ) );
    }

    @Override
    public double getPressure( double x, double y ) {
        if ( y < 0 ) {
            return getStandardAirPressure() + getLiquidDensity() * getGravity() * Math.abs( 0 - y );
        }
        else {
            return super.getPressure( x, y );
        }
    }

    public Pool getPool() {
        return pool;
    }
}