//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.components {
import mx.controls.sliderClasses.SliderThumb;

//REVIEW "My" is not a prefix that's suitable for production code.
/**
 * Thumb icon for a slider that has a 16 point size (rather than the smaller default), to make it easier to see and use.
 */
public class MySliderThumb extends SliderThumb {
    public static const SIZE: Number = 16; //REVIEW private

    public function MySliderThumb() {
        super();
        this.width = SIZE;
        this.height = SIZE;
    }
}
}