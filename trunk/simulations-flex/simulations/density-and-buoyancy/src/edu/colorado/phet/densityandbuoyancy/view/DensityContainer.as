//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.densityandbuoyancy.components.DensityVBox;
import edu.colorado.phet.densityandbuoyancy.components.ModeRadioButton;
import edu.colorado.phet.flashcommon.ApplicationLifecycle;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.flexcommon.PhetLogoButton;

import flash.events.MouseEvent;

import mx.controls.Label;
import mx.controls.RadioButton;

/**
 * Main canvas for the density sim. Contains all of the sim-related UI
 */
public class DensityContainer extends AbstractDBContainer {

    private var densityCanvas: DensityCanvas;
    private var customButton: RadioButton;

    public var modeControlPanel: DensityVBox;

    public function DensityContainer() {
        super();

        const myThis: DensityContainer = this;

        addBackground();

        densityCanvas = new DensityCanvas( myThis );
        addChild( densityCanvas );

        modeControlPanel = new DensityVBox();
        modeControlPanel.setStyle( "right", DensityAndBuoyancyConstants.CONTROL_INSET );
        modeControlPanel.y = DensityAndBuoyancyConstants.CONTROL_INSET;

        var label: Label = new Label();
        label.text = FlexSimStrings.get( 'mode.title', 'Blocks' );
        label.setStyle( "fontWeight", "bold" );
        modeControlPanel.addChild( label );

        customButton = new ModeRadioButton( FlexSimStrings.get( 'mode.customObject', 'Custom' ), true, function(): void {
            densityCanvas.switchToCustomObject()
        } );
        modeControlPanel.addChild( customButton );
        modeControlPanel.addChild( new ModeRadioButton( FlexSimStrings.get( 'mode.objectsOfSameMass', 'Same Mass' ), false, function(): void {
            densityCanvas.switchToSameMass()
        } ) );
        modeControlPanel.addChild( new ModeRadioButton( FlexSimStrings.get( 'mode.objectsOfSameVolume', 'Same Volume' ), false, function(): void {
            densityCanvas.switchToSameVolume()
        } ) );
        modeControlPanel.addChild( new ModeRadioButton( FlexSimStrings.get( 'mode.objectsOfSameDensity', 'Same Density' ), false, function(): void {
            densityCanvas.switchToSameDensity()
        } ) );
        modeControlPanel.addChild( new ModeRadioButton( FlexSimStrings.get( 'mode.mysteryObjects', 'Mystery' ), false, function(): void {
            densityCanvas.switchToMysteryObjects()
        } ) );

        addChild( modeControlPanel );

        addResetAll();
        addLogo();

        ApplicationLifecycle.addApplicationCompleteListener( function(): void {
            densityCanvas.switchToCustomObject();
            densityCanvas.addEventListener( MouseEvent.MOUSE_DOWN, refocusCallback );

            var densityFlashCommon: DensityAndBuoyancyFlashCommon = new DensityFlashCommon();
            addChild( densityFlashCommon );
            densityFlashCommon.init();

            densityCanvas.start();
        } );
    }

    override public function resetAll(): void {
        super.resetAll();
        customButton.selected = true;
        densityCanvas.resetAll();
    }

    protected override function addLogo(): void {
        phetLogoButton = new PhetLogoButton();
        phetLogoButton.setStyle( "left", DensityAndBuoyancyConstants.CONTROL_INSET );
        phetLogoButton.setStyle( "bottom", DensityAndBuoyancyConstants.CONTROL_INSET );
        addChild( phetLogoButton );
    }
}
}