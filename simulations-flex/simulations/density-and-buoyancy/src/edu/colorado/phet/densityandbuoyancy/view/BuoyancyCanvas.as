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
public class BuoyancyCanvas extends AbstractDensityAndBuoyancyCanvas {

    private var buoyancyModule: BuoyancyModule;
    private var customButton: RadioButton;

    public function BuoyancyCanvas() {
        super();

        addBackground();

        buoyancyModule = new BuoyancyModule();
        addChild( buoyancyModule );

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
            buoyancyModule.switchToCustomObject()
        } );
        customButton.selected = true;
        modeControlPanel.addChild( customButton );

        var sameMassButton: RadioButton = new RadioButton();
        sameMassButton.groupName = "modes";
        sameMassButton.label = FlexSimStrings.get( 'mode.objectsOfSameMass', 'Same Mass' );
        sameMassButton.addEventListener( MouseEvent.CLICK, function(): void {
            buoyancyModule.switchToSameMass()
        } );
        modeControlPanel.addChild( sameMassButton );

        var sameVolumeButton: RadioButton = new RadioButton();
        sameVolumeButton.groupName = "modes";
        sameVolumeButton.label = FlexSimStrings.get( 'mode.objectsOfSameVolume', 'Same Volume' );
        sameVolumeButton.addEventListener( MouseEvent.CLICK, function(): void {
            buoyancyModule.switchToSameVolume()
        } );
        modeControlPanel.addChild( sameVolumeButton );

        var sameDensityButton: RadioButton = new RadioButton();
        sameDensityButton.groupName = "modes";
        sameDensityButton.label = FlexSimStrings.get( 'mode.objectsOfSameDensity', 'Same Density' );
        sameDensityButton.addEventListener( MouseEvent.CLICK, function(): void {
            buoyancyModule.switchToSameDensity()
        } );
        modeControlPanel.addChild( sameDensityButton );

        addChild( modeControlPanel );

        /*

         <!--<mx:Canvas label="Advanced" fontSize="12">-->
         <!--<mx:Canvas backgroundColor="0x000000" width="100%" height="100%"/>-->
         <!--<mx:Panel left="20" y="20" title="Toybox">-->
         <!--<view:ToyboxView width="400" height="125" id="toybox" densityView="{tab1}"/>-->
         <!--</mx:Panel>-->
         <!--<view:DensityViewFull width="100%" height="100%" id="tab1"/>-->
         <!--<mx:Panel left="20" bottom="20">-->
         <!--<mx:CheckBox id="gravityForceVectorCheckBox" label="Gravity Force" selected="true" change="tab1.setGravityForceVisible(gravityForceVectorCheckBox.selected)"/>-->
         <!--<mx:CheckBox id="buoyancyForceVectorCheckBox" label="Buoyancy Force" selected="true" change="tab1.setBuoyancyForceVisible(buoyancyForceVectorCheckBox.selected)"/>-->
         <!--<mx:CheckBox id="contactForceVectorCheckBox" label="Contact Force" selected="true" change="tab1.setContactForceVisible(contactForceVectorCheckBox.selected)"/>-->
         <!--<mx:CheckBox id="fluidDragForceVectorCheckBox" label="Fluid Drag Force" selected="true" change="tab1.setFluidDragForceVisible(fluidDragForceVectorCheckBox.selected)"/>-->
         <!--</mx:Panel>-->
         <!--<mx:HBox id="propertiesContentPanel" right="20" top="20">-->
         <!--</mx:HBox>-->
         <!--<mx:Panel right="10" bottom="10">-->
         <!--<mx:Button label="Reset All" click="resetTab1()"/>-->
         <!--</mx:Panel>-->
         <!--</mx:Canvas>-->
         <!--</mx:TabNavigator>-->
         */

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
            buoyancyModule.setGravityForceVisible( gravityCheckbox.selected );
        } );
        arrowControlPanel.addChild( gravityCheckbox );

        var buoyancyCheckbox: CheckBox = new CheckBox();
        buoyancyCheckbox.label = FlexSimStrings.get( 'forceArrows.buoyancy', 'Buoyancy' );
        buoyancyCheckbox.addEventListener( MouseEvent.CLICK, function(): void {
            buoyancyModule.setBuoyancyForceVisible( buoyancyCheckbox.selected );
        } );
        arrowControlPanel.addChild( buoyancyCheckbox );

        var contactCheckbox: CheckBox = new CheckBox();
        contactCheckbox.label = FlexSimStrings.get( 'forceArrows.contact', 'Contact' );
        contactCheckbox.addEventListener( MouseEvent.CLICK, function(): void {
            buoyancyModule.setContactForceVisible( contactCheckbox.selected );
        } );
        arrowControlPanel.addChild( contactCheckbox );

        var fluidDragCheckbox: CheckBox = new CheckBox();
        fluidDragCheckbox.label = FlexSimStrings.get( 'forceArrows.fluidDrag', 'Fluid Drag' );
        fluidDragCheckbox.addEventListener( MouseEvent.CLICK, function(): void {
            buoyancyModule.setFluidDragForceVisible( fluidDragCheckbox.selected );
        } );
        arrowControlPanel.addChild( fluidDragCheckbox );

        addChild( arrowControlPanel );
    }


    override public function init(): void {
        super.init();

        const fluidDensityControl: FluidDensityControl = new FluidDensityControl( buoyancyModule.model.fluidDensity, buoyancyModule.units );
        fluidDensityControl.setStyle( "bottom", DensityConstants.CONTROL_INSET );

        const updateFluidDensityControlLocation: Function = function(): void {
            fluidDensityControl.x = stage.width / 2 - fluidDensityControl.width / 2;
        };
        stage.addEventListener( Event.RESIZE, updateFluidDensityControlLocation );
        fluidDensityControl.addEventListener( FlexEvent.UPDATE_COMPLETE, updateFluidDensityControlLocation ); // listen to when our fluid control gets its size
        updateFluidDensityControlLocation();

        addChild( fluidDensityControl );

        // TODO: why multiple initialization functions? - JO
        buoyancyModule.init();
        buoyancyModule.doInit( this );
        buoyancyModule.switchToCustomObject();

        buoyancyModule.addEventListener( MouseEvent.MOUSE_DOWN, refocusCallback );

        buoyancyModule.start();
    }

    public function get running(): Boolean {
        return buoyancyModule.running;
    }

    public function set running( b: Boolean ): void {
        buoyancyModule.running = b;
    }

    override public function resetAll(): void {
        super.resetAll();
        customButton.selected = true;
        buoyancyModule.resetAll();
    }
}
}