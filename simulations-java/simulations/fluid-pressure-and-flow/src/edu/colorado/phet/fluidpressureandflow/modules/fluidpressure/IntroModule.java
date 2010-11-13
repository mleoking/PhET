package edu.colorado.phet.fluidpressureandflow.modules.fluidpressure;

import edu.colorado.phet.common.phetcommon.application.Module;

/**
 * @author Sam Reid
 */
public class IntroModule extends Module {
    IntroModel model;

    public IntroModule() {
        this( new IntroModel() );
    }

    IntroModule( IntroModel model ) {
        super( "Intro", model.getClock() );
        this.model = model;
        setSimulationPanel( new IntroCanvas( this ) );
        getModulePanel().setLogoPanel( null );
        setClockControlPanel( null );
    }

    public void resetAll() {
    }

    public IntroModel getIntroModel() {
        return model;
    }
}
