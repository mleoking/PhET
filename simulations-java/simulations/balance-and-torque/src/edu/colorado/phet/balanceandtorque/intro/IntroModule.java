// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.intro;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources;
import edu.colorado.phet.balanceandtorque.intro.model.IntroModel;
import edu.colorado.phet.balanceandtorque.intro.view.IntroCanvas;
import edu.colorado.phet.common.phetcommon.application.Module;

/**
 * Main module for this tab.
 *
 * @author John Blanco
 */
public class IntroModule extends Module {

    private IntroModel model;

    //REVIEW Having both a public and private constructor is a clever way to get around a problem with
    // the PhET framework: you want the clock to be in the model, but the clock needs to be pass to
    // the constructor. Too bad we need to resort to this, but I'll probably steal it for future use!
    // One suggestion though... Unless there is some reason for doing some stuff in the public constructor
    // and some in the private constructor, move everything that's in the public constructor into the
    // private constructor. As is, the private constructor is incomplete and, if it's visibility ever changes,
    // calling it will result in a module that has a control panel and logo panel, not what you want.
    // Ditto for BalanceLabModule and BalanceGameModule.
    public IntroModule() {
        this( new IntroModel() );
        setClockControlPanel( null );
        getModulePanel().setLogoPanel( null );
    }

    private IntroModule( IntroModel model ) {
        super( BalanceAndTorqueResources.Strings.INTRO, model.getClock() );
        this.model = model;
        setSimulationPanel( new IntroCanvas( model ) );
        reset();
    }

    @Override public void reset() {
        model.reset();
    }
}
