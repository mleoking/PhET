// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.game;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources;
import edu.colorado.phet.balanceandtorque.game.model.BalanceGameModel;
import edu.colorado.phet.balanceandtorque.game.view.BalanceGameCanvas;
import edu.colorado.phet.common.phetcommon.application.Module;

/**
 * The "Game" module.
 *
 * @author John Blanco
 */
public class BalanceGameModule extends Module {

    //REVIEW move setClockControlPanel to private constructor
    public BalanceGameModule() {
        this( new BalanceGameModel() );
        setClockControlPanel( null );
    }

    //REVIEW why is there no call to getModulePanel().setLogoPanel( null ), as in other modules?
    private BalanceGameModule( BalanceGameModel model ) {
        super( BalanceAndTorqueResources.Strings.GAME, model.getClock() );
        setSimulationPanel( new BalanceGameCanvas( model ) );
    }
}
