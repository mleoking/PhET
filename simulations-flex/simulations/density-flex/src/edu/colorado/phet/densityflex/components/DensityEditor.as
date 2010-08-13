package edu.colorado.phet.densityflex.components {
import edu.colorado.phet.densityflex.model.NumericProperty;
import edu.colorado.phet.densityflex.model.Substance;

import edu.colorado.phet.densityflex.view.units.Unit;

import mx.controls.HSlider;

public class DensityEditor extends PropertyEditor {
    public function DensityEditor(property:NumericProperty, minimum:Number, maximum:Number,unit:Unit) {
        super(property, minimum, maximum,unit);
    }

    override protected function createSlider(property:NumericProperty, minimum:Number, maximum:Number,unit:Unit):HSlider {
        const slider:HSlider = super.createSlider(property, minimum, maximum,unit);
        slider.tickValues = [Substance.WOOD.getDensity(),Substance.WATER_BALLOON.getDensity(),Substance.LEAD.getDensity()];//values for styrofoam, water, lead
        slider.width = SLIDER_WIDTH;
        return slider;
    }
}
}