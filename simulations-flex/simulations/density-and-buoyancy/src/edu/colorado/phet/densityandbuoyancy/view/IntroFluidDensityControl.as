package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.components.DensityHBox;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.model.NumericProperty;
import edu.colorado.phet.densityandbuoyancy.view.units.Units;

import flash.events.MouseEvent;

import mx.containers.Grid;
import mx.controls.Label;
import mx.controls.RadioButton;

public class IntroFluidDensityControl extends DensityHBox {
    private var grid: Grid = new Grid();

    private var GROUP: String = "fluid.controls.intro";

    public function IntroFluidDensityControl( fluidDensity: NumericProperty, units: Units ) {
        const title: Label = new Label();
        title.text = "Fluid";
        title.setStyle( "fontWeight", "bold" );
        addChild( title );

        const radioButton: RadioButton = new RadioButton();
        radioButton.selected = true;
        radioButton.addEventListener( MouseEvent.CLICK, function(): void {
            fluidDensity.value = Material.WATER.getDensity();
        } );
        radioButton.groupName = GROUP;
        radioButton.label = "Water";
        addChild( radioButton );

        const radioButton2: RadioButton = new RadioButton();
        radioButton2.groupName = GROUP;
        radioButton2.addEventListener( MouseEvent.CLICK, function(): void {
            fluidDensity.value = Material.OLIVE_OIL.getDensity();
        } );
        radioButton2.label = "Oil";
        addChild( radioButton2 );

        setStyle( "paddingTop", 10 );
    }
}
}