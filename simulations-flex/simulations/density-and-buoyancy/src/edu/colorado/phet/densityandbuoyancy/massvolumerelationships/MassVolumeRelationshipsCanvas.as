//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.massvolumerelationships {
import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.densityandbuoyancy.components.DensityVBox;
import edu.colorado.phet.densityandbuoyancy.components.ModeRadioButton;
import edu.colorado.phet.densityandbuoyancy.view.*;
import edu.colorado.phet.flashcommon.ApplicationLifecycle;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.flexcommon.PhetLogoButton;

import flash.events.MouseEvent;

import mx.controls.Label;

/**
 * Requested modifications for Abraham, Gelder and Greenbowe, made by copying and modifying the version from the density sim.
 * Main canvas for the density sim. Contains all of the sim-related UI.  Top level class because there's only 1 tab in the sim (and therefore no tabs).
 */
public class MassVolumeRelationshipsCanvas extends AbstractDensityAndBuoyancyCanvas {

    private var densityCanvas: MassVolumeRelationshipsPlayAreaComponent;
    public var modeControlPanel: DensityVBox;
    private var _sameSubstanceButton: ModeRadioButton;

    public function MassVolumeRelationshipsCanvas() {
        super();

        addBackground();

        densityCanvas = new MassVolumeRelationshipsPlayAreaComponent( this );
        addChild( densityCanvas );

        //TODO: modeControlPanel should be a subclass of DensityVBox, eg ModeControlPanel that takes densityCanvas as constructor arg
        modeControlPanel = new DensityVBox();
        modeControlPanel.setStyle( "right", DensityAndBuoyancyConstants.CONTROL_INSET );
        modeControlPanel.y = DensityAndBuoyancyConstants.CONTROL_INSET;

        var label: Label = new Label();
        label.text = FlexSimStrings.get( 'mode.title', 'Blocks' );
        label.setStyle( "fontWeight", "bold" );
        modeControlPanel.addChild( label );

        _sameSubstanceButton = new ModeRadioButton( FlexSimStrings.get( 'mode.sameSubstance', 'Same Substance' ), true, function(): void {
            densityCanvas.switchToSameSubstance()
        } );
        modeControlPanel.addChild( _sameSubstanceButton );

        modeControlPanel.addChild( new ModeRadioButton( FlexSimStrings.get( 'mode.customObject', 'Custom' ), false, function(): void {
            densityCanvas.switchToCustomObject()
        } ) );
        modeControlPanel.addChild( new ModeRadioButton( FlexSimStrings.get( 'mode.objectsOfSameMass', 'Same Mass' ), false, function(): void {
            densityCanvas.switchToSameMass()
        } ) );
        modeControlPanel.addChild( new ModeRadioButton( FlexSimStrings.get( 'mode.objectsOfSameVolume', 'Same Volume' ), false, function(): void {
            densityCanvas.switchToSameVolume()
        } ) );
        modeControlPanel.addChild( new ModeRadioButton( FlexSimStrings.get( 'mode.mysteryObjects', 'Mystery' ), false, function(): void {
            densityCanvas.switchToMysteryObjects()
        } ) );

        addChild( modeControlPanel );

        addResetAll();
        addLogo();

        ApplicationLifecycle.addApplicationCompleteListener( function(): void {
            densityCanvas.switchToSameSubstance();
            densityCanvas.addEventListener( MouseEvent.MOUSE_DOWN, refocusCallback );

            var densityFlashCommon: DensityAndBuoyancyFlashCommon = new DensityFlashCommon();
            addChild( densityFlashCommon );
            densityFlashCommon.init();

            densityCanvas.start();
        } );
    }

    override public function resetAll(): void {
        super.resetAll();
//        customButton.selected = true;
        _sameSubstanceButton.selected = true;
        densityCanvas.resetAll();
    }

    //TODO this feature should be provided by common code: eg, PhetCanvas extends Canvas, and has method addLogo, all sims should extend PhetCanvas, not Canvas
    protected override function addLogo(): void {
        phetLogoButton = new PhetLogoButton();
        phetLogoButton.setStyle( "left", DensityAndBuoyancyConstants.CONTROL_INSET );
        phetLogoButton.setStyle( "bottom", DensityAndBuoyancyConstants.CONTROL_INSET );
        addChild( phetLogoButton );
    }
}
}