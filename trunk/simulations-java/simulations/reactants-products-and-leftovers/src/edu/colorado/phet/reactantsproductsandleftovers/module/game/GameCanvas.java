// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameChallenge.ChallengeType;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel.GameAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.view.RPALCanvas;
import edu.colorado.phet.reactantsproductsandleftovers.view.RightArrowNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.game.*;
import edu.colorado.phet.reactantsproductsandleftovers.view.realreaction.RealReactionEquationNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Canvas for the "Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameCanvas extends RPALCanvas {

    //---------------------------------------------------------------------------------
    //  Class data
    //---------------------------------------------------------------------------------

    private static final Color BUTTONS_COLOR = new Color( 255, 255, 0, 150 ); // translucent yellow
    private static final PhetFont BUTTONS_FONT = new PhetFont( Font.BOLD, 30 );
    private static final double BUTTON_X_SPACING = 20;

    private static final PDimension BOX_SIZE = RPALConstants.BEFORE_AFTER_BOX_SIZE;

    private static final double FACE_DIAMETER = 150;
    private static final Color FACE_COLOR = new Color( 255, 255, 0, 180 ); // translucent yellow

    // node collection names, for managing visibility
    private static final String GAME_SETTINGS_STATE = "gameSetting";
    private static final String GAME_OVER_STATE = "gameSummary";
    private static final String FIRST_ATTEMPT_STATE = "firstAttempt";
    private static final String FIRST_ATTEMPT_CORRECT_STATE = "firstAttemptCorrect";
    private static final String FIRST_ATTEMPT_WRONG_STATE = "firstAttemptWrong";
    private static final String SECOND_ATTEMPT_STATE = "secondAttempt";
    private static final String SECOND_ATTEMPT_CORRECT_STATE = "secondAttemptCorrect";
    private static final String SECOND_ATTEMPT_WRONG_STATE = "secondAttemptWrong";
    private static final String ANSWER_SHOWN_STATE = "answerShown";

    //---------------------------------------------------------------------------------
    //  Instance data
    //---------------------------------------------------------------------------------

    private final GameModel model;
    private final NodeVisibilityManager visibilityManager;
    private final GameAudioPlayer audioPlayer;

    // static nodes, allocated once, always visible
    private final PhetPNode buttonsParentNode;
    private final RPALScoreboardNode scoreboardNode;
    private final RightArrowNode arrowNode;
    private final ReactionNumberLabelNode reactionNumberLabelNode;

    // static nodes, allocated once, visibility changes
    private final PhetPNode parentNode;
    private final PhetPNode gameSettingsNode;
    private final FaceNode faceNode;
    private final ButtonNode checkButton, nextButton, tryAgainButton, showAnswerButton;
    private final GameMessageNode instructionsNode;
    private final PointsDeltaNode pointsDeltaNode;
    private final GameRewardNode rewardNode;

    // developer nodes, allocated once, always visible
    private final DevAnswerNode devAnswerNode;

    // dynamic nodes, always visible
    private RealReactionEquationNode equationNode; // allocated when reaction changes
    private GameBeforeNode beforeNode; // allocated when reaction changes
    private GameAfterNode afterNode; // allocated when reaction changes
    private RPALGameOverNode gameOverNode; // allocate at end of game

    //---------------------------------------------------------------------------------
    //  Constructors
    //---------------------------------------------------------------------------------

    public GameCanvas( final GameModel model, Resettable resettable ) {
        super();

        audioPlayer = new GameAudioPlayer( model.isSoundEnabled() );

        // reward node
        rewardNode = new GameRewardNode();

        // game settings
        gameSettingsNode = new GameSettingsNode( model );
        gameSettingsNode.scale( 1.5 );

        // all other nodes are children of this node
        parentNode = new PhetPNode();
        addWorldChild( parentNode );
        parentNode.moveToBack();

        // rendering order of top-level nodes
        addWorldChild( rewardNode );
        addWorldChild( parentNode );
        addWorldChild( gameSettingsNode );

        // right-pointing arrow
        arrowNode = new RightArrowNode();
        parentNode.addChild( arrowNode );

        // reaction number label
        reactionNumberLabelNode = new ReactionNumberLabelNode( model );
        parentNode.addChild( reactionNumberLabelNode );

        // scoreboard
        scoreboardNode = new RPALScoreboardNode( model );
        parentNode.addChild( scoreboardNode );

        // face, for indicating correct/incorrect guess
        faceNode = new FaceNode( FACE_DIAMETER, FACE_COLOR );
        parentNode.addChild( faceNode );

        // points awarded
        pointsDeltaNode = new PointsDeltaNode( model );
        parentNode.addChild( pointsDeltaNode );

        // buttons, all under the same parent, to facilitate moving between Before & After boxes
        buttonsParentNode = new PhetPNode();
        parentNode.addChild( buttonsParentNode );
        checkButton = new ButtonNode( RPALStrings.BUTTON_CHECK, BUTTONS_FONT, BUTTONS_COLOR );
        buttonsParentNode.addChild( checkButton );
        nextButton = new ButtonNode( RPALStrings.BUTTON_NEXT, BUTTONS_FONT, BUTTONS_COLOR );
        buttonsParentNode.addChild( nextButton );
        tryAgainButton = new ButtonNode( RPALStrings.BUTTON_TRY_AGAIN, BUTTONS_FONT, BUTTONS_COLOR );
        buttonsParentNode.addChild( tryAgainButton );
        showAnswerButton = new ButtonNode( RPALStrings.BUTTON_SHOW_ANSWER, BUTTONS_FONT, BUTTONS_COLOR );
        buttonsParentNode.addChild( showAnswerButton );

        // instructions
        instructionsNode = new GameMessageNode( "?", Color.YELLOW, 32 ); // text will be set based on challenge type
        parentNode.addChild( instructionsNode );

        // dev nodes
        devAnswerNode = new DevAnswerNode( model );
        if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
            parentNode.addChild( devAnswerNode );
        }

        this.model = model;
        model.addGameListener( new GameAdapter() {

            @Override
            public void newGame() {
                handleNewGame();
            }

            @Override
            public void gameStarted() {
                handleGameStarted();
            }

            @Override
            public void gameCompleted() {
                handleGameCompleted();
            }

            @Override
            public void gameAborted() {
                handleGameAborted();
            }

            @Override
            public void challengeChanged() {
                handleChallengeChanged();
            }

            @Override
            public void guessChanged() {
                handleGuessChanged();
            }

            @Override
            public void soundEnabledChanged() {
                handleSoundEnabledChanged();
            }
        } );

        // when any button's visibility changes, update the layout of the buttons
        PropertyChangeListener buttonVisibilityListener = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                if ( evt.getPropertyName() == PNode.PROPERTY_VISIBLE ) {
                    updateButtonsLayout();
                }
            }
        };

        // Check button checks the user's solution
        checkButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                checkButtonPressed();
            }
        } );
        checkButton.addPropertyChangeListener( buttonVisibilityListener );

        // Next button advanced to the next challenge
        nextButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                nextButtonPressed();
            }
        } );
        nextButton.addPropertyChangeListener( buttonVisibilityListener );

        // Try Again button lets the user make another attempt
        tryAgainButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                tryAgainButtonPressed();
            }
        } );
        tryAgainButton.addPropertyChangeListener( buttonVisibilityListener );

        // Show Answer button shows the correct answer
        showAnswerButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showAnswerButtonPressed();
            }
        } );
        showAnswerButton.addPropertyChangeListener( buttonVisibilityListener );

        // Define which static nodes should be visible at each state of the game.
        PNode[] managedNodes = { parentNode, gameSettingsNode, faceNode, checkButton, nextButton, tryAgainButton, showAnswerButton, instructionsNode, pointsDeltaNode, rewardNode };
        visibilityManager = new NodeVisibilityManager( managedNodes );
        visibilityManager.add( GAME_SETTINGS_STATE, gameSettingsNode );
        visibilityManager.add( FIRST_ATTEMPT_STATE, parentNode, checkButton, instructionsNode );
        visibilityManager.add( FIRST_ATTEMPT_CORRECT_STATE, parentNode, faceNode, nextButton, pointsDeltaNode );
        visibilityManager.add( FIRST_ATTEMPT_WRONG_STATE, parentNode, faceNode, tryAgainButton );
        visibilityManager.add( SECOND_ATTEMPT_STATE, parentNode, checkButton );
        visibilityManager.add( SECOND_ATTEMPT_CORRECT_STATE, parentNode, faceNode, nextButton, pointsDeltaNode );
        visibilityManager.add( SECOND_ATTEMPT_WRONG_STATE, parentNode, faceNode, showAnswerButton );
        visibilityManager.add( ANSWER_SHOWN_STATE, parentNode, nextButton );
        visibilityManager.add( GAME_OVER_STATE, rewardNode );

        // initial state
        updateDynamicNodes();
        updateButtonsLayout();
        visibilityManager.setVisibility( GAME_SETTINGS_STATE );
    }

    //---------------------------------------------------------------------------------
    //  Setters and getters
    //---------------------------------------------------------------------------------

    /**
     * Gets the game reward node, so we can play/pause the animation
     * when the associated Module is activated/deactivated.
     */
    public GameRewardNode getRewardNode() {
        return rewardNode;
    }

    //---------------------------------------------------------------------------------
    //  Handlers for model event notifications
    //---------------------------------------------------------------------------------

    private void handleNewGame() {
        removeGameOverNode();
        visibilityManager.setVisibility( GAME_SETTINGS_STATE );
    }

    private void handleGameStarted() {
        showGuess( true );
        visibilityManager.setVisibility( FIRST_ATTEMPT_STATE );
    }

    private void handleGameCompleted() {
        addGameOverNode();
        rewardNode.setLevel( model.getLevel(), model.isPerfectScore() );
        visibilityManager.setVisibility( GAME_OVER_STATE );
        if ( model.isPerfectScore() ) {
            audioPlayer.gameOverPerfectScore();
        }
        else if ( model.getPoints() == 0 ) {
            audioPlayer.gameOverZeroScore();
        }
        else {
            audioPlayer.gameOverImperfectScore();
        }
    }

    private void addGameOverNode() {
        gameOverNode = new RPALGameOverNode( model );
        gameOverNode.scale( 1.5 );
        addWorldChild( gameOverNode );
        centerNode( gameOverNode );
    }

    private void removeGameOverNode() {
        if ( gameOverNode != null ) {
            removeWorldChild( gameOverNode );
            gameOverNode = null;
        }
    }

    private void handleGameAborted() {
        visibilityManager.setVisibility( GAME_SETTINGS_STATE );
    }

    private void handleChallengeChanged() {
        visibilityManager.setVisibility( FIRST_ATTEMPT_STATE );
        updateDynamicNodes();
        showGuess( true ); // do this after updating dynamic nodes!
    }

    private void handleGuessChanged() {
        instructionsNode.setVisible( false );
    }

    private void handleSoundEnabledChanged() {
        audioPlayer.setEnabled( model.isSoundEnabled() );
    }

    //---------------------------------------------------------------------------------
    //  Handlers for buttons
    //---------------------------------------------------------------------------------

    /*
    * When the "Check" button is pressed, ask the model to evaluate the user's answer.
    * The model handles the awarding of points.
    * All we need to do here is move to the proper state based on whether the
    * answer was correct, and how many attempts the user had made.
    */
    private void checkButtonPressed() {
        showGuess( false );
        boolean correct = model.checkGuess();
        if ( correct ) {
            audioPlayer.correctAnswer();
            faceNode.smile();
            if ( model.getAttempts() == 1 ) {
                visibilityManager.setVisibility( FIRST_ATTEMPT_CORRECT_STATE );
            }
            else {
                visibilityManager.setVisibility( SECOND_ATTEMPT_CORRECT_STATE );
            }
            showAnswerForCorrectGuess();
        }
        else {
            audioPlayer.wrongAnswer();
            faceNode.frown();
            if ( model.getAttempts() == 1 ) {
                visibilityManager.setVisibility( FIRST_ATTEMPT_WRONG_STATE );
            }
            else {
                visibilityManager.setVisibility( SECOND_ATTEMPT_WRONG_STATE );
            }
        }
    }

    private void nextButtonPressed() {
        model.nextChallenge();
    }

    private void tryAgainButtonPressed() {
        showGuess( true );
        visibilityManager.setVisibility( SECOND_ATTEMPT_STATE );
    }

    private void showAnswerButtonPressed() {
        showAnswer();
        visibilityManager.setVisibility( ANSWER_SHOWN_STATE );
    }

    //---------------------------------------------------------------------------------
    //  Visibility of guess and answer
    //---------------------------------------------------------------------------------

    /*
    * Shows the user's guess in the appropriate box, with optionally editable spinners.
    */
    private void showGuess( boolean editable ) {
        GameChallenge challenge = model.getChallenge();
        if ( challenge.getChallengeType() == ChallengeType.AFTER ) {
            afterNode.showGuess( editable );
        }
        else {
            beforeNode.showGuess( editable );
        }
    }

    /*
     * Shows all parts of the correct answer (molecules and numbers) in both boxes.
     */
    private void showAnswer() {
        afterNode.showAnswer();
        beforeNode.showAnswer();
    }

    /*
    * Shows all parts of the "given" answer associated with a correct guess,
    * in case parts of the answer (molecules or numbers) were hidden as part of the challenge.
    */
    private void showAnswerForCorrectGuess() {
        if ( model.getChallenge().getChallengeType() == ChallengeType.AFTER ) {
            beforeNode.showAnswer();
        }
        else {
            afterNode.showAnswer();
        }
    }

    //---------------------------------------------------------------------------------
    //  Updaters
    //---------------------------------------------------------------------------------

    /*
    * Called when the canvas size changes.
    */
    @Override
    protected void updateLayout() {
        super.updateLayout();
        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() > 0 && worldSize.getHeight() > 0 ) {

            // make the reward fill the play area
            PBounds newBounds = new PBounds( 0, 0, worldSize.getWidth(), worldSize.getHeight() );
            rewardNode.setBounds( newBounds );

            // center nodes in the play area
            centerNode( gameSettingsNode );
            centerNode( gameOverNode );
            centerNode( parentNode );
        }
    }

    /*
    * Updates nodes that are "dynamic".
    * Dynamic nodes are replaced when the challenge changes.
    */
    private void updateDynamicNodes() {

        if ( equationNode != null ) {
            equationNode.cleanup();
            parentNode.removeChild( equationNode );
        }
        equationNode = new RealReactionEquationNode( model.getChallenge().getReaction() );
        parentNode.addChild( equationNode );

        if ( beforeNode != null ) {
            beforeNode.cleanup();
            parentNode.removeChild( beforeNode );
        }
        beforeNode = new GameBeforeNode( model, BOX_SIZE );
        parentNode.addChild( beforeNode );

        if ( afterNode != null ) {
            afterNode.cleanup();
            parentNode.removeChild( afterNode );
        }
        afterNode = new GameAfterNode( model, BOX_SIZE );
        parentNode.addChild( afterNode );

        // move a bunch of static nodes to the front
        devAnswerNode.moveToFront();
        buttonsParentNode.moveToFront();
        faceNode.moveToFront();
        pointsDeltaNode.moveToFront();
        instructionsNode.moveToFront();

        updateNodesLayout();
    }

    /*
     * Updates the layout of all nodes.
     */
    private void updateNodesLayout() {

        // reaction number label in upper right
        double x = 0;
        double y = 7;  // eyeballed this to align it with baseline of equationNode, which is HTML
        reactionNumberLabelNode.setOffset( x, y );

        // equation to right of label, vertically centered
        x = reactionNumberLabelNode.getFullBoundsReference().getWidth() + 35;
        y = 0;
        equationNode.setOffset( x, y );

        // Before box below reaction number label, left justified
        x = reactionNumberLabelNode.getFullBoundsReference().getMinX();
        y = reactionNumberLabelNode.getFullBoundsReference().getMaxY() - PNodeLayoutUtils.getOriginYOffset( beforeNode ) + 40;
        beforeNode.setOffset( x, y );

        // arrow to the right of Before box, vertically centered with box
        final double arrowXSpacing = 20;
        x = beforeNode.getFullBoundsReference().getMaxX() + arrowXSpacing;
        y = beforeNode.getYOffset() + ( BOX_SIZE.getHeight() / 2 );
        arrowNode.setOffset( x, y );

        // After box to the right of arrow, top aligned with Before box
        x = arrowNode.getFullBoundsReference().getMaxX() + arrowXSpacing;
        y = beforeNode.getYOffset();
        afterNode.setOffset( x, y );

        // scoreboard, at bottom left of play area
        scoreboardNode.setBackgroundWidth( afterNode.getFullBoundsReference().getMaxX() - beforeNode.getFullBoundsReference().getMinX() );
        x = beforeNode.getXOffset();
        y = Math.max( beforeNode.getFullBoundsReference().getMaxY(), afterNode.getFullBoundsReference().getMaxY() ) + 20;
        scoreboardNode.setOffset( x, y );

        // face a little above center in proper box
        {
            Point2D offset = null;
            if ( model.getChallenge().getChallengeType() == ChallengeType.AFTER ) {
                offset = afterNode.getOffset();
            }
            else {
                offset = beforeNode.getOffset();
            }
            x = offset.getX() + ( ( BOX_SIZE.getWidth() - faceNode.getFullBoundsReference().getWidth() ) / 2 );
            y = offset.getY() + ( ( BOX_SIZE.getHeight() - faceNode.getFullBoundsReference().getHeight() ) / 2 ) - 20;
            faceNode.setOffset( x, y );
        }

        // points awarded, to right of face
        x = faceNode.getFullBoundsReference().getMaxX() + 15;
        y = faceNode.getFullBoundsReference().getCenterY() - ( pointsDeltaNode.getFullBoundsReference().getHeight() / 2 );
        pointsDeltaNode.setOffset( x, y );

        // instructions centered in proper box
        {
            Point2D offset = null;
            if ( model.getChallenge().getChallengeType() == ChallengeType.AFTER ) {
                instructionsNode.setText( RPALStrings.QUESTION_HOW_MANY_PRODUCTS_AND_LEFTOVERS );
                offset = afterNode.getOffset();
            }
            else {
                instructionsNode.setText( RPALStrings.QUESTION_HOW_MANY_REACTANTS );
                offset = beforeNode.getOffset();
            }
            x = offset.getX() + ( ( BOX_SIZE.getWidth() - instructionsNode.getFullBoundsReference().getWidth() ) / 2 );
            y = offset.getY() + ( ( BOX_SIZE.getHeight() - instructionsNode.getFullBoundsReference().getHeight() ) / 2 ) - 20;
            instructionsNode.setOffset( x, y );
        }

        // dev answer centered below scoreboard
        x = scoreboardNode.getFullBoundsReference().getCenterX() - ( devAnswerNode.getFullBoundsReference().getWidth() / 2 );
        y = scoreboardNode.getFullBoundsReference().getMaxY() + 2;
        devAnswerNode.setOffset( x, y );

        // game settings, horizontally and vertically centered on everything else
        x = parentNode.getFullBoundsReference().getCenterX() - ( gameSettingsNode.getFullBoundsReference().getWidth() / 2 );
        y = parentNode.getFullBoundsReference().getCenterY() - ( gameSettingsNode.getFullBoundsReference().getHeight() / 2 );
        gameSettingsNode.setOffset( x, y );
    }

    /*
    * Updates the layout of the buttons that appear in the Before/After box.
    * These buttons are organized in the scenegraph below a common parent node.
    * So we can layout the buttons, then position the parent.
    */
    private void updateButtonsLayout() {

        // arrange all visible buttons in a row
        double x = 0;
        double y = 0;
        double buttonMaxX = 0;
        double buttonMaxY = 0;
        for ( int i = 0; i < buttonsParentNode.getChildrenCount(); i++ ) {
            PNode child = buttonsParentNode.getChild( i );
            if ( child.getVisible() ) {
                child.setOffset( x, y );
                x += child.getFullBoundsReference().getWidth() + BUTTON_X_SPACING;
                buttonMaxX = child.getFullBoundsReference().getMaxX();
                buttonMaxY = child.getFullBoundsReference().getMaxY();
            }
        }

        // put visible buttons at bottom center of the proper box
        Point2D offset = null;
        if ( model.getChallenge().getChallengeType() == ChallengeType.AFTER ) {
            offset = afterNode.getOffset();
        }
        else {
            offset = beforeNode.getOffset();
        }
        x = offset.getX() + ( ( BOX_SIZE.getWidth() - buttonMaxX ) / 2 );
        y = offset.getY() + BOX_SIZE.getHeight() - buttonMaxY - 10;
        buttonsParentNode.setOffset( x, y );
    }
}
