// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.watertower;

import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowModule;

/**
 * @author Sam Reid
 */
public class WaterTowerModule extends FluidPressureAndFlowModule<WaterTowerModel> {
    public WaterTowerModule() {
        super( "Water Tower", new WaterTowerModel() );
        setSimulationPanel( new WaterTowerCanvas( this ) );
    }
}