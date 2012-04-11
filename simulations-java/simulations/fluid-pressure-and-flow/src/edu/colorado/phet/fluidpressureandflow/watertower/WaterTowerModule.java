// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.UserComponents;
import edu.colorado.phet.fluidpressureandflow.common.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.watertower.model.WaterTowerModel;
import edu.colorado.phet.fluidpressureandflow.watertower.view.WaterTowerCanvas;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.WATER_TOWER;

/**
 * The water tower module of the sim, shows a water tower which can be moved, and out of which water flows.  It also adds a measuring tape.
 *
 * @author Sam Reid
 */
public class WaterTowerModule extends FluidPressureAndFlowModule<WaterTowerModel> {
    public final Property<Boolean> measuringTapeVisible = new Property<Boolean>( false );
    public final WaterTowerCanvas canvas;

    public WaterTowerModule() {
        super( UserComponents.waterTowerTab, WATER_TOWER, new WaterTowerModel() );
        canvas = new WaterTowerCanvas( this );
        setSimulationPanel( canvas );
    }

    //Reset the module
    @Override public void reset() {
        super.reset();
        measuringTapeVisible.reset();
        canvas.reset();
    }
}