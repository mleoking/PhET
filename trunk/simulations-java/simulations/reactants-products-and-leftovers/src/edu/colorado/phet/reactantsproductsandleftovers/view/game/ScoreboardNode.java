/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel.GameAdapter;

/**
 * Scoreboard, displays the current state of the Game.
 * Also has a "New Game" button that allows the user to start over at any time.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ScoreboardNode extends GameScoreboardNode {
    
    private static final NumberFormat POINTS_FORMAT = new DecimalFormat( "0.#" );
    
    public ScoreboardNode( final GameModel model ) {
        super( GameModel.getLevelRange().getMax(), GameModel.getPerfectScore(), POINTS_FORMAT );
        
        // when the model changes, update the scoreboard
        model.addGameListener( new GameAdapter() {

            @Override
            public void levelChanged() {
                setLevel( model.getLevel() );
            }

            @Override
            public void pointsChanged() {
                setScore( model.getPoints() );
            }

            @Override
            public void timerVisibleChanged() {
                setTimerVisible( model.isTimerVisible() );
                if ( model.isTimerVisible() ) {
                    setTime( model.getTime() );
                }
            }
            
            @Override
            public void timeChanged() {
                setTime( model.getTime() );
            }
            
            @Override
            public void gameStarted() {
                setConfirmNewGame( true );
            }
            
            @Override 
            public void gameCompleted() {
                setConfirmNewGame( false );
            }
        });
        
        // when the "New Game" button is pressed, tell the model
        addGameScoreboardListener( new GameScoreboardListener() {
            public void newGamePressed() {
                model.newGame();
            }
        });
        
        // initial state
        setScore( model.getPoints() );
        setLevel( model.getLevel() );
        setTimerVisible( model.isTimerVisible() );
        setTime( model.getTime() );
    }
}
