// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowModule;

/**
 * @author Sam Reid
 */
public class FluidFlowModule extends FluidPressureAndFlowModule<FluidFlowModel> {
    public FluidFlowModule() {
        super( "Flow", new FluidFlowModel() );
        setSimulationPanel( new FluidFlowCanvas( this ) );
    }

    public FluidFlowModel getFluidFlowModel() {
        return getFluidPressureAndFlowModel();
    }
}
