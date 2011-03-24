//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.components {
import mx.controls.sliderClasses.SliderThumb;

public class MySliderThumb extends SliderThumb {
    public static const SIZE: Number = 16;

    public function MySliderThumb() {
        super();
        this.width = SIZE;
        this.height = SIZE;
    }
}
}