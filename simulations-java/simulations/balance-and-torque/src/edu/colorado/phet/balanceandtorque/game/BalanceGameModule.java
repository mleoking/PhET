// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.game;

import edu.colorado.phet.balanceandtorque.game.view.GameCanvas;
import edu.colorado.phet.balanceandtorque.teetertotter.model.BalancingActModel;
import edu.colorado.phet.common.phetcommon.application.Module;

/**
 * @author John Blanco
 */
public class BalanceGameModule extends Module {

    public BalanceGameModule() {
        this( new BalancingActModel() );

        setClockControlPanel( null );
    }

    private BalanceGameModule( BalancingActModel model ) {
        // TODO: i18n
        super( "Game", model.getClock() );
        setSimulationPanel( new GameCanvas() );
    }
}
