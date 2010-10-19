package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.view.modes.CustomObjectMode;
import edu.colorado.phet.densityandbuoyancy.view.modes.Mode;

import flash.events.Event;

import mx.events.FlexEvent;

public class BuoyancyPlaygroundContainer extends BuoyancyContainer {

    public function BuoyancyPlaygroundContainer() {
    }

    override public function init(): void {
        super.init();

        const fluidDensityControl: FluidDensityControl = new FluidDensityControl( buoyancyCanvas.model.fluidDensity, buoyancyCanvas.units );
        fluidDensityControl.setStyle( "bottom", DensityConstants.CONTROL_INSET );

        const updateFluidDensityControlLocation: Function = function(): void {
            fluidDensityControl.x = stage.width / 2 - fluidDensityControl.width / 2;
        };
        addEventListener( Event.RESIZE, updateFluidDensityControlLocation );
        fluidDensityControl.addEventListener( FlexEvent.UPDATE_COMPLETE, updateFluidDensityControlLocation ); // listen to when our fluid control gets its size
        updateFluidDensityControlLocation();

        addChild( fluidDensityControl );
    }

    override public function createCustomObjectMode( canvas: AbstractDBCanvas ): Mode {
        return new CustomObjectMode( canvas );
    }
}
}