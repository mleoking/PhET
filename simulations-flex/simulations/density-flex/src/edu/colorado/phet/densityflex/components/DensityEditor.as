package edu.colorado.phet.densityflex.components {
import mx.controls.HSlider;

public class DensityEditor extends PropertyEditor {
    public function DensityEditor(property:NumericProperty) {
        super(property);
    }

    override protected function createSlider(property:NumericProperty):HSlider {
        const slider:HSlider = super.createSlider(property);
        const myArray:Array=new Array();
        myArray.push("styrofoam");
        myArray.push("water balloon");
        myArray.push("lead");
        slider.labels = myArray;
//        slider.tickInterval=(slider.maximum-slider.minimum)/(3-1);
        slider.tickValues=[0,10,50,60,70,90,100];
        slider.width = SLIDER_WIDTH;
        return slider;
    }
}
}