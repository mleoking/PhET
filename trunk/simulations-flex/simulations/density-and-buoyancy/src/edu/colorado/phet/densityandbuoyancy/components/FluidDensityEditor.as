package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.model.NumericProperty;
import edu.colorado.phet.densityandbuoyancy.view.units.Unit;

public class FluidDensityEditor extends PropertyEditor {
    public function FluidDensityEditor( property: NumericProperty, minimum: Number, maximum: Number, unit: Unit, dataTipClamp: Function, bounds: Bounds ) {
        super( property, minimum, maximum, unit, dataTipClamp, bounds );
    }

    override protected function createSlider( property: NumericProperty, minimum: Number, maximum: Number, unit: Unit, dataTipClamp: Function, bounds: Bounds ): SliderDecorator {
        const slider: SliderDecorator = super.createSlider( property, minimum, maximum, unit, dataTipClamp, bounds );
        for each ( var material: Material in Material.LABELED_LIQUID_MATERIALS ) {
            var unitDensity: Number = unit.fromSI( material.getDensity() );
            slider.addTick( unitDensity, material.tickColor, material.name )
        }
        return slider;
    }
}
}