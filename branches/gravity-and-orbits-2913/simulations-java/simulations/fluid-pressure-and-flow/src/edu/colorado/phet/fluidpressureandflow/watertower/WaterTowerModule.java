// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.fluidpressureandflow.common.FPAFStrings;
import edu.colorado.phet.fluidpressureandflow.common.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.watertower.model.WaterTowerModel;
import edu.colorado.phet.fluidpressureandflow.watertower.view.WaterTowerCanvas;

/**
 * The water tower module of the sim, shows a water tower which can be moved, and out of which water flows.  It also adds a measuring tape.
 *
 * @author Sam Reid
 */
public class WaterTowerModule extends FluidPressureAndFlowModule<WaterTowerModel> {
    public final Property<Boolean> measuringTapeVisible = new Property<Boolean>( false );//TODO: move to subclasses that have measuring tape
    public WaterTowerCanvas canvas;

    public WaterTowerModule() {
        super( FPAFStrings.WATER_TOWER, new WaterTowerModel() );
        canvas = new WaterTowerCanvas( this );
        setSimulationPanel( canvas );
    }

    @Override public void reset() {
        super.reset();
        measuringTapeVisible.reset();
        canvas.reset();
    }
}