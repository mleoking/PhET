// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.game;

import edu.colorado.phet.balanceandtorque.game.view.GameCanvas;
import edu.colorado.phet.balanceandtorque.teetertotter.model.TeeterTotterTorqueModel;
import edu.colorado.phet.common.phetcommon.application.Module;

/**
 * @author John Blanco
 */
public class TorqueGameModule extends Module {

    public TorqueGameModule() {
        this( new TeeterTotterTorqueModel() );

        setClockControlPanel( null );
    }

    private TorqueGameModule( TeeterTotterTorqueModel model ) {
        // TODO: i18n
        super( "Game", model.getClock() );
        setSimulationPanel( new GameCanvas() );
    }
}
