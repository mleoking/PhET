//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.densityandbuoyancy.components.DensityVBox;
import edu.colorado.phet.densityandbuoyancy.view.modes.Mode;
import edu.colorado.phet.flashcommon.AbstractMethodError;
import edu.colorado.phet.flashcommon.ApplicationLifecycle;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.flexcommon.model.BooleanProperty;

import flash.events.MouseEvent;

import mx.controls.CheckBox;
import mx.controls.Label;

/**
 * Main canvas for the buoyancy sim. Contains all of the sim-related UI
 */
public class BuoyancyCanvas extends AbstractDensityAndBuoyancyCanvas {

    protected var buoyancyCanvas: BuoyancyPlayAreaComponent;

    public function BuoyancyCanvas( extendedPool: Boolean, showExactLiquidColor: Boolean ) {
        super();

        addBackground();

        buoyancyCanvas = new BuoyancyPlayAreaComponent( this, extendedPool, showExactLiquidColor );
        addChild( buoyancyCanvas );


        addResetAll();

        addLogo();

        var arrowControlPanel: DensityVBox = new DensityVBox();
        arrowControlPanel.setStyle( "left", DensityAndBuoyancyConstants.CONTROL_INSET );
        arrowControlPanel.setStyle( "bottom", DensityAndBuoyancyConstants.CONTROL_INSET );
        arrowControlPanel.visible = true;

        {
            var label: Label = new Label();
            label.text = FlexSimStrings.get( 'forceArrowControlPanelTitle', 'Show Forces' );
            label.setStyle( "fontWeight", "bold" );
            arrowControlPanel.addChild( label );
        }

        function attachForceCheckbox( label: String, property: BooleanProperty, color: int ): void {
            var checkBox: CheckBox = new CheckBox();
            checkBox.label = label;
            checkBox.addEventListener( MouseEvent.CLICK, function(): void {
                property.value = checkBox.selected;
            } );
            property.addListener( function(): void {
                checkBox.selected = property.value;
            } );
            styleForceCheckbox( checkBox, color );
            arrowControlPanel.addChild( checkBox );
        }

        attachForceCheckbox( FlexSimStrings.get( 'forceArrows.gravity', 'Gravity' ), buoyancyCanvas.gravityArrowsVisible, DensityAndBuoyancyConstants.GRAVITY_COLOR );
        attachForceCheckbox( FlexSimStrings.get( 'forceArrows.buoyancy', 'Buoyancy' ), buoyancyCanvas.buoyancyArrowsVisible, DensityAndBuoyancyConstants.BUOYANCY_COLOR );
        attachForceCheckbox( FlexSimStrings.get( 'forceArrows.contact', 'Contact' ), buoyancyCanvas.contactArrowsVisible, DensityAndBuoyancyConstants.CONTACT_COLOR );

        addChild( arrowControlPanel );

        {
            var label2: Label = new Label();
            label2.text = FlexSimStrings.get( 'controlPanel.readouts', 'Readouts' );
            label2.setStyle( "fontWeight", "bold" );
            arrowControlPanel.addChild( label2 );
        }

        {
            var showMassReadoutsCheckBox: CheckBox = new CheckBox();
            showMassReadoutsCheckBox.selected = buoyancyCanvas.massReadoutsVisible.value;
            showMassReadoutsCheckBox.label = FlexSimStrings.get( 'controlPanel.showMasses', 'Masses' );
            showMassReadoutsCheckBox.addEventListener( MouseEvent.CLICK, function(): void {//TODO: keyboard accessibilty
                buoyancyCanvas.setMassReadoutsVisible( showMassReadoutsCheckBox.selected );
            } );
            arrowControlPanel.addChild( showMassReadoutsCheckBox );

            buoyancyCanvas.massReadoutsVisible.addListener( function(): void {
                showMassReadoutsCheckBox.selected = buoyancyCanvas.massReadoutsVisible.value;
            } );
        }
        {
            var valueCheckBox: CheckBox = new CheckBox();
            buoyancyCanvas.vectorValuesVisible.addListener( function(): void {
                valueCheckBox.selected = buoyancyCanvas.vectorValuesVisible.value;
            } );
            valueCheckBox.selected = buoyancyCanvas.vectorValuesVisible.value;//TODO: autocallback from addListener
            valueCheckBox.label = FlexSimStrings.get( 'controlPanel.showVectorValues', "Force Values" );
            valueCheckBox.addEventListener( MouseEvent.CLICK, function(): void {
                buoyancyCanvas.vectorValuesVisible.value = valueCheckBox.selected;
            } );
            arrowControlPanel.addChild( valueCheckBox );
        }

        ApplicationLifecycle.addApplicationCompleteListener( function(): void {
            // TODO: why multiple initialization functions? - JO
            buoyancyCanvas.switchToDefaultMode();

            buoyancyCanvas.addEventListener( MouseEvent.MOUSE_DOWN, refocusCallback );

            buoyancyCanvas.start();
        } );
    }

    private function styleForceCheckbox( gravityCheckbox: CheckBox, color: int ): void {
        gravityCheckbox.setStyle( "iconColor", color );
        gravityCheckbox.setStyle( "color", color );
        gravityCheckbox.setStyle( "textRollOverColor", color );
        gravityCheckbox.setStyle( "textSelectedColor", color );
    }

    public function get running(): Boolean {
        return buoyancyCanvas.running;
    }

    public function set running( b: Boolean ): void {
        buoyancyCanvas.running = b;
    }

    override public function resetAll(): void {
        super.resetAll();
        buoyancyCanvas.resetAll();
    }

    public function getDefaultMode( canvas: AbstractDensityAndBuoyancyPlayAreaComponent ): Mode {
        throw new AbstractMethodError();
    }

    protected override function addLogo(): void {
    }
}
}