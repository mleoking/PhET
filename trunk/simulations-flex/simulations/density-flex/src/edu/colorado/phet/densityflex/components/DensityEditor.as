package edu.colorado.phet.densityflex.components {
import mx.controls.HSlider;

public class DensityEditor extends PropertyEditor {
    public function DensityEditor(property:NumericProperty) {
        super(property);
    }

    override protected function createSlider(property:NumericProperty):HSlider {
        const slider:HSlider = super.createSlider(property);
        slider.labels = ["styrofoam","water balloon","lead"];
        slider.minimum = 1E-6;
        slider.maximum = 15;
        slider.tickValues=[0.035,1,11.34];//values for styrofoam, water, lead
        slider.width = SLIDER_WIDTH;
        return slider;
    }
}
}