// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.module.game;

import edu.colorado.phet.common.games.GameSettings;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;

/**
 * Model for the "Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameModel {

    private final GameSettings gameSettings;

    public GameModel() {
        gameSettings = new GameSettings( new IntegerRange( 1, 3, 1 ) /* level */, true /* sound */, true /* timer */ );
    }

    public GameSettings getGameSettings() {
        return gameSettings;
    }

    public void startGame() {
        //TODO
        System.out.println( "GameModel.startGame" );//XXX
    }

    public void reset() {
        gameSettings.reset();
    }
}
