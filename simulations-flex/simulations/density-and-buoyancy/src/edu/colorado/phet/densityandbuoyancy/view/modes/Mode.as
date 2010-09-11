package edu.colorado.phet.densityandbuoyancy.view.modes {
import edu.colorado.phet.densityandbuoyancy.model.Scale;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDensityModule;

public class Mode {
    protected var module:AbstractDensityModule;

    public function Mode( module:AbstractDensityModule ) {
        this.module = module;
    }

    public function teardown():void {
        module.model.clearDensityObjects();
    }

    public function init():void {
        addScales();
    }

    public function addScales() : void {
        if ( module.showScales() ) {
            module.model.addDensityObject( new Scale( Scale.GROUND_SCALE_X, Scale.GROUND_SCALE_Y, module.model ) );
            module.model.addDensityObject( new Scale( Scale.POOL_SCALE_X, Scale.POOL_SCALE_Y, module.model ) );
        }
    }
}
}