// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.module.game;

import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.balancingchemicalequations.model.GameProblemsFactory;
import edu.colorado.phet.common.games.GameSettings;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;

/**
 * Model for the "Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameModel {

    private static final IntegerRange LEVELS_RANGE = new IntegerRange( 1, 3, 1 );
    private static final int PROBLEMS_PER_GAME = 5;

    private final GameProblemsFactory problemsFactory;
    private final GameSettings gameSettings;
    private Equation[] problemSet;
    private int problemIndex;

    public GameModel() {
        problemsFactory = new GameProblemsFactory();
        gameSettings = new GameSettings( LEVELS_RANGE, true /* sound */, true /* timer */ );
    }

    public GameSettings getGameSettings() {
        return gameSettings;
    }

    public void startGame() {
        problemSet = problemsFactory.createProblemSet( PROBLEMS_PER_GAME, gameSettings.level.getValue() );
        problemIndex = 0;

        //XXX debug
        {
            System.out.println( "Problem set for level " + gameSettings.level.getValue() );
            for ( Equation equation : problemSet ) {
                System.out.println( equation.getName() );
            }
        }
    }

    public void reset() {
        gameSettings.reset();
    }
}
