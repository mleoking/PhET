//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view.modes {
import edu.colorado.phet.densityandbuoyancy.view.AbstractDBCanvas;

/**
 * A mode is a different configuration within a single module (tab), which can be selected via a radio button in the top-right of the canvas.
 */
//REVIEW based on class hierarchy, this looks like it's intended to be abstract? If so, why not throw exceptions like in AbstractDBContainer.addLogo?
//REVIEW This was initially confusing because it differs greatly from the concept of modes used in Gravity and Orbits,
// where each mode has a separate canvas and model.  Some documentation would help.
public class Mode {
    protected var canvas: AbstractDBCanvas;

    public function Mode( canvas: AbstractDBCanvas ) {
        this.canvas = canvas;
    }

    //REVIEW doc, when is this called and what should be done in here?
    public function teardown(): void {
        canvas.model.teardown();
    }

    //REVIEW doc, when is this called and what should be done in here?
    public function init(): void {
    }

    //REVIEW doc, when is this called and what should be done in here?
    public function reset(): void {
    }
}
}