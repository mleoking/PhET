package edu.colorado.phet.densityflex.components {
import edu.colorado.phet.densityflex.model.Substance;

import mx.controls.HSlider;

public class DensityEditor extends PropertyEditor {
    public function DensityEditor(property:NumericProperty,minimum:Number,maximum:Number) {
        super(property,minimum, maximum);
    }

    override protected function createSlider(property:NumericProperty,minimum:Number,maximum:Number):HSlider {
        const slider:HSlider = super.createSlider(property,minimum, maximum);
        slider.tickValues = [Substance.STYROFOAM.getDensity(),Substance.WATER_BALLOON.getDensity(),Substance.LEAD.getDensity()];//values for styrofoam, water, lead
        slider.width = SLIDER_WIDTH;
        return slider;
    }
}
}