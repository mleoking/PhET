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
import edu.colorado.phet.balanceandtorque.game.model.MassDistancePair;
import edu.colorado.phet.balanceandtorque.teetertotter.model.ColumnState;
import edu.colorado.phet.balanceandtorque.teetertotter.model.LabeledImageMass;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.ImageMass;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.Mass;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.ShapeMass;
import edu.colorado.phet.balanceandtorque.teetertotter.view.AttachmentBarNode;
import edu.colorado.phet.balanceandtorque.teetertotter.view.BrickStackNode;
import edu.colorado.phet.balanceandtorque.teetertotter.view.FulcrumAbovePlankNode;
import edu.colorado.phet.balanceandtorque.teetertotter.view.ImageMassNode;
import edu.colorado.phet.balanceandtorque.teetertotter.view.LabeledImageMassNode;
import edu.colorado.phet.balanceandtorque.teetertotter.view.LevelIndicatorNode;
import edu.colorado.phet.balanceandtorque.teetertotter.view.PlankNode;
import edu.colorado.phet.balanceandtorque.teetertotter.view.RotatingRulerNode;
import edu.colorado.phet.balanceandtorque.teetertotter.view.TiltedSupportColumnNode;
import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.games.GameOverNode;
import edu.colorado.phet.common.games.GameScoreboardNode;
import edu.colorado.phet.common.games.GameSettingsPanel;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.nodes.OutlinePText;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.background.OutsideBackgroundNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
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

    // Overall size of the "stage", which is essentially the play area.
    private static Dimension2D STAGE_SIZE = new PDimension( 1008, 679 );

    // Constants that control the appearance of the happy and sad faces, used
    // to provide feedback to the user.
    private static final double FACE_DIAMETER = STAGE_SIZE.getWidth() * 0.30;
    private static final Color FACE_COLOR = new Color( 255, 255, 0, 180 ); // translucent yellow
    private static final Color EYE_AND_MOUTH_COLOR = new Color( 0, 0, 0, 100 ); // translucent gray

    // Various other constants.
    private static Font BUTTON_FONT = new PhetFont( 24, false );

    private static final GameAudioPlayer GAME_AUDIO_PLAYER = new GameAudioPlayer( true ) {{
        init();
    }};

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Game model.
    private final BalanceGameModel model;

    // Scoreboard that tracks user's score while playing the game.
    private final GameScoreboardNode scoreboard;

    // Canvas layers.
    private PNode rootNode;
    private final PNode challengeLayer = new PNode();
    private final PNode controlLayer = new PNode();

    // Game nodes.
    private PNode gameSettingsNode;
    private PNode gameOverNode = null;

    // Nodes for entering and displaying specific mass values.
    private final MassValueEntryNode massValueEntryNode;
    private final MassValueEntryNode.DisplayAnswerNode massValueAnswerNode;

    // Create the smiling and frowning faces and center them on the screen
    private final SmileFaceWithScoreNode smilingFace = new SmileFaceWithScoreNode();
    private final PNode frowningFace = new FaceNode( FACE_DIAMETER, FACE_COLOR, EYE_AND_MOUTH_COLOR, EYE_AND_MOUTH_COLOR ) {{
        frown();
    }};

    // Buttons.
    // TODO: i18n of all buttons
    private TextButtonNode checkAnswerButton = new TextButtonNode( "Check Answer", BUTTON_FONT, Color.YELLOW );
    private TextButtonNode tryAgainButton = new TextButtonNode( "Try Again", BUTTON_FONT, Color.YELLOW );
    private TextButtonNode nextChallengeButton = new TextButtonNode( "Next", BUTTON_FONT, Color.YELLOW );
    private TextButtonNode displayCorrectAnswerButton = new TextButtonNode( "Display Correct Answer", BUTTON_FONT, Color.YELLOW );

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
                new Point( (int) Math.round( STAGE_SIZE.getWidth() * 0.40 ), (int) Math.round( STAGE_SIZE.getHeight() * 0.75 ) ),
                150 );

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        // Add the background that consists of the ground and sky.
        rootNode.addChild( new OutsideBackgroundNode( mvt, 3, 1 ) );

        // Add the layers.
        rootNode.addChild( controlLayer );
        rootNode.addChild( challengeLayer );

        // Add the fulcrum, the columns, etc.
        challengeLayer.addChild( new FulcrumAbovePlankNode( mvt, model.getFulcrum() ) );
        challengeLayer.addChild( new TiltedSupportColumnNode( mvt, model.getSupportColumn(), model.supportColumnState ) );
        challengeLayer.addChild( new PlankNode( mvt, model.getPlank(), this ) {{
            //Disable interactivity since the user should only be able to move the free block
            setPickable( false );
            setChildrenPickable( false );
        }} );
        challengeLayer.addChild( new AttachmentBarNode( mvt, model.getAttachmentBar() ) );

        // Watch the model and add/remove visual representations of masses.
        model.movableMasses.addElementAddedObserver( new VoidFunction1<Mass>() {
            public void apply( Mass mass ) {
                // Create and add the view representation for this mass.
                final PNode massNode = createMassNode( mass );
                challengeLayer.addChild( massNode );
                // Add the removal listener for if and when this mass is removed from the model.
                model.movableMasses.addElementRemovedObserver( mass, new VoidFunction0() {
                    public void apply() {
                        challengeLayer.removeChild( massNode );
                    }
                } );
            }
        } );
        model.fixedMasses.addElementAddedObserver( new VoidFunction1<MassDistancePair>() {
            public void apply( MassDistancePair massDistancePair ) {
                // Create and add the view representation for this mass.
                final PNode massNode = createMassNode( massDistancePair.mass );
                // Don't allow the user to move this mass around.
                massNode.setPickable( false );
                massNode.setChildrenPickable( false );
                // Add it to the correct layer.
                challengeLayer.addChild( massNode );
                // Add the removal listener for if and when this mass is removed from the model.
                model.fixedMasses.addElementRemovedObserver( massDistancePair, new VoidFunction0() {
                    public void apply() {
                        challengeLayer.removeChild( massNode );
                    }
                } );
            }
        } );

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

        // Hook up the audio player to the sound settings.
        model.gameSettings.soundEnabled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean soundEnabled ) {
                GAME_AUDIO_PLAYER.setEnabled( soundEnabled );
            }
        } );

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

        // Add the title.  It is blank to start with, and is updated later at
        // the appropriate state change.
        titleNode = new PNode();
        rootNode.addChild( titleNode );

        // Position and add the smiley and frowny faces.
        Point2D feedbackFaceCenter = new Point2D.Double( mvt.modelToViewX( 0 ), FACE_DIAMETER / 2 + 20 );
        smilingFace.centerFullBoundsOnPoint( feedbackFaceCenter.getX(), feedbackFaceCenter.getY() );
        frowningFace.centerFullBoundsOnPoint( feedbackFaceCenter.getX(), feedbackFaceCenter.getY() );
        rootNode.addChild( smilingFace );
        rootNode.addChild( frowningFace );

        // Add the dialog node that is used in some challenges to enable the
        // user to submit specific mass values.
        massValueEntryNode = new MassValueEntryNode( model );
        rootNode.addChild( massValueEntryNode );
        massValueAnswerNode = new MassValueEntryNode.DisplayAnswerNode( model );
        rootNode.addChild( massValueAnswerNode );
        massValueEntryNode.centerFullBoundsOnPoint( feedbackFaceCenter.getX(), feedbackFaceCenter.getY() );
        massValueAnswerNode.centerFullBoundsOnPoint( feedbackFaceCenter.getX(), feedbackFaceCenter.getY() );

        // Add and lay out the buttons.
        checkAnswerButton.centerFullBoundsOnPoint( mvt.modelToViewX( 0 ), mvt.modelToViewY( 0 ) + 40 );
        checkAnswerButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.checkAnswer();
            }
        } );
        rootNode.addChild( checkAnswerButton );
        tryAgainButton.centerFullBoundsOnPoint( mvt.modelToViewX( 0 ), mvt.modelToViewY( 0 ) + 40 );
        tryAgainButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.tryAgain();
            }
        } );
        rootNode.addChild( tryAgainButton );
        nextChallengeButton.centerFullBoundsOnPoint( mvt.modelToViewX( 0 ), mvt.modelToViewY( 0 ) + 40 );
        nextChallengeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.nextChallenge();
            }
        } );
        rootNode.addChild( nextChallengeButton );
        displayCorrectAnswerButton.centerFullBoundsOnPoint( mvt.modelToViewX( 0 ), mvt.modelToViewY( 0 ) + 40 );
        displayCorrectAnswerButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.displayCorrectAnswer();
            }
        } );
        rootNode.addChild( displayCorrectAnswerButton );

        // Add a listener that prevents the "Check Answer" button from being
        // enabled when there are no masses on the right side of the plank.
        VoidFunction1<Mass> checkAnswerButtonEnabledController = new VoidFunction1<Mass>() {
            public void apply( Mass addedMass ) {
                boolean massesOnRightSide = false;
                for ( Mass mass : model.getPlank().massesOnSurface ) {
                    if ( mass.getPosition().getX() > model.getPlank().getPlankSurfaceCenter().getX() ) {
                        massesOnRightSide = true;
                        break;
                    }
                }
                checkAnswerButton.setEnabled( massesOnRightSide );
            }
        };
        model.getPlank().massesOnSurface.addElementAddedObserver( checkAnswerButtonEnabledController );
        model.getPlank().massesOnSurface.addElementRemovedObserver( checkAnswerButtonEnabledController );

        // Register for changes to the game state and update accordingly.
        model.gameStateProperty.addObserver( new VoidFunction1<BalanceGameModel.GameState>() {
            public void apply( BalanceGameModel.GameState gameState ) {
                handleGameStateChange( gameState );
            }
        } );

        // Show the level indicator to help the student see if the plank is
        // perfectly balanced, but only show when the support column has been
        // removed.
        challengeLayer.addChild( new LevelIndicatorNode( mvt, model.getPlank() ) {{
            model.supportColumnState.addObserver( new VoidFunction1<ColumnState>() {
                public void apply( ColumnState columnState ) {
                    setVisible( columnState == ColumnState.NONE );
                }
            } );
        }} );

        // Add a check box for controlling whether the ruler is visible.
        BooleanProperty rulerVisibilityProperty = new BooleanProperty( false );
        // TODO: i18n
        PropertyCheckBox rulerVisibilityCheckBox = new PropertyCheckBox( "Show Ruler", rulerVisibilityProperty ) {{
            setFont( new PhetFont( 16 ) );
            setBackground( new Color( 0, 0, 0, 0 ) );
        }};
        controlLayer.addChild( new PSwing( rulerVisibilityCheckBox ) {{
            setOffset( mvt.modelToViewX( 3 ) - getFullBoundsReference().width / 2, mvt.modelToViewY( -0.25 ) );
        }} );

        // Add the ruler node.
        challengeLayer.addChild( new RotatingRulerNode( model.getPlank(), mvt, rulerVisibilityProperty ) );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    // Utility method for showing/hiding several PNodes, used in handleGameStateChange
    private void setVisible( boolean visible, PNode... nodes ) {
        for ( PNode node : nodes ) {
            node.setVisible( visible );
        }
    }

    // Utility method for showing several PNodes, used in handleGameStateChange
    private void show( PNode... nodes ) {
        setVisible( true, nodes );
    }

    // Utility method for hiding all of the game nodes whose visibility changes
    // during the course of a challenge.
    private void hideAllGameNodes() {
        setVisible( false, smilingFace, frowningFace, gameSettingsNode, scoreboard, titleNode, checkAnswerButton, tryAgainButton,
                    nextChallengeButton, displayCorrectAnswerButton, massValueEntryNode, massValueAnswerNode );
    }

    // When the game state changes, update the view with the appropriate
    // buttons and readouts.
    private void handleGameStateChange( BalanceGameModel.GameState newState ) {

        // Hide all nodes - the appropriate ones will be shown later based on
        // the current state.
        hideAllGameNodes();

        // Show the nodes appropriate to the state
        if ( newState == OBTAINING_GAME_SETUP ) {
            show( gameSettingsNode );
            hideChallenge();
            if ( gameOverNode != null ) {
                rootNode.removeChild( gameOverNode );
                gameOverNode = null;
            }
        }
        else if ( newState == PRESENTING_INTERACTIVE_CHALLENGE ) {
            updateTitle();
            show( scoreboard, titleNode );
            if ( model.getCurrentChallenge().getChallengeViewConfig().showMassEntryDialog ) {
                show( massValueEntryNode );
            }
            else {
                show( checkAnswerButton );
            }
            showChallengeGraphics();

            // Set the challenge layer to be interactive so that the user can
            // manipulate the masses.
            challengeLayer.setPickable( true );
            challengeLayer.setChildrenPickable( true );
        }
        else if ( newState == SHOWING_CORRECT_ANSWER_FEEDBACK ) {
            GAME_AUDIO_PLAYER.correctAnswer();
            smilingFace.setScore( model.getChallengeCurrentPointValue() );
            show( scoreboard, nextChallengeButton, smilingFace );
            showChallengeGraphics();
        }
        else if ( newState == SHOWING_INCORRECT_ANSWER_FEEDBACK_TRY_AGAIN ) {
            GAME_AUDIO_PLAYER.wrongAnswer();
            show( scoreboard, tryAgainButton, frowningFace );
            showChallengeGraphics();
        }
        else if ( newState == SHOWING_INCORRECT_ANSWER_FEEDBACK_MOVE_ON ) {
            GAME_AUDIO_PLAYER.wrongAnswer();
            show( scoreboard, displayCorrectAnswerButton, frowningFace );
            showChallengeGraphics();
        }
        else if ( newState == DISPLAYING_CORRECT_ANSWER ) {
            show( scoreboard, nextChallengeButton );
            if ( model.getCurrentChallenge().getChallengeViewConfig().showMassEntryDialog ) {
                massValueAnswerNode.update();
                show( massValueAnswerNode );
            }
            showChallengeGraphics();
        }
        else if ( newState == SHOWING_GAME_RESULTS ) {
            if ( model.getScoreProperty().get() == model.getMaximumPossibleScore() ) {
                GAME_AUDIO_PLAYER.gameOverPerfectScore();
            }
            else if ( model.getScoreProperty().get() == 0 ) {
                GAME_AUDIO_PLAYER.gameOverZeroScore();
            }
            else {
                GAME_AUDIO_PLAYER.gameOverImperfectScore();
            }
            showGameOverNode();
            hideChallenge();
        }
    }

    private void updateTitle() {
        titleNode = new OutlinePText( model.getCurrentChallenge().getChallengeViewConfig().title, new PhetFont( 64, true ), Color.WHITE, Color.BLACK, 1 ) {{
            setOffset( mvt.modelToViewX( 0 ) - getFullBoundsReference().width / 2,
                       STAGE_SIZE.getHeight() * 0.25 - getFullBoundsReference().height / 2 );
        }};
    }

    private void hideChallenge() {
        challengeLayer.setVisible( false );
        controlLayer.setVisible( false );
    }

    /**
     * Show the graphic model elements for this challenge, i.e. the plank,
     * fulcrum, etc.
     */
    private void showChallengeGraphics() {
        challengeLayer.setVisible( true );
        controlLayer.setVisible( true );

        // By default this is initially set up to be non-interactive.
        challengeLayer.setPickable( false );
        challengeLayer.setChildrenPickable( false );
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

    private PNode createMassNode( Mass mass ) {
        PNode massNode = null;
        if ( mass instanceof ShapeMass ) {
            // TODO: Always bricks right now, may have to change in the future.
            massNode = new BrickStackNode( (ShapeMass) mass, mvt, BalanceGameCanvas.this, new BooleanProperty( !mass.isMystery() ) );
        }
        else if ( mass instanceof LabeledImageMass ) {
            massNode = new LabeledImageMassNode( mvt, (LabeledImageMass) mass, BalanceGameCanvas.this, new BooleanProperty( !mass.isMystery() ) );
        }
        else if ( mass instanceof ImageMass ) {
            massNode = new ImageMassNode( mvt, (ImageMass) mass, BalanceGameCanvas.this, new BooleanProperty( !mass.isMystery() ) );
        }
        else {
            System.out.println( getClass().getName() + " - Error: Unrecognized mass type." );
            assert false;
        }
        return massNode;
    }

    // Utility method for converting seconds to milliseconds, which is needed
    // because the model things in seconds and the scoreboard uses ms.
    static long convertSecondsToMilliseconds( double seconds ) {
        return (long) ( seconds * 1000 );
    }

    //-------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //-------------------------------------------------------------------------

    /**
     * Node that represents a smiling face with the additional points gained
     * for getting the answer correct shown immediately below it.
     */
    private static class SmileFaceWithScoreNode extends PNode {

        private static final Font SCORE_FONT = new PhetFont( 20, true );
        private PText scoreNode;
        private FaceNode faceNode;

        private SmileFaceWithScoreNode() {
            faceNode = new FaceNode( FACE_DIAMETER, FACE_COLOR, EYE_AND_MOUTH_COLOR, EYE_AND_MOUTH_COLOR );
            addChild( faceNode );
            scoreNode = new PText();
            scoreNode.setFont( SCORE_FONT );
            addChild( scoreNode );
        }

        public void setScore( int score ) {
            if ( score >= 0 ) {
                scoreNode.setText( "+" + Integer.toString( score ) );
                scoreNode.setOffset( faceNode.getFullBoundsReference().getCenterX() - scoreNode.getFullBoundsReference().width / 2,
                                     faceNode.getFullBoundsReference().getMaxY() );
            }
            else {
                System.out.println( getClass().getName() + " - Warning: Attempt to set zero or negative score." );
                scoreNode.setText( "" );
            }
        }
    }
}