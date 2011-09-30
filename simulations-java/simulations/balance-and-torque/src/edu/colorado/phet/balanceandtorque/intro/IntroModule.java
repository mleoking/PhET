// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.intro;

import edu.colorado.phet.balanceandtorque.intro.model.IntroModel;
import edu.colorado.phet.balanceandtorque.intro.view.IntroCanvas;
import edu.colorado.phet.common.phetcommon.application.Module;

/**
 * Main module for this tab.
 *
 * @author John Blanco
 */
public class IntroModule extends Module {

    IntroModel model;

    public IntroModule() {
        this( new IntroModel() );
        setClockControlPanel( null );
        getModulePanel().setLogoPanel( null );
    }

    private IntroModule( IntroModel model ) {
        // TODO: i18n
        super( "Intro", model.getClock() );
        this.model = model;
        setSimulationPanel( new IntroCanvas( model ) );
        reset();
    }

    @Override public void reset() {
        model.reset();
    }
}
