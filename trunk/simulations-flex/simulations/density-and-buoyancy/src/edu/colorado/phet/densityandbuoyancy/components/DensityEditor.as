package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.model.NumericProperty;
import edu.colorado.phet.densityandbuoyancy.model.Material;
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
        const tickValues:Array = new Array();
        for each ( var material:Material in Material.SELECTABLE_MATERIALS ) {
            tickValues.push(unit.fromSI(material.getDensity()));
        }
        slider.tickValues = tickValues;
        return slider;
    }
}
}