package edu.colorado.phet.densityandbuoyancy.view.modes {
import edu.colorado.phet.densityandbuoyancy.model.BuoyancyScale;
import edu.colorado.phet.densityandbuoyancy.model.Scale;
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
        addScales();
    }

    /**
     * Currently overridden whenever we need to add a scale for the Density sim
     */
    public function addScales(): void {
        if ( canvas.showScales() ) {
            canvas.model.addDensityObject( new BuoyancyScale( Scale.GROUND_SCALE_X, Scale.GROUND_SCALE_Y, canvas.model ) );
            canvas.model.addDensityObject( new BuoyancyScale( Scale.POOL_SCALE_X, Scale.POOL_SCALE_Y, canvas.model ) );
        }
    }

    public function reset(): void {
    }
}
}