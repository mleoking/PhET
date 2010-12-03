package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.components.DensityVBox;
import edu.colorado.phet.densityandbuoyancy.model.BooleanProperty;
import edu.colorado.phet.densityandbuoyancy.view.modes.Mode;
import edu.colorado.phet.flashcommon.StageHandler;
import edu.colorado.phet.flexcommon.FlexSimStrings;

import flash.display.Stage;
import flash.events.MouseEvent;

import mx.controls.CheckBox;
import mx.controls.Label;

/**
 * Main canvas for the buoyancy sim. Contains all of the sim-related UI
 */
public class BuoyancyContainer extends AbstractDBContainer {

    protected var buoyancyCanvas: BuoyancyCanvas;

    public function BuoyancyContainer( extendedPool: Boolean, showExactLiquidColor: Boolean ) {
        super();

        addBackground();

        buoyancyCanvas = new BuoyancyCanvas( this, extendedPool, showExactLiquidColor );
        addChild( buoyancyCanvas );


        addResetAll();

        addLogo();

        var arrowControlPanel: DensityVBox = new DensityVBox();
        arrowControlPanel.setStyle( "left", DensityConstants.CONTROL_INSET );
        arrowControlPanel.setStyle( "bottom", DensityConstants.CONTROL_INSET );
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

        attachForceCheckbox( FlexSimStrings.get( 'forceArrows.gravity', 'Gravity' ), buoyancyCanvas.gravityArrowsVisible, DensityConstants.GRAVITY_COLOR );
        attachForceCheckbox( FlexSimStrings.get( 'forceArrows.buoyancy', 'Buoyancy' ), buoyancyCanvas.buoyancyArrowsVisible, DensityConstants.BUOYANCY_COLOR );
        attachForceCheckbox( FlexSimStrings.get( 'forceArrows.contact', 'Contact' ), buoyancyCanvas.contactArrowsVisible, DensityConstants.CONTACT_COLOR );
        attachForceCheckbox( FlexSimStrings.get( 'forceArrows.fluidDrag', 'Fluid Drag' ), buoyancyCanvas.fluidDragArrowsVisible, DensityConstants.FLUID_DRAG_COLOR );

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

        StageHandler.addStageCreationListener( function( stage: Stage ): void {
            // TODO: why multiple initialization functions? - JO
            buoyancyCanvas.init();
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

    public function getDefaultMode( canvas: AbstractDBCanvas ): Mode {
        throw new Error( "Abstract method error" );
    }

    protected override function addLogo(): void {
    }
}
}