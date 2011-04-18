//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.densityandbuoyancy.components.DensityVBox;
import edu.colorado.phet.densityandbuoyancy.components.FluidDensityControl;
import edu.colorado.phet.densityandbuoyancy.view.modes.Mode;
import edu.colorado.phet.flashcommon.ApplicationLifecycle;
import edu.colorado.phet.flexcommon.FlexSimStrings;

import flash.events.MouseEvent;

import mx.controls.Label;
import mx.controls.RadioButton;

/**
 * Adds Buoyancy Playground-tab specific UI
 */
public class BuoyancyPlaygroundCanvas extends BuoyancyCanvas {
    private static var count: Number = 0;

    private var oneObjectButton: RadioButton;

    public function BuoyancyPlaygroundCanvas() {
        super( true, false );
        //Create the panel that lets the user choose "one object" or "two object" mode
        var modeControlPanel: DensityVBox = new DensityVBox();
        modeControlPanel.setStyle( "right", DensityAndBuoyancyConstants.CONTROL_INSET );
        modeControlPanel.y = DensityAndBuoyancyConstants.CONTROL_INSET;

        //Label that says "Blocks"
        var label: Label = new Label();
        label.text = FlexSimStrings.get( 'mode.title', 'Blocks' );
        label.setStyle( "fontWeight", "bold" );
        modeControlPanel.addChild( label );

        //Radio button for choosing "one block" mode
        var groupName: String = "playground_modes" + count;
        oneObjectButton = new RadioButton();
        oneObjectButton.groupName = groupName;
        oneObjectButton.label = FlexSimStrings.get( 'mode.one', 'One' );
        oneObjectButton.addEventListener( MouseEvent.CLICK, function( evt: MouseEvent ): void {
            buoyancyCanvas.switchToOneObject();
        } );
        modeControlPanel.addChild( oneObjectButton );

        //Radio button for choosing "two blocks" mode
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
            fluidDensityControl.setStyle( "bottom", DensityAndBuoyancyConstants.CONTROL_INSET );
            fluidDensityControl.setStyle( "horizontalCenter", 0 );

            addChild( fluidDensityControl );

            buoyancyCanvas.playgroundModes.oneObject.addListener( function(): void {
                oneObjectButton.selected = buoyancyCanvas.playgroundModes.oneObject.value
            } );
        } );
    }

    override public function getDefaultMode( canvas: AbstractDensityAndBuoyancyPlayAreaComponent ): Mode {
        return buoyancyCanvas.playgroundModes;
    }
}
}