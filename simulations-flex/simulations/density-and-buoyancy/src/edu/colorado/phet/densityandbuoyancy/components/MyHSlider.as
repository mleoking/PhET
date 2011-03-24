//  Copyright 2002-2011, University of Colorado

package edu.colorado.phet.densityandbuoyancy.components {
import mx.controls.HSlider;

/**
 * This subclass is used to call the protected function commitProperties() on HSlider,
 * which fixes the problem that getSliderThumbCount = 1, but getSliderThumb(0) throws a nullpointerexception
 */
public class MyHSlider extends HSlider {
    public function MyHSlider() {
    }

    public function doCommitProperties(): void {
        commitProperties();
    }
}
}
