package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.model.NumericProperty;
import edu.colorado.phet.densityandbuoyancy.model.Substance;
import edu.colorado.phet.densityandbuoyancy.view.units.Unit;

import mx.controls.HSlider;
import mx.events.SliderEvent;

public class DensityEditor extends PropertyEditor {
    var densityObject:DensityObject;

    public function DensityEditor(property:NumericProperty, minimum:Number, maximum:Number, unit:Unit, densityObject:DensityObject) {
        super(property, minimum, maximum, unit);
        this.densityObject = densityObject;
    }

    override protected function createSlider(property:NumericProperty, minimum:Number, maximum:Number, unit:Unit):HSlider {
        const slider:HSlider = super.createSlider(property, minimum, maximum, unit);
        slider.tickValues = [unit.fromSI(Substance.WOOD.getDensity()),unit.fromSI(Substance.WATER_BALLOON.getDensity()),unit.fromSI(Substance.LEAD.getDensity())];//values for styrofoam, water, lead

        slider.addEventListener(SliderEvent.THUMB_RELEASE, function (event:SliderEvent):void {
            var value:Number = unit.toSI(event.value);
            updateObject(maximum, minimum, value,Substance.WOOD);
            updateObject(maximum, minimum, value,Substance.WATER_BALLOON);
            updateObject(maximum, minimum, value,Substance.LEAD);
        });
        return slider;
    }

    //if within tolerance of any mode, switch to that mode
    private function updateObject(maximum:Number, minimum:Number, value:Number,substance:Substance):void {
        var tolerance:Number = (maximum - minimum) / 100.0 * 1.5;//last number is percent tolerance
        if (Math.abs(value - substance.getDensity()) < tolerance) {
            densityObject.substance = substance;
        }
    }
}
}