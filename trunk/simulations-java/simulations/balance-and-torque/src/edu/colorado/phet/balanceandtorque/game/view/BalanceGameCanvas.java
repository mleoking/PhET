// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.game.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import edu.colorado.phet.balanceandtorque.game.model.BalanceGameModel;
import edu.colorado.phet.balanceandtorque.teetertotter.view.OutlinePText;
import edu.colorado.phet.common.games.GameOverNode;
import edu.colorado.phet.common.games.GameScoreboardNode;
import edu.colorado.phet.common.games.GameSettingsPanel;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.background.OutsideBackgroundNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Canvas for the balance game.
 *
 * @author John Blanco
 */
public class BalanceGameCanvas extends PhetPCanvas {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static Dimension2D STAGE_SIZE = new PDimension( 1008, 679 );

    private static Font BUTTON_FONT = new PhetFont( 18, false );

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Game model.
    private final BalanceGameModel model;

    // Scoreboard that tracks user's score while playing the game.
    private final GameScoreboardNode scoreboard;

    // Canvas layers.
    private PNode rootNode;

    // Game nodes.
    private PNode gameSettingsNode;
    private PNode gameOverNode = null;

    // Buttons.
    // TODO: i18n of all buttons
    private TextButtonNode checkAnswerButton = new TextButtonNode( "Check Answer (correct)", BUTTON_FONT, Color.YELLOW );
    private TextButtonNode tryAgainButton = new TextButtonNode( "Try Again", BUTTON_FONT, Color.YELLOW );
    private TextButtonNode nextChallengeButton = new TextButtonNode( "Next Challenge", BUTTON_FONT, Color.YELLOW );

    // TODO: This is for prototyping and should go eventually.  Soon even.
    private TextButtonNode checkAnswerWrongButton = new TextButtonNode( "Check Answer (wrong)", BUTTON_FONT, Color.YELLOW );
    private PNode titleNode;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /**
     * Constructor for the balance game canvas.
     *
     * @param model
     */
    public BalanceGameCanvas( final BalanceGameModel model ) {
        this.model = model;

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );

        // Set up the model-canvas transform.  The multiplier factors for the
        // 2nd point can be adjusted to shift the center right or left, and the
        // scale factor can be adjusted to zoom in or out (smaller numbers zoom
        // out, larger ones zoom in).
        ModelViewTransform mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( STAGE_SIZE.getWidth() * 0.5 ), (int) Math.round( STAGE_SIZE.getHeight() * 0.75 ) ),
                150 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        // Add the background that consists of the ground and sky.
        rootNode.addChild( new OutsideBackgroundNode( mvt, 3, 1 ) );

        // Create and add the game settings node.
        VoidFunction0 startFunction = new VoidFunction0() {
            public void apply() {
                model.startGame();
            }
        };
        gameSettingsNode = new PSwing( new GameSettingsPanel( model.gameSettings, startFunction ) ) {{
            scale( 1.5 );
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullBoundsReference().width / 2,
                       STAGE_SIZE.getHeight() / 2 - getFullBoundsReference().height / 2 );
        }};
        rootNode.addChild( gameSettingsNode );

        // Create and add the game scoreboard.
        scoreboard = new GameScoreboardNode( BalanceGameModel.MAX_LEVELS, model.getMaximumPossibleScore(), new DecimalFormat( "0.#" ) ) {{
            setBackgroundWidth( STAGE_SIZE.getWidth() * 0.85 );
            model.getClock().addClockListener( new ClockAdapter() {
                @Override
                public void simulationTimeChanged( ClockEvent clockEvent ) {
                    if ( model.isTimerEnabled() && model.isBestTimeRecorded( model.getLevel() ) ) {
                        setTime( model.getTime(), model.getBestTime( model.getLevel() ) );
                    }
                    else {
                        setTime( model.getTime() );
                    }
                }
            } );
            model.getScoreProperty().addObserver( new SimpleObserver() {
                public void update() {
                    setScore( model.getScoreProperty().get() );
                }
            } );
            model.getGameSettings().timerEnabled.addObserver( new SimpleObserver() {
                public void update() {
                    setTimerVisible( model.getGameSettings().timerEnabled.get() );
                }
            } );
            model.getGameSettings().level.addObserver( new SimpleObserver() {
                public void update() {
                    setLevel( model.getGameSettings().level.get() );
                }
            } );
        }};

        // Set up listener for the button on the score board that indicates that
        // a new game is desired.
        scoreboard.addGameScoreboardListener( new GameScoreboardNode.GameScoreboardListener() {
            public void newGamePressed() {
                model.newGame();
            }
        } );

        // Add the scoreboard.
        scoreboard.setOffset( STAGE_SIZE.getWidth() / 2 - scoreboard.getFullBoundsReference().width / 2,
                              STAGE_SIZE.getHeight() - scoreboard.getFullBoundsReference().height - 20 );
        rootNode.addChild( scoreboard );

        // Add the big title.
        // TODO: i18n
        titleNode = new OutlinePText( "Balance Me!", new PhetFont( 64, true ), Color.WHITE, Color.BLACK, 1 ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullBoundsReference().width / 2,
                       STAGE_SIZE.getHeight() * 0.25 - getFullBoundsReference().height / 2 );
        }};
        rootNode.addChild( titleNode );

        // Lay out and add the buttons.
        checkAnswerButton.setOffset( titleNode.getFullBoundsReference().getX(), titleNode.getFullBoundsReference().getMaxY() + 30 );
        rootNode.addChild( checkAnswerButton );
        checkAnswerWrongButton.setOffset( checkAnswerButton.getFullBoundsReference().getMaxX(), checkAnswerButton.getFullBoundsReference().getY() );
        rootNode.addChild( checkAnswerWrongButton );
        tryAgainButton.setOffset( checkAnswerButton.getFullBoundsReference().getX(), checkAnswerButton.getFullBoundsReference().getMaxY() + 5 );
        rootNode.addChild( tryAgainButton );
        nextChallengeButton.setOffset( checkAnswerButton.getFullBoundsReference().getX(), checkAnswerButton.getFullBoundsReference().getMaxY() + 5 );
        rootNode.addChild( nextChallengeButton );


    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    private void handleGameStateChange( BalanceGameModel.GameState oldState, BalanceGameModel.GameState newState ) {
        switch( newState ) {

            case OBTAINING_GAME_SETUP:
                gameSettingsNode.setVisible( true );
                scoreboard.setVisible( false );
                if ( gameOverNode != null ) {
                    rootNode.removeChild( gameOverNode );
                    gameOverNode = null;
                }
                titleNode.setVisible( false );
                break;

            case PRESENTING_INTERACTIVE_CHALLENGE:
                gameSettingsNode.setVisible( true );
                scoreboard.setVisible( true );
                titleNode.setVisible( true );
                break;

            case SHOWING_CORRECT_ANSWER_FEEDBACK:
                gameSettingsNode.setVisible( true );
                scoreboard.setVisible( true );
                titleNode.setVisible( false );
                break;

            case SHOWING_INCORRECT_ANSWER_FEEDBACK:
                gameSettingsNode.setVisible( true );
                scoreboard.setVisible( true );
                break;

            case DISPLAYING_CORRECT_ANSWER:
                gameSettingsNode.setVisible( true );
                scoreboard.setVisible( true );
                break;

            case SHOWING_GAME_RESULTS:
                gameSettingsNode.setVisible( true );
                scoreboard.setVisible( false );
                showGameOverNode();
                break;
        }
    }

    private void showGameOverNode() {
        assert gameOverNode == null; // Test that any previous ones were cleaned up.

        // Create the "game over" node based on the results.
        gameOverNode = new GameOverNode( model.getLevel(), model.getScoreProperty().get(),
                                         model.getMaximumPossibleScore(), new DecimalFormat( "##" ), model.getTime(),
                                         model.getBestTime( model.getLevel() ), model.isNewBestTime(),
                                         model.gameSettings.timerEnabled.get() );

        // Add the node.
        rootNode.addChild( gameOverNode );
    }

    //-------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //-------------------------------------------------------------------------
}