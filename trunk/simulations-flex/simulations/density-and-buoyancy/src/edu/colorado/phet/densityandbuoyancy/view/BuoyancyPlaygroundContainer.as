package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.components.DensityVBox;
import edu.colorado.phet.densityandbuoyancy.view.modes.Mode;
import edu.colorado.phet.flashcommon.ApplicationLifecycle;
import edu.colorado.phet.flexcommon.FlexSimStrings;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.controls.Label;
import mx.controls.RadioButton;

public class BuoyancyPlaygroundContainer extends BuoyancyContainer {
    private var count: Number = 0;

    private var oneObjectButton: RadioButton;

    public function BuoyancyPlaygroundContainer() {
        super( true, false );
        var modeControlPanel: DensityVBox = new DensityVBox();
        modeControlPanel.setStyle( "right", DensityConstants.CONTROL_INSET );
        modeControlPanel.y = DensityConstants.CONTROL_INSET;

        var label: Label = new Label();
        label.text = FlexSimStrings.get( 'mode.title', 'Blocks' );
        label.setStyle( "fontWeight", "bold" );
        modeControlPanel.addChild( label );

        var groupName: String = "playground_modes" + count;

        oneObjectButton = new RadioButton();
        oneObjectButton.groupName = groupName;
        oneObjectButton.label = FlexSimStrings.get( 'mode.one', 'One' );
        oneObjectButton.addEventListener( MouseEvent.CLICK, function( evt: MouseEvent ): void {
            buoyancyCanvas.switchToOneObject();
        } );
        modeControlPanel.addChild( oneObjectButton );

        var twoObjectsButton: RadioButton = new RadioButton();
        twoObjectsButton.groupName = groupName;
        twoObjectsButton.label = FlexSimStrings.get( 'mode.two', 'Two' );
        twoObjectsButton.addEventListener( MouseEvent.CLICK, function( evt: MouseEvent ): void {
            buoyancyCanvas.switchToTwoObjects();
        } );
        modeControlPanel.addChild( twoObjectsButton );

        addChild( modeControlPanel );
        oneObjectButton.selected = true;

        ApplicationLifecycle.addApplicationCompleteListener( function(): void {
            const fluidDensityControl: FluidDensityControl = new FluidDensityControl( buoyancyCanvas.model.fluidDensity, buoyancyCanvas.units );
            fluidDensityControl.setStyle( "bottom", DensityConstants.CONTROL_INSET );

            const updateFluidDensityControlLocation: Function = function(): void {
                fluidDensityControl.x = stage.stageWidth / 2 - fluidDensityControl.width / 2;
            };
            stage.addEventListener( Event.RESIZE, updateFluidDensityControlLocation );

            addChild( fluidDensityControl );
            updateFluidDensityControlLocation();

            buoyancyCanvas.playgroundModes.oneObject.addListener( function(): void {
                oneObjectButton.selected = buoyancyCanvas.playgroundModes.oneObject.value
            } );
        } );
    }

    override public function getDefaultMode( canvas: AbstractDBCanvas ): Mode {
        return buoyancyCanvas.playgroundModes;
    }
}
}