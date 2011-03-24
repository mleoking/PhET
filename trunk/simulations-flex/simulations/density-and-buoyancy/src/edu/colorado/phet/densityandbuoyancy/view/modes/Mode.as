//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view.modes {
import edu.colorado.phet.densityandbuoyancy.view.AbstractDBCanvas;

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