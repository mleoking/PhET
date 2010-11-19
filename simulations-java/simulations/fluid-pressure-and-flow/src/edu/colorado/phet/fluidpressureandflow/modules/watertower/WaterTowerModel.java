package edu.colorado.phet.fluidpressureandflow.modules.watertower;

import edu.colorado.phet.fluidpressureandflow.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.model.VelocitySensor;

/**
 * @author Sam Reid
 */
public class WaterTowerModel extends FluidPressureAndFlowModel {
    private VelocitySensor velocitySensor0;

    public WaterTowerModel() {
        addPressureSensor( new PressureSensor( this, 0, 0 ) );
//        velocitySensor0 = new VelocitySensor( 0,0, );
    }

//    public VelocitySensor getVelocitySensor0() {
//        return velocitySensor0;
//    }
}
