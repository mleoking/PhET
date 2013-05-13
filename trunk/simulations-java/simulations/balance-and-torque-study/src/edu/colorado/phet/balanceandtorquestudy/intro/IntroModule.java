// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.intro;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources;
import edu.colorado.phet.balanceandtorque.intro.model.IntroModel;
import edu.colorado.phet.balanceandtorque.intro.view.IntroCanvas;
import edu.colorado.phet.common.piccolophet.SimSharingPiccoloModule;

import static edu.colorado.phet.balanceandtorque.BalanceAndTorqueSimSharing.UserComponents.introTab;

/**
 * The "Intro" module.
 *
 * @author John Blanco
 */
public class IntroModule extends SimSharingPiccoloModule {

    private IntroModel model;

    public IntroModule() {
        this( new IntroModel() );
    }

    private IntroModule( IntroModel model ) {
        super( introTab, BalanceAndTorqueResources.Strings.INTRO, model.getClock() );
        this.model = model;
        setClockControlPanel( null );
        setSimulationPanel( new IntroCanvas( model ) );
        reset();
    }

    @Override public void reset() {
        model.reset();
    }
}
