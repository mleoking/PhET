// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.game.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import edu.colorado.phet.balanceandtorque.game.model.BalanceGameModel;
import edu.colorado.phet.balanceandtorque.teetertotter.view.AttachmentBarNode;
import edu.colorado.phet.balanceandtorque.teetertotter.view.FulcrumAbovePlankNode;
import edu.colorado.phet.balanceandtorque.teetertotter.view.OutlinePText;
import edu.colorado.phet.balanceandtorque.teetertotter.view.PlankNode;
import edu.colorado.phet.balanceandtorque.teetertotter.view.TiltedSupportColumnNode;
import edu.colorado.phet.common.games.GameOverNode;
import edu.colorado.phet.common.games.GameScoreboardNode;
import edu.colorado.phet.common.games.GameSettingsPanel;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.background.OutsideBackgroundNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.balanceandtorque.game.model.BalanceGameModel.GameState.*;

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

    //Layer to show the challenge-specific nodes such as the plank, etc.
    private final PNode challengeLayer = new PNode();

    // Size of the smiling and frowning faces
    private double FACE_DIAMETER = STAGE_SIZE.getWidth() / 6;

    //Create the smiling and frowning faces and center them on the screen
    private final PNode smilingFace = new FaceNode( FACE_DIAMETER ) {{
        setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2,
                   STAGE_SIZE.getHeight() / 2 - getFullBounds().getHeight() / 2 );
    }};
    private final PNode frowningFace = new FaceNode( FACE_DIAMETER ) {{
        frown();
        setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2,
                   STAGE_SIZE.getHeight() / 2 - getFullBounds().getHeight() / 2 );
    }};

    // Buttons.
    // TODO: i18n of all buttons
    private TextButtonNode checkAnswerButton = new TextButtonNode( "Check Answer (correct)", BUTTON_FONT, Color.YELLOW );
    private TextButtonNode tryAgainButton = new TextButtonNode( "Try Again", BUTTON_FONT, Color.YELLOW );
    private TextButtonNode nextChallengeButton = new TextButtonNode( "Next", BUTTON_FONT, Color.YELLOW );
    private TextButtonNode displayCorrectAnswerButton = new TextButtonNode( "Display Correct Answer", BUTTON_FONT, Color.YELLOW );

    // TODO: This is for prototyping and should go eventually.  Soon even.
    private TextButtonNode checkAnswerWrongButton = new TextButtonNode( "Check Answer (wrong)", BUTTON_FONT, Color.YELLOW );
    private PNode titleNode;
    private ModelViewTransform mvt;

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
        mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( STAGE_SIZE.getWidth() * 0.5 ), (int) Math.round( STAGE_SIZE.getHeight() * 0.75 ) ),
                150 );

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
                        setTime( convertSecondsToMilliseconds( model.getTime() ), convertSecondsToMilliseconds( model.getBestTime( model.getLevel() ) ) );
                    }
                    else {
                        // TODO: Should we even be setting the time here?  Can we get rid of this?
                        setTime( convertSecondsToMilliseconds( model.getTime() ) );
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
                model.showGameInitDialog();
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

        // Add the smiley and frowny faces.
        rootNode.addChild( smilingFace );
        rootNode.addChild( frowningFace );

        // Lay out and add the buttons.
        checkAnswerButton.setOffset( titleNode.getFullBoundsReference().getX(), titleNode.getFullBoundsReference().getMaxY() + 30 );
        checkAnswerButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.checkGuess();
            }
        } );
        rootNode.addChild( checkAnswerButton );
        checkAnswerWrongButton.setOffset( checkAnswerButton.getFullBoundsReference().getMaxX(), checkAnswerButton.getFullBoundsReference().getY() );
        checkAnswerWrongButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.checkIncorrectGuess();
            }
        } );
        rootNode.addChild( checkAnswerWrongButton );
        tryAgainButton.setOffset( checkAnswerButton.getFullBoundsReference().getX(), checkAnswerButton.getFullBoundsReference().getMaxY() + 5 );
        tryAgainButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.tryAgain();
            }
        } );
        rootNode.addChild( tryAgainButton );
        nextChallengeButton.setOffset( checkAnswerButton.getFullBoundsReference().getX(), checkAnswerButton.getFullBoundsReference().getMaxY() + 5 );
        nextChallengeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.nextChallenge();
            }
        } );
        rootNode.addChild( nextChallengeButton );
        displayCorrectAnswerButton.setOffset( checkAnswerButton.getFullBoundsReference().getX(), checkAnswerButton.getFullBoundsReference().getMaxY() + 5 );
        displayCorrectAnswerButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.displayCorrectAnswer();
            }
        } );
        rootNode.addChild( displayCorrectAnswerButton );

        // Register for changes to the game state and update accordingly.
        model.gameStateProperty.addObserver( new VoidFunction1<BalanceGameModel.GameState>() {
            public void apply( BalanceGameModel.GameState gameState ) {
                handleGameStateChange( gameState );
            }
        } );

        //Attach the layer where the challenges will be shown
        rootNode.addChild( challengeLayer );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    // Utility method for showing/hiding several PNodes, used in handleGameStateChange
    public static void setVisible( boolean visible, PNode... nodes ) {
        for ( PNode node : nodes ) {
            node.setVisible( visible );
        }
    }

    // Utility method for showing several PNodes, used in handleGameStateChange
    public static void show( PNode... nodes ) {
        setVisible( true, nodes );
    }

    //When the game state changes, update the view with the appropriate buttons and readouts
    private void handleGameStateChange( BalanceGameModel.GameState newState ) {

        //Hide all nodes, then show the nodes relevant to each state.
        setVisible( false, smilingFace, frowningFace, gameSettingsNode, scoreboard, titleNode, checkAnswerButton, checkAnswerWrongButton, tryAgainButton,
                    nextChallengeButton, displayCorrectAnswerButton );

        //Show the nodes appropriate to the state
        if ( newState == OBTAINING_GAME_SETUP ) {
            show( gameSettingsNode );
            if ( gameOverNode != null ) {
                rootNode.removeChild( gameOverNode );
                gameOverNode = null;
            }
        }
        else if ( newState == PRESENTING_INTERACTIVE_CHALLENGE ) {
            show( scoreboard, titleNode, checkAnswerWrongButton, checkAnswerButton );
            showChallenge();
        }
        else if ( newState == SHOWING_CORRECT_ANSWER_FEEDBACK ) {
            show( scoreboard, nextChallengeButton, smilingFace );
            hideChallenge();
        }
        else if ( newState == SHOWING_INCORRECT_ANSWER_FEEDBACK_TRY_AGAIN ) {
            show( scoreboard, tryAgainButton, frowningFace );
            hideChallenge();
        }
        else if ( newState == SHOWING_INCORRECT_ANSWER_FEEDBACK_MOVE_ON ) {
            show( scoreboard, displayCorrectAnswerButton, frowningFace );
            hideChallenge();
        }
        else if ( newState == DISPLAYING_CORRECT_ANSWER ) {
            show( scoreboard, nextChallengeButton );
            hideChallenge();
        }
        else if ( newState == SHOWING_GAME_RESULTS ) {
            showGameOverNode();
            hideChallenge();
        }
    }

    private void hideChallenge() {
        challengeLayer.removeAllChildren();
    }

    //Add graphics for the next challenge, assumes that the model state already reflects the challenge to be shown
    private void showChallenge() {
        challengeLayer.removeAllChildren();
        challengeLayer.addChild( new FulcrumAbovePlankNode( mvt, model.getFulcrum() ) );
        challengeLayer.addChild( new TiltedSupportColumnNode( mvt, model.getSupportColumn(), model.columnState ) );
        challengeLayer.addChild( new PlankNode( mvt, model.getPlank(), this ) {{

            //Disable interactivity since the user should only be able to move the free block
            setPickable( false );
            setChildrenPickable( false );
        }} );
        challengeLayer.addChild( new AttachmentBarNode( mvt, model.getAttachmentBar() ) );
    }

    private void showGameOverNode() {
        assert gameOverNode == null; // Test that any previous ones were cleaned up.

        // Create the "game over" node based on the results.
        gameOverNode = new GameOverNode( model.getLevel(), model.getScoreProperty().get(), model.getMaximumPossibleScore(), new DecimalFormat( "##" ),
                                         convertSecondsToMilliseconds( model.getTime() ), convertSecondsToMilliseconds( model.getBestTime( model.getLevel() ) ),
                                         model.isNewBestTime(), model.gameSettings.timerEnabled.get() ) {{
            scale( 1.5 );
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullBoundsReference().width / 2,
                       STAGE_SIZE.getHeight() / 2 - getFullBoundsReference().height / 2 );
            addGameOverListener( new GameOverNode.GameOverListener() {
                public void newGamePressed() {
                    model.showGameInitDialog();
                }
            } );
        }};

        // Add the node.
        rootNode.addChild( gameOverNode );
    }

    // Utility method for converting seconds to milliseconds, which is needed
    // because the model things in seconds and the scoreboard uses ms.
    static long convertSecondsToMilliseconds( double seconds ) {
        return (long) ( seconds * 1000 );
    }

    //-------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //-------------------------------------------------------------------------
}