//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view.modes {
import edu.colorado.phet.densityandbuoyancy.view.AbstractDBCanvas;

/**
 * A mode is a different configuration within a single module (tab), which can be selected via a radio button in the top-right of the canvas.
 */
public class Mode {
    protected var canvas: AbstractDBCanvas;

    public function Mode( canvas: AbstractDBCanvas ) {
        this.canvas = canvas;
    }

    public function teardown(): void {
        canvas.model.teardown();
    }

    public function init(): void {
    }

    public function reset(): void {
    }
}
}