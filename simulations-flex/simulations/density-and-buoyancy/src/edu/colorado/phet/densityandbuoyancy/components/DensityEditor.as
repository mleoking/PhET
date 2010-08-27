package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.model.NumericProperty;
import edu.colorado.phet.densityandbuoyancy.model.Substance;
import edu.colorado.phet.densityandbuoyancy.view.units.Unit;

import mx.controls.HSlider;
import mx.events.SliderEvent;

public class DensityEditor extends PropertyEditor {
    private var densityObject:DensityObject;

    public function DensityEditor(property:NumericProperty, minimum:Number, maximum:Number, unit:Unit, densityObject:DensityObject) {
        super(property, minimum, maximum, unit);
        this.densityObject = densityObject;
    }

    override protected function createSlider(property:NumericProperty, minimum:Number, maximum:Number, unit:Unit):HSlider {
        const slider:HSlider = super.createSlider(property, minimum, maximum, unit);
        slider.tickValues = [unit.fromSI(Substance.WOOD.getDensity()),unit.fromSI(Substance.WATER_BALLOON.getDensity()),unit.fromSI(Substance.ALUMINUM.getDensity())];//values for styrofoam, water, lead
        return slider;
    }
}
}