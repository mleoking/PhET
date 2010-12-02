package edu.colorado.phet.densityandbuoyancy.components {
import mx.controls.sliderClasses.SliderThumb;

/**
 * The density slider thumb is smaller than the regular thumb to help users know it is just a readout and not a control.
 * @author Sam Reid
 */
public class DensitySliderThumb extends SliderThumb {
    public function DensitySliderThumb() {
        super();
        this.width = MySliderThumb.SIZE / 2;
        this.height = MySliderThumb.SIZE / 2;
    }
}
}