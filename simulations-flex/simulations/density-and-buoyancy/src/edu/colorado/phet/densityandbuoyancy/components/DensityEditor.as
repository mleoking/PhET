package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.model.NumericProperty;
import edu.colorado.phet.densityandbuoyancy.view.units.Unit;

public class DensityEditor extends PropertyEditor {
    private var densityObject:DensityObject;

    public function DensityEditor(property:NumericProperty, minimum:Number, maximum:Number, unit:Unit, densityObject:DensityObject, dataTipClamp:Function) {
        super(property, minimum, maximum, unit, densityObject, dataTipClamp);
        this.densityObject = densityObject;

        function updateTextBoxEnabled():void {
            textField.enabled = densityObject.material.isCustom();
        }

        densityObject.addMaterialListener(updateTextBoxEnabled);
        updateTextBoxEnabled();

        setStyle( "paddingTop", 10 ); // give us a bit more padding to compensate for the labeled tickmarks
    }

    override protected function createSlider(property:NumericProperty, minimum:Number, maximum:Number, unit:Unit, densityObject:DensityObject, dataTipClamp:Function):SliderDecorator {
        const slider:SliderDecorator = super.createSlider(property, minimum, maximum, unit, densityObject, dataTipClamp);
        for each (var material:Material in Material.SELECTABLE_MATERIALS) {
            slider.addTick(unit.fromSI(material.getDensity()), material.tickColor, material.name)
        }
        function updateEnabled():void {
            slider.enabled = densityObject.material.isCustom();
        }

        densityObject.addMaterialListener(updateEnabled);
        updateEnabled();
        return slider;
    }
}
}