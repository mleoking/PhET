package edu.colorado.phet.fluidpressureandflow.modules.fluidpressure;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
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
        if ( position.getY() > 0 ) {
            return getPressureFunction().evaluate( position.getY() );
        }
        else {
            return getStandardAirPressure() + pool.getLiquidDensity() * getGravity() * Math.abs( 0 - position.getY() );
        }
    }

    public Pool getPool() {
        return pool;
    }

    @Override
    public void addFluidChangeObserver( SimpleObserver updatePressure ) {
        super.addFluidChangeObserver( updatePressure );
        pool.addDensityListener( updatePressure );
    }

}
