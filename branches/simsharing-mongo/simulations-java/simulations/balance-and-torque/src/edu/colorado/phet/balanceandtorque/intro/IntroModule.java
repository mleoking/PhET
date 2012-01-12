// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.intro;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources;
import edu.colorado.phet.balanceandtorque.intro.model.IntroModel;
import edu.colorado.phet.balanceandtorque.intro.view.IntroCanvas;
import edu.colorado.phet.common.phetcommon.application.Module;

/**
 * The "Intro" module.
 *
 * @author John Blanco
 */
public class IntroModule extends Module {

    private IntroModel model;

    public IntroModule() {
        this( new IntroModel() );
    }

    private IntroModule( IntroModel model ) {
        super( BalanceAndTorqueResources.Strings.INTRO, model.getClock() );
        this.model = model;
        setClockControlPanel( null );
        setSimulationPanel( new IntroCanvas( model ) );
        reset();
    }

    @Override public void reset() {
        model.reset();
    }
}
