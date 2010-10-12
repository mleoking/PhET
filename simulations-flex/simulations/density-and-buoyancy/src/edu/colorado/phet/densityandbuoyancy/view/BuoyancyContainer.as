package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.components.DensityVBox;
import edu.colorado.phet.flexcommon.FlexSimStrings;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.controls.CheckBox;
import mx.controls.Label;
import mx.controls.RadioButton;
import mx.events.FlexEvent;

/**
 * Main canvas for the buoyancy sim. Contains all of the sim-related UI
 */
public class BuoyancyContainer extends AbstractDBContainer {

    private var buoyancyCanvas: BuoyancyCanvas;
    private var customButton: RadioButton;

    public function BuoyancyContainer() {
        super();

        addBackground();

        buoyancyCanvas = new BuoyancyCanvas();
        addChild( buoyancyCanvas );

        var modeControlPanel: DensityVBox = new DensityVBox();
        modeControlPanel.setStyle( "right", DensityConstants.CONTROL_INSET );
        modeControlPanel.y = DensityConstants.CONTROL_INSET;

        var label: Label = new Label();
        label.text = FlexSimStrings.get( 'mode.title', 'Objects' );
        label.setStyle( "fontWeight", "bold" );
        modeControlPanel.addChild( label );

        customButton = new RadioButton();
        customButton.groupName = "modes";
        customButton.label = FlexSimStrings.get( 'mode.customObject', 'Custom' );
        customButton.addEventListener( MouseEvent.CLICK, function(): void {
            buoyancyCanvas.switchToCustomObject()
        } );
        customButton.selected = true;
        modeControlPanel.addChild( customButton );

        var sameMassButton: RadioButton = new RadioButton();
        sameMassButton.groupName = "modes";
        sameMassButton.label = FlexSimStrings.get( 'mode.objectsOfSameMass', 'Same Mass' );
        sameMassButton.addEventListener( MouseEvent.CLICK, function(): void {
            buoyancyCanvas.switchToSameMass()
        } );
        modeControlPanel.addChild( sameMassButton );

        var sameVolumeButton: RadioButton = new RadioButton();
        sameVolumeButton.groupName = "modes";
        sameVolumeButton.label = FlexSimStrings.get( 'mode.objectsOfSameVolume', 'Same Volume' );
        sameVolumeButton.addEventListener( MouseEvent.CLICK, function(): void {
            buoyancyCanvas.switchToSameVolume()
        } );
        modeControlPanel.addChild( sameVolumeButton );

        var sameDensityButton: RadioButton = new RadioButton();
        sameDensityButton.groupName = "modes";
        sameDensityButton.label = FlexSimStrings.get( 'mode.objectsOfSameDensity', 'Same Density' );
        sameDensityButton.addEventListener( MouseEvent.CLICK, function(): void {
            buoyancyCanvas.switchToSameDensity()
        } );
        modeControlPanel.addChild( sameDensityButton );

        addChild( modeControlPanel );

        addResetAll();

        addLogo();

        var arrowControlPanel: DensityVBox = new DensityVBox();
        arrowControlPanel.setStyle( "left", DensityConstants.CONTROL_INSET );
        arrowControlPanel.visible = false; // will be made visible once we know the height of the logo

        phetLogoButton.addEventListener( FlexEvent.UPDATE_COMPLETE, function(): void {
            arrowControlPanel.setStyle( "bottom", phetLogoButton.height + 2 * DensityConstants.CONTROL_INSET + 35 );
            arrowControlPanel.visible = true;
        } );

        var gravityCheckbox: CheckBox = new CheckBox();
        gravityCheckbox.label = FlexSimStrings.get( 'forceArrows.gravity', 'Gravity' );
        gravityCheckbox.addEventListener( MouseEvent.CLICK, function(): void {
            buoyancyCanvas.setGravityForceVisible( gravityCheckbox.selected );
        } );
        arrowControlPanel.addChild( gravityCheckbox );

        var buoyancyCheckbox: CheckBox = new CheckBox();
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
    }


    override public function init(): void {
        super.init();

        const fluidDensityControl: FluidDensityControl = new FluidDensityControl( buoyancyCanvas.model.fluidDensity, buoyancyCanvas.units );
        fluidDensityControl.setStyle( "bottom", DensityConstants.CONTROL_INSET );

        const updateFluidDensityControlLocation: Function = function(): void {
            fluidDensityControl.x = stage.width / 2 - fluidDensityControl.width / 2;
        };
        stage.addEventListener( Event.RESIZE, updateFluidDensityControlLocation );
        fluidDensityControl.addEventListener( FlexEvent.UPDATE_COMPLETE, updateFluidDensityControlLocation ); // listen to when our fluid control gets its size
        updateFluidDensityControlLocation();

        addChild( fluidDensityControl );

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
        customButton.selected = true;
        buoyancyCanvas.resetAll();
    }
}
}