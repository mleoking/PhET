package edu.colorado.phet.densityflex.components {
import edu.colorado.phet.densityflex.model.Substance;

import mx.controls.HSlider;

public class DensityEditor extends PropertyEditor {
    public function DensityEditor(property:NumericProperty) {
        super(property);
    }

    override protected function createSlider(property:NumericProperty):HSlider {
        const slider:HSlider = super.createSlider(property);
        slider.minimum = Substance.STYROFOAM.getDensity() * 0.9;
        slider.maximum = Substance.LEAD.getDensity() * 1.1;//have a maximum a bit beyond lead so students don't think lead is the most dense thing in the world
        slider.tickValues = [Substance.STYROFOAM.getDensity(),Substance.WATER_BALLOON.getDensity(),Substance.LEAD.getDensity()];//values for styrofoam, water, lead
        slider.width = SLIDER_WIDTH;
        return slider;
    }
}
}