//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.view.units.Unit;
import edu.colorado.phet.flexcommon.model.NumericProperty;

/**
 * The FluidDensityEditor is a PropertyEditor that shows tick marks and tick mark labels for certain fluid densities.
 */
public class FluidDensityEditor extends PropertyEditor {
    public function FluidDensityEditor( property: NumericProperty, minimum: Number, maximum: Number, unit: Unit, dataTipClamp: Function, bounds: NumericClamp ) {
        super( property, minimum, maximum, unit, dataTipClamp, bounds );
    }

    override protected function createSlider( property: NumericProperty, minimum: Number, maximum: Number, unit: Unit, dataTipClamp: Function, bounds: NumericClamp ): SliderDecorator {
        const slider: SliderDecorator = super.createSlider( property, minimum, maximum, unit, dataTipClamp, bounds );
        slider.isFluidDensitySlider = true;
        slider.enableTickmarks();
        //REVIEW Material.LABELED_LIQUID_MATERIALS is an unnecessary coupling, better to pass in material list as an arg.
        for each ( var material: Material in Material.LABELED_LIQUID_MATERIALS ) {
            var unitDensity: Number = unit.fromSI( material.getDensity() );
            slider.addTick( unitDensity, material.tickColor, material.name )
        }
        return slider;
    }
}
}