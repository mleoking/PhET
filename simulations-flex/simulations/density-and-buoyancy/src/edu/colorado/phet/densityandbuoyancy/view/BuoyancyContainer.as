package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.components.DensityVBox;
import edu.colorado.phet.densityandbuoyancy.view.modes.Mode;
import edu.colorado.phet.flexcommon.FlexSimStrings;

import flash.events.MouseEvent;

import mx.controls.CheckBox;
import mx.controls.Label;

/**
 * Main canvas for the buoyancy sim. Contains all of the sim-related UI
 */
public class BuoyancyContainer extends AbstractDBContainer {

    protected var buoyancyCanvas: BuoyancyCanvas;

    public function BuoyancyContainer() {
        super();

        addBackground();

        buoyancyCanvas = new BuoyancyCanvas();
        addChild( buoyancyCanvas );


        addResetAll();

        addLogo();

        var arrowControlPanel: DensityVBox = new DensityVBox();
        arrowControlPanel.setStyle( "left", DensityConstants.CONTROL_INSET );
        arrowControlPanel.setStyle( "bottom", DensityConstants.CONTROL_INSET );
        arrowControlPanel.visible = true;

        //        var readoutControlPanel:DensityVBox = new DensityVBox();
        //        readoutControlPanel.setStyle("left")

        {
            var label: Label = new Label();
            label.text = FlexSimStrings.get( 'forceArrowControlPanelTitle', 'Show Forces' );
            label.setStyle( "fontWeight", "bold" );
            arrowControlPanel.addChild( label );
        }

        var gravityCheckbox: CheckBox = new CheckBox();
        gravityCheckbox.label = FlexSimStrings.get( 'forceArrows.gravity', 'Gravity' );
        gravityCheckbox.addEventListener( MouseEvent.CLICK, function(): void {
            buoyancyCanvas.setGravityForceVisible( gravityCheckbox.selected );
        } );
        arrowControlPanel.addChild( gravityCheckbox );

        var buoyancyCheckbox: CheckBox = new CheckBox();
        buoyancyCheckbox.selected = buoyancyCanvas.buoyantForceVisible;
        buoyancyCheckbox.label = FlexSimStrings.get( 'forceArrows.buoyancy', 'Buoyancy' );
        buoyancyCheckbox.addEventListener( MouseEvent.CLICK, function(): void {
            buoyancyCanvas.setBuoyancyForceVisible( buoyancyCheckbox.selected );
        } );
        arrowControlPanel.addChild( buoyancyCheckbox );

        var contactCheckbox: CheckBox = new CheckBox();
        contactCheckbox.label = FlexSimStrings.get( 'forceArrows.contact', 'Contact' );
        contactCheckbox.addEventListener( MouseEvent.CLICK, function(): void {
            buoyancyCanvas.setContactForceVisible( contactCheckbox.selected );
        } );
        arrowControlPanel.addChild( contactCheckbox );

        var fluidDragCheckbox: CheckBox = new CheckBox();
        fluidDragCheckbox.label = FlexSimStrings.get( 'forceArrows.fluidDrag', 'Fluid Drag' );
        fluidDragCheckbox.addEventListener( MouseEvent.CLICK, function(): void {
            buoyancyCanvas.setFluidDragForceVisible( fluidDragCheckbox.selected );
        } );
        arrowControlPanel.addChild( fluidDragCheckbox );
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
            valueCheckBox.label = FlexSimStrings.get( 'controlPanel.showVectorValues', "Vector Values" );
            valueCheckBox.addEventListener( MouseEvent.CLICK, function(): void {
                buoyancyCanvas.vectorValuesVisible.value = valueCheckBox.selected;
                //                buoyancyCanvas.setFluidDragForceVisible( showMassReadoutsCheckBox.selected );
            } );
            arrowControlPanel.addChild( valueCheckBox );
        }
    }

    override public function init(): void {
        super.init();

        // TODO: why multiple initialization functions? - JO
        buoyancyCanvas.init();
        buoyancyCanvas.doInit( this );
        buoyancyCanvas.switchToCustomObject();

        buoyancyCanvas.addEventListener( MouseEvent.MOUSE_DOWN, refocusCallback );

        buoyancyCanvas.start();
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

    public function createCustomObjectMode( canvas: AbstractDBCanvas ): Mode {
        throw new Error( "Abstract method error" );
    }

    protected override function addLogo(): void {
    }
}
}