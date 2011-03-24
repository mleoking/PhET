//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.view.units.Unit;
import edu.colorado.phet.flexcommon.model.NumericProperty;

public class FluidDensityEditor extends PropertyEditor {
    public function FluidDensityEditor( property: NumericProperty, minimum: Number, maximum: Number, unit: Unit, dataTipClamp: Function, bounds: Bounds ) {
        super( property, minimum, maximum, unit, dataTipClamp, bounds );
    }

    override protected function createSlider( property: NumericProperty, minimum: Number, maximum: Number, unit: Unit, dataTipClamp: Function, bounds: Bounds ): SliderDecorator {
        const slider: SliderDecorator = super.createSlider( property, minimum, maximum, unit, dataTipClamp, bounds );
        slider.isFluidDensitySlider = true;
        slider.enableTickmarks();
        for each ( var material: Material in Material.LABELED_LIQUID_MATERIALS ) {
            var unitDensity: Number = unit.fromSI( material.getDensity() );
            slider.addTick( unitDensity, material.tickColor, material.name )
        }
        return slider;
    }
}
}