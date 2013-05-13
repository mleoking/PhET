// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.game.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueResources;
import edu.colorado.phet.balanceandtorquestudy.balancelab.view.AttachmentBarNode;
import edu.colorado.phet.balanceandtorquestudy.balancelab.view.MinSecNode;
import edu.colorado.phet.balanceandtorquestudy.common.model.ColumnState;
import edu.colorado.phet.balanceandtorquestudy.common.model.LevelSupportColumn;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.Mass;
import edu.colorado.phet.balanceandtorquestudy.common.view.FulcrumAbovePlankNode;
import edu.colorado.phet.balanceandtorquestudy.common.view.LevelIndicatorNode;
import edu.colorado.phet.balanceandtorquestudy.common.view.LevelSupportColumnNode;
import edu.colorado.phet.balanceandtorquestudy.common.view.MassNodeFactory;
import edu.colorado.phet.balanceandtorquestudy.common.view.PlankNode;
import edu.colorado.phet.balanceandtorquestudy.common.view.RotatingRulerNode;
import edu.colorado.phet.balanceandtorquestudy.common.view.TiltedSupportColumnNode;
import edu.colorado.phet.balanceandtorquestudy.game.model.BalanceGameChallenge;
import edu.colorado.phet.balanceandtorquestudy.game.model.BalanceGameChallengeFactory;
import edu.colorado.phet.balanceandtorquestudy.game.model.BalanceGameModel;
import edu.colorado.phet.balanceandtorquestudy.game.model.BalanceMassesChallenge;
import edu.colorado.phet.balanceandtorquestudy.game.model.MassDistancePair;
import edu.colorado.phet.balanceandtorquestudy.game.model.TiltPrediction;
import edu.colorado.phet.balanceandtorquestudy.game.model.TiltPredictionChallenge;
import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.nodes.OutlineTextNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.background.OutsideBackgroundNode;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueSimSharing.UserComponents.*;
import static edu.colorado.phet.balanceandtorquestudy.game.model.BalanceGameModel.GameState.*;
import static edu.colorado.phet.common.piccolophet.PhetPCanvas.CenteredStage.DEFAULT_STAGE_SIZE;

/**
 * Canvas for the balance game.
 *
 * @author John Blanco
 */
public class BalanceGameCanvas extends PhetPCanvas {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    // Constants that control the appearance of the happy and sad faces, used
    // to provide feedback to the user.
    private static final double FACE_DIAMETER = DEFAULT_STAGE_SIZE.getWidth() * 0.30;
    private static final Color FACE_COLOR = new Color( 255, 255, 0, 180 ); // translucent yellow
    private static final Color EYE_AND_MOUTH_COLOR = new Color( 0, 0, 0, 100 ); // translucent gray

    // Various other constants.
    private static Font BUTTON_FONT = new PhetFont( 24, false );

    private static final GameAudioPlayer GAME_AUDIO_PLAYER = new GameAudioPlayer( true ) {{
        init();
    }};
    public static final Color ACTIVE_BUTTON_COLOR = Color.YELLOW;

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Game model.
    private final BalanceGameModel model;

    // Scoreboard that displays the game status while the user is playing.
    private final GameStatusNode statusNode;

    // Canvas layers.
    private PNode rootNode;
    private final PNode challengeLayer = new PNode();
    private final PNode controlLayer = new PNode();
    private final PNode backToLabLayer = new PNode();

    // Game nodes.
    private PNode gameOverNode = null;

    // Nodes for entering and displaying specific mass values.
    private final MassValueEntryNode massValueEntryNode;
    private final MassValueEntryNode.DisplayAnswerNode massValueAnswerNode;

    // Node for submitting tilt predictions.
    protected TiltPredictionSelectorNode tiltPredictionSelectorNode;

    // Create the smiling and frowning faces and center them on the screen.
    private final SmileFaceWithScoreNode smilingFace = new SmileFaceWithScoreNode();
    private final PNode frowningFace = new FaceNode( FACE_DIAMETER, FACE_COLOR, EYE_AND_MOUTH_COLOR, EYE_AND_MOUTH_COLOR ) {{
        frown();
    }};

    // Buttons.
    private TextButtonNode checkAnswerButton = new TextButtonNode( BalanceAndTorqueResources.Strings.CHECK_ANSWER, BUTTON_FONT, ACTIVE_BUTTON_COLOR ) {{
        setUserComponent( checkAnswer );
    }};
    private TextButtonNode tryAgainButton = new TextButtonNode( BalanceAndTorqueResources.Strings.TRY_AGAIN, BUTTON_FONT, ACTIVE_BUTTON_COLOR ) {{
        setUserComponent( tryAgain );
    }};
    private TextButtonNode nextChallengeButton = new TextButtonNode( BalanceAndTorqueResources.Strings.NEXT, BUTTON_FONT, ACTIVE_BUTTON_COLOR ) {{
        setUserComponent( nextChallenge );
    }};
    private TextButtonNode displayCorrectAnswerButton = new TextButtonNode( BalanceAndTorqueResources.Strings.DISPLAY_CORRECT_ANSWER, BUTTON_FONT, ACTIVE_BUTTON_COLOR ) {{
        setUserComponent( displayAnswer );
    }};

    // Challenge title, may change for different challenge types.
    private OutlineTextNode challengeTitleNode;

    private ModelViewTransform mvt;

    // Property that is used to bump the user back to the lab if they submit an
    // incorrect answer.
//    private final BooleanProperty inGame;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /**
     * Constructor for the balance game canvas.
     *
     * @param model - Game model.
     */
    public BalanceGameCanvas( final BalanceGameModel model, final BooleanProperty inGame, Property<Integer> totalTimeCountdown ) {
        this.model = model;

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this ) );

        // Set up the model-canvas transform.  The multiplier factors for the
        // 2nd point can be adjusted to shift the center right or left, and the
        // scale factor can be adjusted to zoom in or out (smaller numbers zoom
        // out, larger ones zoom in).
        mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( DEFAULT_STAGE_SIZE.getWidth() * 0.40 ), (int) Math.round( DEFAULT_STAGE_SIZE.getHeight() * 0.75 ) ),
                150 );

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        // Add the background that consists of the ground and sky.
        rootNode.addChild( new OutsideBackgroundNode( mvt, 3, 1 ) );

        // Add the layers.
        rootNode.addChild( controlLayer );
        rootNode.addChild( challengeLayer );
        rootNode.addChild( backToLabLayer );

        // Add the fulcrum, the columns, etc.
        challengeLayer.addChild( new FulcrumAbovePlankNode( mvt, model.getFulcrum() ) );
        challengeLayer.addChild( new TiltedSupportColumnNode( mvt, model.getTiltSupportColumn(), model.supportColumnState ) );
        for ( LevelSupportColumn levelSupportColumn : model.getLevelSupportColumns() ) {
            challengeLayer.addChild( new LevelSupportColumnNode( mvt, levelSupportColumn, model.supportColumnState, false ) );
        }
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

        // Hook up the audio player to the sound settings.
        model.gameSettings.soundEnabled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean soundEnabled ) {
                GAME_AUDIO_PLAYER.setEnabled( soundEnabled );
            }
        } );

        // Create and add the game scoreboard.
        statusNode = new GameStatusNode( BalanceGameChallengeFactory.CHALLENGES_PER_SET, model.correctAnswers );

        // Add the scoreboard.
        statusNode.setOffset( DEFAULT_STAGE_SIZE.getWidth() / 2 - statusNode.getFullBoundsReference().width / 2,
                              DEFAULT_STAGE_SIZE.getHeight() - statusNode.getFullBoundsReference().height - 20 );
        controlLayer.addChild( statusNode );

        // Add the countdown time readout.
        controlLayer.addChild( new MinSecNode( totalTimeCountdown ) {{
            setOffset( DEFAULT_STAGE_SIZE.getWidth() - getFullBoundsReference().width - 10, 10 );
        }} );

        // Monitor the time and disable the game if the time reaches zero.
        totalTimeCountdown.addObserver( new VoidFunction1<Integer>() {
            public void apply( Integer secondsRemaining ) {
                if ( secondsRemaining <= 0 ) {
                    hideAllGameNodes();
                    hideChallenge();
                    showGameOverNode();
                }
            }
        } );

        // Add the title.  It is blank to start with, and is updated later at
        // the appropriate state change.
        challengeTitleNode = new OutlineTextNode( "Blank", new PhetFont( 64, true ), Color.WHITE, Color.BLACK, 1 );
        updateTitle();
        rootNode.addChild( challengeTitleNode );

        // Add the dialog node that is used in the mass deduction challenges
        // to enable the user to submit specific mass values.
        massValueEntryNode = new MassValueEntryNode( model, this );
        rootNode.addChild( massValueEntryNode );

        // Add the node that is used to depict the correct answer for the
        // mass deduction challenges.
        massValueAnswerNode = new MassValueEntryNode.DisplayAnswerNode( model, this );
        rootNode.addChild( massValueAnswerNode );

        // Position the mass entry and mass answer nodes in the same place.
        Point2D massEntryDialogCenter = new Point2D.Double( mvt.modelToViewX( 0 ),
                                                            challengeTitleNode.getFullBoundsReference().getMaxY() + massValueEntryNode.getFullBounds().height / 2 + 10 );
        massValueEntryNode.centerFullBoundsOnPoint( massEntryDialogCenter.getX(), massEntryDialogCenter.getY() );
        massValueAnswerNode.centerFullBoundsOnPoint( massEntryDialogCenter.getX(), massEntryDialogCenter.getY() );

        // Add the node that allows the user to submit their prediction of
        // which way the plank will tilt.  This is used in the tilt prediction
        // challenges.
        tiltPredictionSelectorNode = new TiltPredictionSelectorNode( model.gameStateProperty );
        rootNode.addChild( tiltPredictionSelectorNode );
        Point2D tiltPredictionSelectorNodeCenter = new Point2D.Double( mvt.modelToViewX( 0 ),
                                                                       challengeTitleNode.getFullBoundsReference().getMaxY() + 100 );
        tiltPredictionSelectorNode.centerFullBoundsOnPoint( tiltPredictionSelectorNodeCenter.getX(),
                                                            tiltPredictionSelectorNodeCenter.getY() );

        // Position and add the smiley and frowny faces.
        Point2D feedbackFaceCenter = new Point2D.Double( mvt.modelToViewX( 0 ), FACE_DIAMETER / 2 + 20 );
        smilingFace.centerFullBoundsOnPoint( feedbackFaceCenter.getX(), feedbackFaceCenter.getY() );
        frowningFace.centerFullBoundsOnPoint( feedbackFaceCenter.getX(), feedbackFaceCenter.getY() );
        rootNode.addChild( smilingFace );
        rootNode.addChild( frowningFace );

        // Add and lay out the buttons.
        checkAnswerButton.centerFullBoundsOnPoint( mvt.modelToViewX( 0 ), mvt.modelToViewY( 0 ) + 40 );

        // Add the listener that will submit the user's answers to the model.
        // Note that the Mass Deduction challenges submit through the mass
        // dialog instead of via this button.
        checkAnswerButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( model.getCurrentChallenge() instanceof BalanceMassesChallenge ) {
                    model.checkAnswer();
                }
                else if ( model.getCurrentChallenge() instanceof TiltPredictionChallenge ) {
                    model.checkAnswer( tiltPredictionSelectorNode.tiltPredictionProperty.get() );
                }
                else {
                    System.out.println( getClass().getName() + " Error: No check answer handler exists for this challenge type." );
                    assert false; // No handler exists for this challenge type.
                }
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

        // Add listeners that control the enabled state of the check answer
        // button.
        VoidFunction1<Mass> checkAnswerButtonEnabledUpdater = new VoidFunction1<Mass>() {
            public void apply( Mass addedMass ) {
                updateCheckAnswerButtonEnabled();
            }
        };
        model.getPlank().massesOnSurface.addElementAddedObserver( checkAnswerButtonEnabledUpdater );
        model.getPlank().massesOnSurface.addElementRemovedObserver( checkAnswerButtonEnabledUpdater );

        // Add a listener that controls the enabled state of the "Check Answer"
        // button for the tilt predictions challenges.
        tiltPredictionSelectorNode.tiltPredictionProperty.addObserver( new SimpleObserver() {
            public void update() {
                updateCheckAnswerButtonEnabled();
            }
        } );

        // Add a key listener that will allow the user to essentially press the
        // active button by pressing the Enter key.
        addKeyListener( new KeyAdapter() {
            @Override public void keyTyped( KeyEvent event ) {
                if ( event.getKeyChar() == KeyEvent.VK_ENTER ) {
                    // The user pressed the Enter key.  If one of the game
                    // control buttons is currently active, treat it as though
                    // this button has been pressed.
                    if ( nextChallengeButton.isVisible() && nextChallengeButton.isEnabled() ) {
                        model.nextChallenge();
                    }
                    else if ( tryAgainButton.isVisible() && tryAgainButton.isEnabled() ) {
                        model.tryAgain();
                    }
                    else if ( checkAnswerButton.isVisible() && checkAnswerButton.isEnabled() ) {
                        model.checkAnswer();
                    }
                    else if ( displayCorrectAnswerButton.isVisible() && displayCorrectAnswerButton.isEnabled() ) {
                        model.displayCorrectAnswer();
                    }
                }
            }
        } );

        // Add the nodes that will send the user back to the lab if they get
        // the wrong answer.
        backToLabLayer.addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, DEFAULT_STAGE_SIZE.getWidth(), DEFAULT_STAGE_SIZE.getHeight() ), new Color( 150, 150, 150, 100 ) ) );
        backToLabLayer.addChild( new ReturnToLabDialog( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                inGame.set( false );
            }
        } ) {{
            centerFullBoundsOnPoint( DEFAULT_STAGE_SIZE.getWidth() / 2, DEFAULT_STAGE_SIZE.getHeight() / 2 );
        }} );

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
        PropertyCheckBox rulerVisibilityCheckBox = new PropertyCheckBox( rulersCheckBox,
                                                                         BalanceAndTorqueResources.Strings.SHOW_RULERS,
                                                                         rulerVisibilityProperty );
        rulerVisibilityCheckBox.setFont( new PhetFont( 16 ) );
        rulerVisibilityCheckBox.setBackground( new Color( 0, 0, 0, 0 ) );
        // Ruler removed by request for Stanford study.
//        controlLayer.addChild( new PSwing( rulerVisibilityCheckBox ) {{
//            setOffset( mvt.modelToViewX( 3 ) - getFullBoundsReference().width / 2, mvt.modelToViewY( -0.25 ) );
//        }} );

        // Add the ruler node.
        challengeLayer.addChild( new RotatingRulerNode( model.getPlank(), mvt, rulerVisibilityProperty ) );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    // Utility method for showing/hiding several PNodes, used in handleGameStateChange
    private void setNodeVisibility( boolean visible, PNode... nodes ) {
        for ( PNode node : nodes ) {
            node.setVisible( visible );
        }
    }

    // Utility method for showing several PNodes, used in handleGameStateChange
    private void show( PNode... nodes ) {
        setNodeVisibility( true, nodes );
    }

    // Utility method for hiding all of the game nodes whose visibility changes
    // during the course of a challenge.
    private void hideAllGameNodes() {
        setNodeVisibility( false, smilingFace, frowningFace, statusNode, challengeTitleNode, checkAnswerButton, tryAgainButton,
                           nextChallengeButton, displayCorrectAnswerButton, massValueEntryNode, massValueAnswerNode, tiltPredictionSelectorNode,
                           backToLabLayer );
    }

    // When the game state changes, update the view with the appropriate
    // buttons and readouts.
    private void handleGameStateChange( BalanceGameModel.GameState newState ) {

        // Hide all nodes - the appropriate ones will be shown later based on
        // the current state.
        hideAllGameNodes();

        // Show the nodes appropriate to the state
        if ( newState == OBTAINING_GAME_SETUP ) {
            hideChallenge();
            if ( gameOverNode != null ) {
                rootNode.removeChild( gameOverNode );
                gameOverNode = null;
            }
        }
        else if ( newState == PRESENTING_INTERACTIVE_CHALLENGE ) {
            updateTitle();
            show( statusNode, challengeTitleNode );
            if ( model.getCurrentChallenge().getChallengeViewConfig().showMassEntryDialog ) {
                massValueEntryNode.clear();
                show( massValueEntryNode );
            }
            else {
                show( checkAnswerButton );
                if ( model.getCurrentChallenge().getChallengeViewConfig().showTiltPredictionSelector ) {
                    tiltPredictionSelectorNode.tiltPredictionProperty.reset();
                    show( tiltPredictionSelectorNode );
                }
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
            show( statusNode, nextChallengeButton, smilingFace );
            showChallengeGraphics();
        }
        else if ( newState == SHOWING_INCORRECT_ANSWER_FEEDBACK_TRY_AGAIN ) {
            GAME_AUDIO_PLAYER.wrongAnswer();
            show( statusNode, frowningFace );
            showChallengeGraphics();

        }
        else if ( newState == SHOWING_INCORRECT_ANSWER_FEEDBACK_MOVE_ON ) {
            GAME_AUDIO_PLAYER.wrongAnswer();
            show( backToLabLayer );
//            show( statusNode, displayCorrectAnswerButton, frowningFace );
            showChallengeGraphics();
        }
        else if ( newState == DISPLAYING_CORRECT_ANSWER ) {
            show( statusNode, nextChallengeButton );
            if ( model.getCurrentChallenge().getChallengeViewConfig().showMassEntryDialog ) {
                massValueAnswerNode.update();
                show( massValueAnswerNode );
            }
            else if ( model.getCurrentChallenge().getChallengeViewConfig().showTiltPredictionSelector ) {
                tiltPredictionSelectorNode.tiltPredictionProperty.set( model.getTipDirection() );
                show( tiltPredictionSelectorNode );
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
            showChallengesCompletedNode();
            hideChallenge();
        }
    }

    private void updateTitle() {
        BalanceGameChallenge balanceGameChallenge = model.getCurrentChallenge();
        if ( balanceGameChallenge != null ) {
            challengeTitleNode.setText( model.getCurrentChallenge().getChallengeViewConfig().title );
        }
        else {
            // Set the value to something so that layout can be done.  This
            // string doesn't need to be translated - user should never see it.
            challengeTitleNode.setText( "No challenge available." );
        }
        challengeTitleNode.setOffset( mvt.modelToViewX( 0 ) - challengeTitleNode.getFullBoundsReference().width / 2,
                                      DEFAULT_STAGE_SIZE.getHeight() * 0.15 - challengeTitleNode.getFullBoundsReference().height / 2 );
    }

    private void updateCheckAnswerButtonEnabled() {
        // I tried to avoid using 'instanceof', but in this case, it was tough
        // to work around.
        if ( model.getCurrentChallenge() instanceof BalanceMassesChallenge ) {
            // The button should be enabled whenever there are masses on the
            // right side of the plank.
            boolean massesOnRightSide = false;
            for ( Mass mass : model.getPlank().massesOnSurface ) {
                if ( mass.getPosition().getX() > model.getPlank().getPlankSurfaceCenter().getX() ) {
                    massesOnRightSide = true;
                    break;
                }
            }
            checkAnswerButton.setEnabled( massesOnRightSide );
        }
        else if ( model.getCurrentChallenge() instanceof TiltPredictionChallenge ) {
            // The button should be enabled once the user has made a prediction.
            checkAnswerButton.setEnabled( tiltPredictionSelectorNode.tiltPredictionProperty.get() != TiltPrediction.NONE );
        }
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

    private void showChallengesCompletedNode() {
        // Add the node.
        rootNode.addChild( new ChallengesCompletedNode() {{
            centerFullBoundsOnPoint( DEFAULT_STAGE_SIZE.getWidth() / 2, DEFAULT_STAGE_SIZE.getHeight() * 0.33 );
        }} );
    }

    private void showGameOverNode() {
        // Add the node.
        rootNode.addChild( new GameOverNode() {{
            centerFullBoundsOnPoint( DEFAULT_STAGE_SIZE.getWidth() / 2, DEFAULT_STAGE_SIZE.getHeight() * 0.33 );
        }} );
    }

    private PNode createMassNode( Mass mass ) {
        return MassNodeFactory.createMassNode( mass, new BooleanProperty( !mass.isMystery() ), mvt, this );
    }

    // Utility method for converting seconds to milliseconds, which is needed
    // because the model things in seconds and the scoreboard uses ms.
    static long convertSecondsToMilliseconds( double seconds ) {
        return (long) ( seconds * 1000 );
    }

    public PNode getChallengeLayer() {
        return challengeLayer;
    }

    //-------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //-------------------------------------------------------------------------

    /**
     * Node that represents a smiling face with the additional points gained
     * for getting the answer correct shown immediately below it.
     */
    private static class SmileFaceWithScoreNode extends PNode {

        private static final Font SCORE_FONT = new PhetFont( 60, true );
        private OutlineTextNode scoreNode;
        private FaceNode faceNode;

        private SmileFaceWithScoreNode() {
            faceNode = new FaceNode( FACE_DIAMETER, FACE_COLOR, EYE_AND_MOUTH_COLOR, EYE_AND_MOUTH_COLOR );
            addChild( faceNode );
            scoreNode = new OutlineTextNode( "X", SCORE_FONT, Color.YELLOW, Color.BLACK, 1 );
            addChild( scoreNode );
        }

        public void setScore( int score ) {
            if ( score >= 0 ) {
                scoreNode.setText( "+" + Integer.toString( score ) );
                scoreNode.setOffset( faceNode.getFullBoundsReference().getMaxX() - scoreNode.getFullBoundsReference().width + 10,
                                     faceNode.getFullBoundsReference().getMaxY() - scoreNode.getFullBoundsReference().height );
            }
            else {
                System.out.println( getClass().getName() + " - Warning: Attempt to set zero or negative score." );
                scoreNode.setText( "" );
            }
        }
    }
}