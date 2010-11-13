package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import edu.colorado.phet.fluidpressureandflow.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.model.Pipe;

/**
 * @author Sam Reid
 */
public class FluidFlowModel extends FluidPressureAndFlowModel {
    private Pipe pipe = new Pipe();

    public Pipe getPipe() {
        return pipe;
    }
}
