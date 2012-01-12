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

    public BalanceGameModule() {
        this( new BalanceGameModel() );
        setLogoPanel( null ); // Do this so that the logo panel won't appear if this module is used alone.
    }

    private BalanceGameModule( BalanceGameModel model ) {
        super( BalanceAndTorqueResources.Strings.GAME, model.getClock() );
        setClockControlPanel( null );
        setSimulationPanel( new BalanceGameCanvas( model ) );
    }
}
