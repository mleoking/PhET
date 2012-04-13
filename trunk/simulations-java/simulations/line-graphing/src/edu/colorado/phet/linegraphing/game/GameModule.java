// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.game;

import edu.colorado.phet.linegraphing.LGResources.Strings;
import edu.colorado.phet.linegraphing.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.LGModule;
import edu.colorado.phet.linegraphing.game.model.GameModel;
import edu.colorado.phet.linegraphing.game.view.GameCanvas;

/**
 * Module for the "Game" tab.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameModule extends LGModule {

    public GameModule() {
        super( UserComponents.lineGameTab, Strings.TAB_LINE_GAME );
        setSimulationPanel( new GameCanvas( new GameModel() ) );
    }
}
