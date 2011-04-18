//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.components {
import mx.controls.sliderClasses.SliderThumb;

/**
 * Thumb icon for a slider that has a 16 point size (rather than the smaller default), to make it easier to see and use.
 */
public class BiggerSliderThumb extends SliderThumb {
    private static const SIZE: Number = 16; //REVIEW private

    public function BiggerSliderThumb() {
        super();
        this.width = SIZE;
        this.height = SIZE;
    }
}
}