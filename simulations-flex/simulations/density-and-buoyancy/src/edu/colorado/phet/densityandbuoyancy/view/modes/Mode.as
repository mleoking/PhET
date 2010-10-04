package edu.colorado.phet.densityandbuoyancy.view.modes {
import edu.colorado.phet.densityandbuoyancy.model.BuoyancyScale;
import edu.colorado.phet.densityandbuoyancy.model.Scale;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDensityModule;

public class Mode {
    protected var module: AbstractDensityModule;

    public function Mode( module: AbstractDensityModule ) {
        this.module = module;
    }

    public function teardown(): void {
        module.model.teardown();
    }

    public function init(): void {
        addScales();
    }

    /**
     * Currently overridden whenever we need to add a scale for the Density sim
     */
    public function addScales(): void {
        if ( module.showScales() ) {
            module.model.addDensityObject( new BuoyancyScale( Scale.GROUND_SCALE_X, Scale.GROUND_SCALE_Y, module.model ) );
            module.model.addDensityObject( new BuoyancyScale( Scale.POOL_SCALE_X, Scale.POOL_SCALE_Y, module.model ) );
        }
    }
}
}