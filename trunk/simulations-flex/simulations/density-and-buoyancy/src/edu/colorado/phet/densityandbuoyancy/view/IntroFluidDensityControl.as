package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.components.DensityHBox;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.view.units.Units;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.flexcommon.model.NumericProperty;

import flash.events.MouseEvent;

import mx.containers.Grid;
import mx.controls.Label;
import mx.controls.RadioButton;

public class IntroFluidDensityControl extends DensityHBox {
    private var grid: Grid = new Grid();

    private var GROUP: String = "fluid.controls.intro";

    public function IntroFluidDensityControl( fluidDensity: NumericProperty, units: Units ) {
        const title: Label = new Label();
        title.text = FlexSimStrings.get( "fluid.controls.intro.label", "Fluid" );
        title.setStyle( "fontWeight", "bold" );
        addChild( title );

        const oilButton: RadioButton = new RadioButton();
        oilButton.groupName = GROUP;
        oilButton.addEventListener( MouseEvent.CLICK, function( evt: MouseEvent ): void {
            fluidDensity.value = Material.OLIVE_OIL.getDensity();
        } );
        oilButton.label = FlexSimStrings.get( "material.oil", "Oil" );
        addChild( oilButton );

        const waterButton: RadioButton = new RadioButton();
        waterButton.selected = true;
        waterButton.addEventListener( MouseEvent.CLICK, function( evt: MouseEvent ): void {
            fluidDensity.value = Material.WATER.getDensity();
        } );
        waterButton.groupName = GROUP;
        waterButton.label = FlexSimStrings.get( "material.water", "Water" );
        addChild( waterButton );

        // keep radio buttons synchronized with the fluid density
        fluidDensity.addListener( function(): void {
            if ( fluidDensity.value == Material.OLIVE_OIL.getDensity() ) {
                oilButton.selected = true;
            }
            if ( fluidDensity.value == Material.WATER.getDensity() ) {
                waterButton.selected = true;
            }
        } );

        setStyle( "paddingTop", 10 );
    }
}
}