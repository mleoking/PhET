package edu.colorado.phet.fluidpressureandflow.modules.fluidpressure;

import java.awt.geom.Point2D;

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
    }

    @Override
    public double getPressure( Point2D position ) {
        if ( position.getY() < 0 ) {
            return getStandardAirPressure() + getLiquidDensity() * getGravity() * Math.abs( 0 - position.getY() );
        }
        else {
            return super.getPressure( position );
        }
    }

    public Pool getPool() {
        return pool;
    }
}