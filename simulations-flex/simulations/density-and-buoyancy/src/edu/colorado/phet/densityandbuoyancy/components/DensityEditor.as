package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.model.NumericProperty;
import edu.colorado.phet.densityandbuoyancy.view.units.Unit;

public class DensityEditor extends PropertyEditor {
    private var densityObject:DensityObject;

    public function DensityEditor(property:NumericProperty, minimum:Number, maximum:Number, unit:Unit, densityObject:DensityObject) {
        super(property, minimum, maximum, unit, densityObject);
        this.densityObject = densityObject;
    }

    override protected function createSlider(property:NumericProperty, minimum:Number, maximum:Number, unit:Unit, densityObject:DensityObject):SliderDecorator {
        const slider:SliderDecorator = super.createSlider(property, minimum, maximum, unit, densityObject);
        for each (var material:Material in Material.SELECTABLE_MATERIALS) {
            slider.addTick(unit.fromSI(material.getDensity()), material.tickColor, material.name)
        }
        return slider;
    }
}
}