// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.module.game;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import javax.swing.*;

import edu.colorado.phet.balancingchemicalequations.BCEConstants;
import edu.colorado.phet.balancingchemicalequations.BCEGlobalProperties;
import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.balancingchemicalequations.module.game.GameModel.GameState;
import edu.colorado.phet.balancingchemicalequations.view.*;
import edu.colorado.phet.balancingchemicalequations.view.game.*;
import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.games.GameOverNode;
import edu.colorado.phet.common.games.GameOverNode.GameOverListener;
import edu.colorado.phet.common.games.GameScoreboardNode;
import edu.colorado.phet.common.games.GameScoreboardNode.GameScoreboardListener;
import edu.colorado.phet.common.games.GameSettingsPanel;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Canvas for the "Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */ class GameCanvas extends BCECanvas {

    private static final Dimension BOX_SIZE = new Dimension( 475, 400 );
    private static final double BOX_SEPARATION = 90; // horizontal spacing between boxes
    private static final Color BUTTONS_COLOR = Color.YELLOW;
    private static final PhetFont BUTTONS_FONT = new PhetFont( Font.BOLD, 30 );

    private final GameModel model;
    private final BCEGlobalProperties globalProperties;
    private final GameAudioPlayer audioPlayer;
    private final GameOverListener newGameButtonListener;
    private final HorizontalAligner aligner;

    // top-level nodes
    private final PNode gameSettingsNode;
    private final PNode gamePlayParentNode; // parent of all nodes during game play
    private GameOverNode gameOverNode;
    private final GameRewardNode gameRewardNode;
    private PNode popupNode; // looks like a dialog, tells user how they did

    // children of gamePlayParentNode
    private final PText equationLabelNode; // labels the equation, eg, "Equation 2 of 5"
    private final EquationNode equationNode;
    private final BoxesNode boxesNode;
    private final HTMLImageButtonNode checkButton, tryAgainButton, showAnswerButton, nextButton;
    private final GameScoreboardNode scoreboardNode;

    private final ActionListener showWhyButtonListener, hideWhyButtonListener; // listeners for buttons in "Not Balanced" popups

    /**
     * Constructor
     *
     * @param model
     * @param globalProperties global properties, many of which are accessed via the menu bar
     */
    public GameCanvas( final GameModel model, final BCEGlobalProperties globalProperties ) {
        super( globalProperties.canvasColor );

        this.model = model;
        this.globalProperties = globalProperties;
        this.audioPlayer = new GameAudioPlayer( model.settings.soundEnabled.get() );
        this.aligner = new HorizontalAligner( BOX_SIZE, BOX_SEPARATION );
        this.gameRewardNode = new GameRewardNode();

        // Game settings
        VoidFunction0 startFunction = new VoidFunction0() {
            public void apply() {
                model.startGame();
            }
        };
        gameSettingsNode = new PSwing( new GameSettingsPanel( model.settings, startFunction ) );
        gameSettingsNode.scale( BCEConstants.SWING_SCALE );

        // Parent node for all nodes visible while the user playing a game
        gamePlayParentNode = new PhetPNode();

        // Equation label
        equationLabelNode = new PText( "?" ); // dummy value for layout, gets filled in later
        equationLabelNode.setTextPaint( Color.BLACK );
        equationLabelNode.setFont( new PhetFont( 20 ) );

        // Equation
        equationNode = new EquationNode( model.currentEquation, model.getCoefficientsRange(), aligner );

        // boxes that show molecules corresponding to the equation coefficients
        boxesNode = new BoxesNode( model.currentEquation, model.getCoefficientsRange(), aligner, globalProperties.boxColor, globalProperties.moleculesVisible );

        // buttons
        checkButton = new HTMLImageButtonNode( BCEStrings.CHECK, BUTTONS_FONT, BUTTONS_COLOR );
        checkButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                playGuessAudio();
                model.check();
            }
        } );
        tryAgainButton = new HTMLImageButtonNode( BCEStrings.TRY_AGAIN, BUTTONS_FONT, BUTTONS_COLOR );
        tryAgainButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.tryAgain();
            }
        } );
        showAnswerButton = new HTMLImageButtonNode( BCEStrings.SHOW_ANSWER, BUTTONS_FONT, BUTTONS_COLOR );
        showAnswerButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.showAnswer();
            }
        } );
        nextButton = new HTMLImageButtonNode( BCEStrings.NEXT, BUTTONS_FONT, BUTTONS_COLOR );
        nextButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.next();
            }
        } );

        // Scoreboard
        scoreboardNode = new GameScoreboardNode( model.settings.level.getMax(), model.getPerfectScore(), new DecimalFormat( "0" ) );
        scoreboardNode.setBackgroundWidth( boxesNode.getFullBoundsReference().getWidth() );
        scoreboardNode.addGameScoreboardListener( new GameScoreboardListener() {
            public void newGamePressed() {
                model.newGame();
            }
        } );

        // Shows the answer (dev)
        final DevAnswerNode answerNode = new DevAnswerNode( model.currentEquation );
        answerNode.setVisible( globalProperties.answersVisible.get() );

        // add top-level stuff to world so that we have centering control
        addWorldChild( gameRewardNode );
        addWorldChild( gameSettingsNode );
        addWorldChild( gamePlayParentNode );

        // game play, rendering order
        gamePlayParentNode.addChild( equationLabelNode );
        gamePlayParentNode.addChild( equationNode );
        gamePlayParentNode.addChild( boxesNode );
        gamePlayParentNode.addChild( checkButton );
        gamePlayParentNode.addChild( tryAgainButton );
        gamePlayParentNode.addChild( showAnswerButton );
        gamePlayParentNode.addChild( nextButton );
        gamePlayParentNode.addChild( scoreboardNode );
        gamePlayParentNode.addChild( answerNode );

        // layout: children of gamePlayParentNode
        {
            final double ySpacing = 25;
            double x, y;

            equationLabelNode.setOffset( 0, 0 );

            // equation below the label
            y = equationLabelNode.getFullBoundsReference().getMaxY() + ySpacing;
            equationNode.setOffset( 0, y );

            // boxes below equation
            y = equationNode.getFullBoundsReference().getMaxY() + ySpacing;
            boxesNode.setOffset( 0, y );

            // buttons centered below boxes
            x = boxesNode.getFullBoundsReference().getCenterX() - ( checkButton.getFullBoundsReference().getWidth() / 2 );
            y = boxesNode.getFullBoundsReference().getMaxY() + ySpacing;
            checkButton.setOffset( x, y );
            x = boxesNode.getFullBoundsReference().getCenterX() - ( tryAgainButton.getFullBoundsReference().getWidth() / 2 );
            tryAgainButton.setOffset( x, y );
            x = boxesNode.getFullBoundsReference().getCenterX() - ( showAnswerButton.getFullBoundsReference().getWidth() / 2 );
            showAnswerButton.setOffset( x, y );
            x = boxesNode.getFullBoundsReference().getCenterX() - ( nextButton.getFullBoundsReference().getWidth() / 2 );
            nextButton.setOffset( x, y );

            // scoreboard at bottom
            x = 0;
            y = checkButton.getFullBoundsReference().getMaxY() + ySpacing;
            scoreboardNode.setOffset( x, y );

            // dev answer below left box
            x = 0;
            y = boxesNode.getFullBoundsReference().getMaxY() + 5;
            answerNode.setOffset( x, y );
        }

        // layout: static top-level nodes
        {
            double x, y;
            gamePlayParentNode.setOffset( 0, 0 );
            x = gamePlayParentNode.getFullBoundsReference().getCenterX() - ( gameSettingsNode.getFullBoundsReference().getWidth() / 2 );
            y = gamePlayParentNode.getFullBoundsReference().getCenterY() - ( gameSettingsNode.getFullBoundsReference().getHeight() / 2 );
            gameSettingsNode.setOffset( x, y );
        }

        // listener that will be added/removed to/from dynamic "Game Over" node
        newGameButtonListener = new GameOverListener() {
            public void newGamePressed() {
                model.newGame();
            }
        };

        // Observers
        {
            model.state.addObserver( new SimpleObserver() {
                public void update() {
                    handleGameStateChange( model.state.get() );
                }
            } );

            model.currentEquation.addObserver( new SimpleObserver() {
                public void update() {
                    updateEquationLabel();
                }
            } );

            model.settings.level.addObserver( new SimpleObserver() {
                public void update() {
                    scoreboardNode.setLevel( model.settings.level.get() );
                }
            } );

            model.settings.timerEnabled.addObserver( new SimpleObserver() {
                public void update() {
                    scoreboardNode.setTimerVisible( model.settings.timerEnabled.get() );
                }
            } );

            model.settings.soundEnabled.addObserver( new SimpleObserver() {
                public void update() {
                    audioPlayer.setEnabled( model.settings.soundEnabled.get() );
                }
            } );

            model.points.addObserver( new SimpleObserver() {
                public void update() {
                    scoreboardNode.setScore( model.points.get() );
                }
            } );

            model.timer.time.addObserver( new SimpleObserver() {
                public void update() {
                    scoreboardNode.setTime( model.timer.time.get() );
                }
            } );

            globalProperties.answersVisible.addObserver( new SimpleObserver() {
                public void update() {
                    answerNode.setVisible( globalProperties.answersVisible.get() );
                }
            } );

            showWhyButtonListener = new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    swapPopups( new NotBalancedVerboseNode( model.currentEquation.get(), globalProperties, hideWhyButtonListener, model.getBalancedRepresentation(), aligner ) );
                }
            };
            hideWhyButtonListener = new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    swapPopups( new NotBalancedTerseNode( globalProperties, showWhyButtonListener ) );
                }
            };
        }
    }

    /**
     * Gets the game reward node, so we can play/pause the animation
     * when the associated Module is activated/deactivated.
     */
    public GameRewardNode getRewardNode() {
        return gameRewardNode;
    }

    /*
     * Updates the label on the equation, eg "Equation 2 of 5".
     */
    private void updateEquationLabel() {
        int index = model.getCurrentEquationIndex() + 1;
        int total = model.getNumberOfEquations();
        String s = MessageFormat.format( BCEStrings.EQUATION_0_OF_1, index, total );
        equationLabelNode.setText( s );
    }

    /*
     * Call an initializer to handle setup of the canvas for a specified state.
     * See the javadoc for GameState for the semantics of states and the significance of their names.
     */
    private void handleGameStateChange( GameState state ) {
        if ( state == GameState.START_GAME ) {
            initStartGame();
        }
        else if ( state == GameState.CHECK ) {
            initCheck();
        }
        else if ( state == GameState.TRY_AGAIN ) {
            initTryAgain();
        }
        else if ( state == GameState.SHOW_ANSWER ) {
            initShowAnswer();
        }
        else if ( state == GameState.NEXT ) {
            initNext();
        }
        else if ( state == GameState.NEW_GAME ) {
            initNewGame();
        }
        else {
            throw new UnsupportedOperationException( "unsupported GameState: " + state );
        }
    }

    /*
     * Put the game in the state where the "Start Game" button is visible.
     * This occurs when the "Game Settings" display is visible.
     */
    public void initStartGame() {
        setGameRewardVisible( false );
        setTopLevelNodeVisible( gameSettingsNode );
    }

    /*
     * Put the game in the state where the "Check" button is visible.
     * This occurs during game play.
     */
    public void initCheck() {
        setTopLevelNodeVisible( gamePlayParentNode );
        setButtonNodeVisible( checkButton );
        setPopupVisible( false );
        equationNode.setEditable( true );
        setBalancedHighlightEnabled( false );
    }

    /*
     * Put the game in the state where the "Try Again" button is visible.
     * This occurs during game play.
     */
    public void initTryAgain() {
        setTopLevelNodeVisible( gamePlayParentNode );
        setButtonNodeVisible( tryAgainButton );
        setPopupVisible( true );
        equationNode.setEditable( false );
        setBalancedHighlightEnabled( false );
    }

    /*
     * Put the game in the state where the "Show Answer" button is visible.
     * This occurs during game play.
     */
    public void initShowAnswer() {
        setTopLevelNodeVisible( gamePlayParentNode );
        setButtonNodeVisible( showAnswerButton );
        setPopupVisible( true );
        equationNode.setEditable( false );
        setBalancedHighlightEnabled( false );
    }

    /*
     * Put the game in the state where the "Next" button is visible.
     * This occurs during game play.
     */
    public void initNext() {
        setTopLevelNodeVisible( gamePlayParentNode );
        setButtonNodeVisible( nextButton );
        setPopupVisible( model.currentEquation.get().isBalancedAndSimplified() );
        equationNode.setEditable( false );
        model.currentEquation.get().balance(); // show the correct answer
        setBalancedHighlightEnabled( true );
    }

    /*
     * Put the game in the state where the "New Game" button is visible.
     * This occurs when the game has been completed and the "Game Over" display is visible.
     */
    public void initNewGame() {
        setPopupVisible( false );
        setGameRewardVisible( true );
        playGameOverAudio();
        updateGameOverNode();
        setTopLevelNodeVisible( gameOverNode );
    }

    /*
     * Make the game reward (animation) visible.
     * If visible == true, this creates a new reward for the current game level and score.
     */
    private void setGameRewardVisible( boolean visible ) {
        if ( visible ) {
            gameRewardNode.setLevel( model.settings.level.get(), model.isPerfectScore() );
        }
        gameRewardNode.setVisible( visible );
    }

    /*
     * Makes one of the top-level nodes visible.
     * Visibility of the top-level nodes is mutually exclusive.
     */
    private void setTopLevelNodeVisible( PNode topLevelNode ) {
        // hide all top-level nodes
        gameSettingsNode.setVisible( false );
        gamePlayParentNode.setVisible( false );
        if ( gameOverNode != null ) {
            gameOverNode.setVisible( false );
        }
        // make one visible
        topLevelNode.setVisible( true );
    }

    /*
     * Make one of the buttons visible.
     * Visibility of the buttons is mutually exclusive.
     */
    private void setButtonNodeVisible( HTMLImageButtonNode buttonNode ) {
        // hide all button nodes
        checkButton.setVisible( false );
        tryAgainButton.setVisible( false );
        showAnswerButton.setVisible( false );
        nextButton.setVisible( false );
        // make one visible
        buttonNode.setVisible( true );
    }

    /*
     * Turns on/off the highlighting feature that indicates whether the equation is balanced.
     * We need to be able to control this so that a balanced equation doesn't highlight
     * until after the user presses the Check button.
     */
    private void setBalancedHighlightEnabled( boolean enabled ) {
        equationNode.setBalancedHighlightEnabled( enabled );
        boxesNode.setBalancedHighlightEnabled( enabled );
    }

    /**
     * Controls the visibility of the games results "popup".
     * This tells the user whether their guess is correct or not.
     *
     * @param visible
     */
    private void setPopupVisible( boolean visible ) {
        if ( popupNode != null ) {
            gamePlayParentNode.removeChild( popupNode );
            popupNode = null;
        }
        if ( visible ) {

            // evaluate the user's answer and create the proper type of node
            Equation equation = model.currentEquation.get();
            if ( equation.isBalancedAndSimplified() ) {
                popupNode = new BalancedNode( model.getCurrentPoints(), globalProperties );
            }
            else if ( equation.isBalanced() ) {
                popupNode = new BalancedNotSimplifiedNode( globalProperties );
            }
            else {
                popupNode = new NotBalancedTerseNode( globalProperties, showWhyButtonListener );
            }

            // Layout, ideally centered between the boxes, but guarantee that buttons are not covered.
            PNodeLayoutUtils.alignInside( popupNode, boxesNode, SwingConstants.CENTER, SwingConstants.CENTER );
            if ( popupNode.getFullBoundsReference().getMaxY() >= checkButton.getFullBoundsReference().getMinY() ) {
                PNodeLayoutUtils.alignInside( popupNode, boxesNode, SwingConstants.BOTTOM, SwingConstants.CENTER );
            }

            gamePlayParentNode.addChild( popupNode ); // visible and in front
        }
    }

    /*
     * Replaces the current popup with a new popup.
     * This is used for the "Not Balanced" popup, which has terse and verbose versions.
     * <p>
     * The new popup is positioned so that it has the same top-center as the old popup.
     * As an additional constrain, the new popup is guaranteed to be above the Try Again button,
     * so that that buttons is not obscured by the popup.
     */
    private void swapPopups( PNode newPopupNode ) {

        PNode oldPopupNode = popupNode;
        gamePlayParentNode.removeChild( popupNode );
        popupNode = newPopupNode;

        // align with top-center of old popup
        double x = oldPopupNode.getFullBoundsReference().getCenterX() - ( popupNode.getFullBoundsReference().getWidth() / 2 );
        double y = oldPopupNode.getFullBoundsReference().getMinY();
        popupNode.setOffset( x, y );
        if ( popupNode.getFullBoundsReference().getMaxY() > tryAgainButton.getFullBoundsReference().getMinY() ) {
            y = tryAgainButton.getFullBoundsReference().getMinY() - popupNode.getFullBoundsReference().getHeight() - 5;
            popupNode.setOffset( x, y );
        }
        gamePlayParentNode.addChild( popupNode );
    }

    /*
     * Plays the audio that corresponds to the accuracy of the user's guess.
     */
    private void playGuessAudio() {
        if ( model.currentEquation.get().isBalancedAndSimplified() ) {
            audioPlayer.correctAnswer();
        }
        else {
            audioPlayer.wrongAnswer();
        }
    }

    /*
     * Plays the audio that corresponds to the final results at the end of a game.
     */
    private void playGameOverAudio() {
        if ( model.points.get() == 0 ) {
            audioPlayer.gameOverZeroScore();
        }
        else if ( model.isPerfectScore() ) {
            audioPlayer.gameOverPerfectScore();
        }
        else {
            audioPlayer.gameOverImperfectScore();
        }
    }

    /*
     * Creates the "Game Over" node that show the results at the end of a game.
     * Cleans up any previously-existing node.
     */
    private void updateGameOverNode() {

        // remove the old node
        if ( gameOverNode != null ) {
            removeWorldChild( gameOverNode );
            gameOverNode.removeGameOverListener( newGameButtonListener );
            gameOverNode = null;
        }

        // add a new node
        int level = model.settings.level.get();
        gameOverNode = new GameOverNode( level, model.points.get(), model.getPerfectScore(), new DecimalFormat( "0" ),
                                         model.timer.time.get(), model.getBestTime( level ), model.isNewBestTime(), model.settings.timerEnabled.get() );
        gameOverNode.scale( BCEConstants.SWING_SCALE );
        addWorldChild( gameOverNode );

        // listen for "New Game" button press
        gameOverNode.addGameOverListener( newGameButtonListener );

        // layout, centered
        centerNode( gameOverNode );
    }

    /*
     * Called when the canvas size changes.
     */
    @Override
    protected void updateLayout() {
        super.updateLayout();
        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() > 0 && worldSize.getHeight() > 0 ) {

            // make the reward animation fill the play area
            PBounds newBounds = new PBounds( 0, 0, worldSize.getWidth(), worldSize.getHeight() );
            gameRewardNode.setBounds( newBounds );

            // center top-level nodes
            centerNode( gameSettingsNode );
            centerNode( gameOverNode );
            centerNode( gamePlayParentNode );
        }
    }
}
