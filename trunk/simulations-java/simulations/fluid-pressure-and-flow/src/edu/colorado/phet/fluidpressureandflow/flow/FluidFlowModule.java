// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow;

import edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.UserComponents;
import edu.colorado.phet.fluidpressureandflow.common.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.flow.model.FluidFlowModel;
import edu.colorado.phet.fluidpressureandflow.flow.view.FluidFlowCanvas;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.FLOW;

/**
 * Module for the "flow" tab
 *
 * @author Sam Reid
 */
public class FluidFlowModule extends FluidPressureAndFlowModule<FluidFlowModel> {
    public FluidFlowModule() {
        super( UserComponents.flowTab, FLOW, new FluidFlowModel() );
        setSimulationPanel( new FluidFlowCanvas( this ) );
    }
}