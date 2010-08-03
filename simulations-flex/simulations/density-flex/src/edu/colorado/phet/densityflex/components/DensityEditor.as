package edu.colorado.phet.densityflex.components {
import mx.controls.HSlider;

public class DensityEditor extends PropertyEditor {
    public function DensityEditor(property:NumericProperty) {
        super(property);
    }

    override protected function createSlider(property:NumericProperty):HSlider {
        const slider:HSlider = super.createSlider(property);
        const myArray:Array=new Array();
        myArray.push("cork");
        myArray.push("water");
        myArray.push("lead");
        slider.labels = myArray;
        slider.tickInterval=(slider.maximum-slider.minimum)/(3-1);
//        slider.width = slider.width*2;
        return slider;
    }
}
}