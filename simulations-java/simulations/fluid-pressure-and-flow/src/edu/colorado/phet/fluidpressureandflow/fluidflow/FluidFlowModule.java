// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.fluidflow;

import edu.colorado.phet.fluidpressureandflow.common.FPAFStrings;
import edu.colorado.phet.fluidpressureandflow.common.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.fluidflow.model.FluidFlowModel;

/**
 * @author Sam Reid
 */
public class FluidFlowModule extends FluidPressureAndFlowModule<FluidFlowModel> {
    public FluidFlowModule() {
        super( FPAFStrings.FLOW, new FluidFlowModel() );
        setSimulationPanel( new FluidFlowCanvas( this ) );
    }

    public FluidFlowModel getFluidFlowModel() {
        return getFluidPressureAndFlowModel();
    }
}
