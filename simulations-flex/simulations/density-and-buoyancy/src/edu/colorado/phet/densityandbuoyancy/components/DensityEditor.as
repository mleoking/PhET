//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.view.units.Unit;
import edu.colorado.phet.flexcommon.model.NumericProperty;

//REVIEW Setting control panel to "My Block" I can't directly control this slider. Assuming that's not a bug, need to change this doc.
// The doc indicates that this is for "controlling an object's density", but it's really for displaying an object's density.
/**
 * PropertyEditor for controlling an object's density that includes a slider with labeled tick marks.
 * The DensityEditor differs from a normal PropertyEditor in that it has slider tick marks and a bigger thumb, and a readout on the slider.
 */
public class DensityEditor extends PropertyEditor {
    private var densityObject: DensityObject;

    public function DensityEditor( property: NumericProperty, minimum: Number, maximum: Number, unit: Unit, dataTipClamp: Function, bounds: Bounds, sliderWidth: Number ) {
        super( property, minimum, maximum, unit, dataTipClamp, bounds, sliderWidth + 80, false );
        this.densityObject = densityObject;
        textField.enabled = false; // direct density changes are now disabled
        setStyle( "paddingTop", 10 ); // give us a bit more padding to compensate for the labeled tickmarks
    }

    override protected function createSlider( property: NumericProperty, minimum: Number, maximum: Number, unit: Unit, dataTipClamp: Function, bounds: Bounds ): SliderDecorator {
        const slider: SliderDecorator = super.createSlider( property, minimum, maximum, unit, dataTipClamp, bounds );
        slider.enableTickmarks();
        for each ( var material: Material in Material.LABELED_DENSITY_MATERIALS ) {
            slider.addTick( unit.fromSI( material.getDensity() ), material.tickColor, material.name )
        }
        slider.enabled = false; // direct density changes are now disabled
        return slider;
    }

    protected override function getSliderThumbClass(): Class {
        return DensitySliderThumb;
    }

    protected override function getSliderThumbOffset(): Number {
        return DensitySliderThumb.SIZE / 2;
    }
}
}