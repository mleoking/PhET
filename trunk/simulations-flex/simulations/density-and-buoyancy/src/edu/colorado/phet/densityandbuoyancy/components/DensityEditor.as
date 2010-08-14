package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.model.NumericProperty;
import edu.colorado.phet.densityandbuoyancy.model.Substance;

import edu.colorado.phet.densityandbuoyancy.view.units.Unit;

import mx.controls.HSlider;

public class DensityEditor extends PropertyEditor {
    public function DensityEditor(property:NumericProperty, minimum:Number, maximum:Number,unit:Unit) {
        super(property, minimum, maximum,unit);
    }

    override protected function createSlider(property:NumericProperty, minimum:Number, maximum:Number,unit:Unit):HSlider {
        const slider:HSlider = super.createSlider(property, minimum, maximum,unit);
        slider.tickValues = [unit.fromSI(Substance.WOOD.getDensity()),unit.fromSI(Substance.WATER_BALLOON.getDensity()),unit.fromSI(Substance.LEAD.getDensity())];//values for styrofoam, water, lead
        slider.width = SLIDER_WIDTH;
        return slider;
    }
}
}