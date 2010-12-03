package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.model.NumericProperty;
import edu.colorado.phet.densityandbuoyancy.view.units.Unit;
import edu.colorado.phet.flashcommon.ApplicationLifecycle;

import mx.controls.sliderClasses.SliderDataTip;
import mx.controls.sliderClasses.SliderThumb;

public class DensityEditor extends PropertyEditor {
    private var densityObject: DensityObject;
    private var dataTip: SliderDataTip = new SliderDataTip();

    public function DensityEditor( property: NumericProperty, minimum: Number, maximum: Number, unit: Unit, dataTipClamp: Function, bounds: Bounds, sliderWidth: Number ) {
        super( property, minimum, maximum, unit, dataTipClamp, bounds, sliderWidth );
        this.densityObject = densityObject;
        textField.enabled = false; // direct density changes are now disabled
        setStyle( "paddingTop", 10 ); // give us a bit more padding to compensate for the labeled tickmarks
    }

    override protected function createSlider( property: NumericProperty, minimum: Number, maximum: Number, unit: Unit, dataTipClamp: Function, bounds: Bounds ): SliderDecorator {
        const slider: SliderDecorator = super.createSlider( property, minimum, maximum, unit, dataTipClamp, bounds );
        for each ( var material: Material in Material.LABELED_DENSITY_MATERIALS ) {
            slider.addTick( unit.fromSI( material.getDensity() ), material.tickColor, material.name )
        }
        slider.enabled = false; // direct density changes are now disabled

        ApplicationLifecycle.addApplicationCompleteListener( function(): void {
            slider.changeListeners.push( function(): void {
                dataTip.text = DensityConstants.format( unit.fromSI( property.value ) ) + " kg/L";
                dataTip.validateNow();
                dataTip.setActualSize( dataTip.getExplicitOrMeasuredWidth(), dataTip.getExplicitOrMeasuredHeight() );
                positionDataTip( slider );
            } );

            dataTip.text = DensityConstants.format( unit.fromSI( property.value ) ) + " kg/L";
            dataTip.validateNow();
            dataTip.setActualSize( dataTip.getExplicitOrMeasuredWidth(), dataTip.getExplicitOrMeasuredHeight() );
            positionDataTip( slider );

            slider.addChild( dataTip );
            dataTip.invalidateProperties();
            dataTip.invalidateSize();
            dataTip.invalidateDisplayList();
        } );

        return slider;
    }

    private function positionDataTip( slider: SliderDecorator ): void {
        if ( slider.getThumbCount() > 0 ) {
            var thumb: SliderThumb = slider.getThumbAt( 0 )
            dataTip.x = thumb.x - dataTip.measuredWidth / 2;
            dataTip.y = thumb.y + thumb.height + slider.myslider.y;
        }
    }

    protected override function getSliderThumbClass(): Class {
        return DensitySliderThumb;
    }

    protected override function getSliderThumbOffset(): Number {
        return DensitySliderThumb.SIZE / 2;
    }
}
}