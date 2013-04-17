//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.densityandbuoyancy.components.DensityVBox;
import edu.colorado.phet.densityandbuoyancy.components.IntroFluidDensityControl;
import edu.colorado.phet.densityandbuoyancy.view.modes.Mode;
import edu.colorado.phet.flashcommon.ApplicationLifecycle;
import edu.colorado.phet.flexcommon.FlexSimStrings;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.controls.Label;
import mx.controls.RadioButton;
import mx.events.FlexEvent;

/**
 * Adds Buoyancy Intro-tab specific UI
 */
public class BuoyancyIntroCanvas extends BuoyancyCanvas {
    public static var count: Number = 0;//for different button groups
    private var sameMassButton: RadioButton;

    public function BuoyancyIntroCanvas() {
        super( false, true );
        count = count + 1;

        //Vertical panel that lets the user choose from the different modes
        var modeControlPanel: DensityVBox = new DensityVBox();
        modeControlPanel.setStyle( "right", DensityAndBuoyancyConstants.CONTROL_INSET );
        modeControlPanel.y = DensityAndBuoyancyConstants.CONTROL_INSET;

        //Title for the box that says "Blocks"
        var label: Label = new Label();
        label.text = FlexSimStrings.get( 'mode.title', 'Blocks' );
        label.setStyle( "fontWeight", "bold" );
        modeControlPanel.addChild( label );

        var groupName: String = "modes" + count;

        //Button for same mass mode
        sameMassButton = new RadioButton();
        sameMassButton.groupName = groupName;
        sameMassButton.label = FlexSimStrings.get( 'mode.objectsOfSameMass', 'Same Mass' );
        sameMassButton.addEventListener( MouseEvent.CLICK, function( e: MouseEvent ): void {
            buoyancyCanvas.switchToSameMass();
        } );
        modeControlPanel.addChild( sameMassButton );

        //Button for same volume mode
        var sameVolumeButton: RadioButton = new RadioButton();
        sameVolumeButton.groupName = groupName;
        sameVolumeButton.label = FlexSimStrings.get( 'mode.objectsOfSameVolume', 'Same Volume' );
        sameVolumeButton.addEventListener( MouseEvent.CLICK, function( e: MouseEvent ): void {
            buoyancyCanvas.switchToSameVolume();
        } );
        modeControlPanel.addChild( sameVolumeButton );

        //Button for same density mode
        var sameDensityButton: RadioButton = new RadioButton();
        sameDensityButton.groupName = groupName;
        sameDensityButton.label = FlexSimStrings.get( 'mode.objectsOfSameDensity', 'Same Density' );
        sameDensityButton.addEventListener( MouseEvent.CLICK, function( e: MouseEvent ): void {
            buoyancyCanvas.switchToSameDensity();
        } );
        modeControlPanel.addChild( sameDensityButton );

        //Add the control panel
        addChild( modeControlPanel );

        //Set the initial mode
        sameMassButton.selected = true;

        //Add the fluid density control and wire it up
        ApplicationLifecycle.addApplicationCompleteListener( function(): void {
            const fluidDensityControl: IntroFluidDensityControl = new IntroFluidDensityControl( buoyancyCanvas.model.fluidDensity );
            fluidDensityControl.setStyle( "bottom", DensityAndBuoyancyConstants.CONTROL_INSET );

            const updateFluidDensityControlLocation: Function = function(): void {
                fluidDensityControl.x = stage.width / 2 - fluidDensityControl.width / 2;
            };
            addEventListener( Event.RESIZE, updateFluidDensityControlLocation );
            fluidDensityControl.addEventListener( FlexEvent.UPDATE_COMPLETE, updateFluidDensityControlLocation ); // listen to when our fluid control gets its size
            updateFluidDensityControlLocation();

            addChild( fluidDensityControl );
        } );
    }

    override public function resetAll(): void {
        super.resetAll();
        sameMassButton.selected = true;
    }

    override public function getDefaultMode( canvas: AbstractDensityAndBuoyancyPlayAreaComponent ): Mode {
        return buoyancyCanvas.sameMassMode;
    }
}
}