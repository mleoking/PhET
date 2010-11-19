package edu.colorado.phet.fluidpressureandflow.modules.watertower;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.fluidpressureandflow.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.model.VelocitySensor;

/**
 * @author Sam Reid
 */
public class WaterTowerModel extends FluidPressureAndFlowModel implements VelocitySensor.Context {

    private WaterTower waterTower = new WaterTower();

    public WaterTowerModel() {
        addPressureSensor( new PressureSensor( this, 0, 0 ) );
        addVelocitySensor( new VelocitySensor( this, 0, 0 ) );
    }

    public ImmutableVector2D getVelocity( double x, double y ) {
        return new ImmutableVector2D();
    }

    public void addVelocityUpdateListener( SimpleObserver observer ) {

    }

    public WaterTower getWaterTower() {
        return waterTower;
    }
}
