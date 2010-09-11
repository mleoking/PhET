package edu.colorado.phet.densityandbuoyancy.view.modes {
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
    }
}
}