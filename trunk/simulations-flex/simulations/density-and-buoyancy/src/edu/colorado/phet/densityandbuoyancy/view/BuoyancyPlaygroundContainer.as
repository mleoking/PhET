package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.components.DensityVBox;
import edu.colorado.phet.densityandbuoyancy.view.modes.Mode;
import edu.colorado.phet.flexcommon.FlexSimStrings;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.controls.Label;
import mx.controls.RadioButton;
import mx.events.FlexEvent;

public class BuoyancyPlaygroundContainer extends BuoyancyContainer {
    private var count: Number = 0;

    public function BuoyancyPlaygroundContainer() {
        var modeControlPanel: DensityVBox = new DensityVBox();
        modeControlPanel.setStyle( "right", DensityConstants.CONTROL_INSET );
        modeControlPanel.y = DensityConstants.CONTROL_INSET;

        var label: Label = new Label();
        label.text = FlexSimStrings.get( 'mode.title', 'Objects' );
        label.setStyle( "fontWeight", "bold" );
        modeControlPanel.addChild( label );

        var groupName: String = "playground_modes" + count;

        var oneObjectButton: RadioButton = new RadioButton();
        oneObjectButton.groupName = groupName;
        oneObjectButton.label = FlexSimStrings.get( 'mode.one', 'One' );
        oneObjectButton.addEventListener( MouseEvent.CLICK, function(): void {
            buoyancyCanvas.switchToOneObject();
        } );
        modeControlPanel.addChild( oneObjectButton );

        var twoObjectsButton: RadioButton = new RadioButton();
        twoObjectsButton.groupName = groupName;
        twoObjectsButton.label = FlexSimStrings.get( 'mode.two', 'Two' );
        twoObjectsButton.addEventListener( MouseEvent.CLICK, function(): void {
            buoyancyCanvas.switchToTwoObjects();
        } );
        modeControlPanel.addChild( twoObjectsButton );

        addChild( modeControlPanel );
        oneObjectButton.selected = true;
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

    override public function getDefaultMode( canvas: AbstractDBCanvas ): Mode {
        return buoyancyCanvas.playgroundModes;
    }
}
}