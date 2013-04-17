// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.game;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

public class GameMakerModule extends PiccoloModule {
    public GameMakerModule() {
        super( "Game Maker", new ConstantDtClock() );
        setSimulationPanel( new GameMakerCanvas() );
        setClockControlPanel( null );
    }
}