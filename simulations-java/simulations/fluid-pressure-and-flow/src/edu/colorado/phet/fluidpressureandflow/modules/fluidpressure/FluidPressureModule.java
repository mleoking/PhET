package edu.colorado.phet.fluidpressureandflow.modules.fluidpressure;

import edu.colorado.phet.common.phetcommon.application.Module;

/**
 * @author Sam Reid
 */
public class FluidPressureModule extends Module {
    FluidPressureModel model;

    public FluidPressureModule() {
        this( new FluidPressureModel() );
    }

    FluidPressureModule( FluidPressureModel model ) {
        super( "Intro", model.getClock() );
        this.model = model;
        setSimulationPanel( new FluidPressureCanvas( this ) );
        getModulePanel().setLogoPanel( null );
        setClockControlPanel( null );
    }

    public void resetAll() {
    }

    public FluidPressureModel getIntroModel() {
        return model;
    }
}
