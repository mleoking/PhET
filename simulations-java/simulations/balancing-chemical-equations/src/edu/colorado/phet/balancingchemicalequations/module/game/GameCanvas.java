// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.module.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import javax.swing.SwingConstants;

import edu.colorado.phet.balancingchemicalequations.BCEConstants;
import edu.colorado.phet.balancingchemicalequations.BCEGlobalProperties;
import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.balancingchemicalequations.module.game.GameModel.GameState;
import edu.colorado.phet.balancingchemicalequations.view.*;
import edu.colorado.phet.balancingchemicalequations.view.game.BalancedNode;
import edu.colorado.phet.balancingchemicalequations.view.game.BalancedNotSimplifiedNode;
import edu.colorado.phet.balancingchemicalequations.view.game.NotBalancedNode;
import edu.colorado.phet.common.games.*;
import edu.colorado.phet.common.games.GameOverNode.GameOverListener;
import edu.colorado.phet.common.games.GameScoreboardNode.GameScoreboardListener;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Canvas for the "Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameCanvas extends BCECanvas {

    private static final Dimension BOX_SIZE = new Dimension( 475, 400 );
    private static final double BOX_SEPARATION = 90;
    private static final Color BUTTONS_COLOR = Color.YELLOW;
    private static final int BUTTONS_FONT_SIZE = 30;

    private final GameModel model;
    private final GameAudioPlayer audioPlayer;

    // top-level nodes
    private final PNode gameSettingsNode;
    private final PNode gamePlayParentNode;
    private GameOverNode gameOverNode;

    // children of problemParentNode, related to interacting with problems
    private final PText equationLabelNode;
    private final EquationNode equationNode;
    private final BoxesNode boxesNode;
    private final ButtonNode checkButton, tryAgainButton, showAnswerButton, nextButton;
    private final GameScoreboardNode scoreboardNode;
    private final PNode balancedNode, balancedNotSimplifiedNode, notBalancedNode;

    public GameCanvas( final GameModel model, final BCEGlobalProperties globalProperties, Resettable resettable ) {
        super( globalProperties.getCanvasColorProperty() );

        this.model = model;
        this.audioPlayer = new GameAudioPlayer( model.getGameSettings().soundEnabled.getValue() );

        HorizontalAligner aligner = new HorizontalAligner( BOX_SIZE, BOX_SEPARATION );

        // Game settings
        VoidFunction0 startFunction = new VoidFunction0() {

            public void apply() {
                model.startGame();
            }
        };
        gameSettingsNode = new PSwing( new GameSettingsPanel( model.getGameSettings(), startFunction ) );
        gameSettingsNode.scale( BCEConstants.SWING_SCALE );

        // Parent node for all nodes visible while the user is working on problems
        gamePlayParentNode = new PhetPNode();

        // Equation label
        equationLabelNode = new PText( "?" );
        equationLabelNode.setTextPaint( Color.BLACK );
        equationLabelNode.setFont( new PhetFont( 20 ) );

        // Equation
        equationNode = new EquationNode( model.getCurrentEquationProperty(), model.getCoefficientsRange(), true, aligner );

        // boxes that show molecules corresponding to the equation coefficients
        boxesNode = new BoxesNode( model.getCurrentEquationProperty(), model.getCoefficientsRange(), aligner, globalProperties.getBoxColorProperty(), globalProperties.getMoleculesVisibleProperty() );

        // buttons
        checkButton = new ButtonNode( BCEStrings.CHECK, BUTTONS_FONT_SIZE, BUTTONS_COLOR );
        checkButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                playGuessAudio();
                model.check();
            }
        } );
        tryAgainButton = new ButtonNode( BCEStrings.TRY_AGAIN, BUTTONS_FONT_SIZE, BUTTONS_COLOR );
        tryAgainButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.tryAgain();
            }
        } );
        showAnswerButton = new ButtonNode( BCEStrings.SHOW_ANSWER, BUTTONS_FONT_SIZE, BUTTONS_COLOR );
        showAnswerButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.showAnswer();
            }
        } );
        nextButton = new ButtonNode( BCEStrings.NEXT, BUTTONS_FONT_SIZE, BUTTONS_COLOR );
        nextButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.next();
            }
        } );

        // Scoreboard
        scoreboardNode = new GameScoreboardNode( model.getGameSettings().level.getMax(), model.getMaxScore(), new DecimalFormat( "0" ) );
        scoreboardNode.setBackgroundWidth( boxesNode.getFullBoundsReference().getWidth() );
        scoreboardNode.addGameScoreboardListener( new GameScoreboardListener() {
            public void newGamePressed() {
                model.newGame();
            }
        } );

        // Balance indicators
        balancedNode = new BalancedNode();
        balancedNotSimplifiedNode = new BalancedNotSimplifiedNode();
        notBalancedNode = new NotBalancedNode( model.getCurrentEquationProperty(), globalProperties.getShowChartsAndScalesInGame() );
        notBalancedNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent event ) {
                /*
                 * Bounds of this node will change when bar charts and balance scales are visible,
                 * as specified by the global property showChartsAndScalesInGame.
                 */
                if ( event.getPropertyName().equals( PNode.PROPERTY_FULL_BOUNDS ) ) {
                    updateGameResultsLayout();
                }
            }
        } );

        // Dev, shows balanced coefficients
        final BalancedEquationNode balancedEquationNode = new BalancedEquationNode( model.getCurrentEquationProperty() );
        if ( globalProperties.isDev() ) {
            addChild( balancedEquationNode );
        }

        // Game results
        gameOverNode = new GameOverNode( 1, 1, 1, new DecimalFormat( "0" ), 1, 1, false, false );//XXX dummy, replaced each time the game is completed

        // rendering order
        addChild( gameSettingsNode );
        addChild( gamePlayParentNode );
        addChild( gameOverNode );
        gamePlayParentNode.addChild( equationLabelNode );
        gamePlayParentNode.addChild( equationNode );
        gamePlayParentNode.addChild( boxesNode );
        gamePlayParentNode.addChild( checkButton );
        gamePlayParentNode.addChild( tryAgainButton );
        gamePlayParentNode.addChild( showAnswerButton );
        gamePlayParentNode.addChild( nextButton );
        gamePlayParentNode.addChild( scoreboardNode );
        gamePlayParentNode.addChild( balancedNode );
        gamePlayParentNode.addChild( balancedNotSimplifiedNode );
        gamePlayParentNode.addChild( notBalancedNode );
        gamePlayParentNode.addChild( balancedEquationNode );

        // layout of children of problemParentNode
        {
            final double ySpacing = 25;
            double x, y;

            equationLabelNode.setOffset( 0, 0 );

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

            // game result indicators, centered between boxes
            updateGameResultsLayout();

            // dev answer below left box
            x = 0;
            y = boxesNode.getFullBoundsReference().getMaxY() + 5;
            balancedEquationNode.setOffset( x, y );
        }

        // layout of top-level nodes
        {
            double x, y;
            gamePlayParentNode.setOffset( 0, 0 );
            x = gamePlayParentNode.getFullBoundsReference().getCenterX() - ( gameSettingsNode.getFullBoundsReference().getWidth() / 2 );
            y = gamePlayParentNode.getFullBoundsReference().getCenterY() - ( gameSettingsNode.getFullBoundsReference().getHeight() / 2 );
            gameSettingsNode.setOffset( x, y );
            x = gamePlayParentNode.getFullBoundsReference().getCenterX() - ( gameOverNode.getFullBoundsReference().getWidth() / 2 );
            x = gamePlayParentNode.getFullBoundsReference().getCenterY() - ( gameOverNode.getFullBoundsReference().getHeight() / 2 );
            gameOverNode.setOffset( x, y );
        }

        // Observers
        {
            globalProperties.getMoleculesVisibleProperty().addObserver( new SimpleObserver() {

                public void update() {
                    // TODO hide molecules in boxes
                }
            } );

            model.addGameStateObserver( new SimpleObserver() {

                public void update() {
                    handleGameStateChange( model.getGameState() );
                }
            } );

            model.getCurrentEquationProperty().addObserver( new SimpleObserver() {

                public void update() {
                    updateEquationLabel();
                }
            } );

            model.getGameSettings().level.addObserver( new SimpleObserver() {

                public void update() {
                    scoreboardNode.setLevel( model.getGameSettings().level.getValue() );
                }
            } );

            model.getGameSettings().timerEnabled.addObserver( new SimpleObserver() {

                public void update() {
                    scoreboardNode.setTimerVisible( model.getGameSettings().timerEnabled.getValue() );
                }
            } );

            model.getGameSettings().soundEnabled.addObserver( new SimpleObserver() {

                public void update() {
                    audioPlayer.setEnabled( model.getGameSettings().soundEnabled.getValue() );
                }
            } );

            model.addPointsObserver( new SimpleObserver() {

                public void update() {
                    scoreboardNode.setScore( model.getPoints() );
                }
            } );

            model.addTimeObserver( new SimpleObserver() {

                public void update() {
                    scoreboardNode.setTime( model.getTime() );
                }
            } );
        }
    }

    private void updateEquationLabel() {
        int index = model.getEquationIndex() + 1;
        int total = model.getNumberOfEquations();
        String s = MessageFormat.format( BCEStrings.EQUATION_0_OF_1, index, total );
        equationLabelNode.setText( s );
    }

    private void handleGameStateChange( GameState prompt ) {
        if ( prompt == GameState.START_GAME ) {
            initStartGame();
        }
        else if ( prompt == GameState.CHECK ) {
            initCheck();
        }
        else if ( prompt == GameState.TRY_AGAIN ) {
            initTryAgain();
        }
        else if ( prompt == GameState.SHOW_ANSWER ) {
            initShowAnswer();
        }
        else if ( prompt == GameState.NEXT ) {
            initNext();
        }
        else if ( prompt == GameState.NEW_GAME ) {
            initNewGame();
        }
        else {
            throw new UnsupportedOperationException( "unsupported GamePrompt: " + prompt );
        }
    }

    public void initStartGame() {
        setTopLevelNodeVisible( gameSettingsNode );
    }

    public void initCheck() {
        setTopLevelNodeVisible( gamePlayParentNode );
        setButtonNodeVisible( checkButton );
        setBalancedIndicatorVisible( false );
        equationNode.setEditable( true );
        setBalancedHighlightEnabled( false );
    }

    public void initTryAgain() {
        setTopLevelNodeVisible( gamePlayParentNode );
        setButtonNodeVisible( tryAgainButton );
        setBalancedIndicatorVisible( true );
        equationNode.setEditable( false );
        setBalancedHighlightEnabled( false );
    }

    public void initShowAnswer() {
        setTopLevelNodeVisible( gamePlayParentNode );
        setButtonNodeVisible( showAnswerButton );
        setBalancedIndicatorVisible( true );
        equationNode.setEditable( false );
        setBalancedHighlightEnabled( false );
    }

    public void initNext() {
        setTopLevelNodeVisible( gamePlayParentNode );
        setButtonNodeVisible( nextButton );
        setBalancedIndicatorVisible( model.getCurrentEquation().isBalancedWithLowestCoefficients() );
        equationNode.setEditable( false );
        model.getCurrentEquation().balance(); // show the correct answer
        setBalancedHighlightEnabled( true );
    }

    public void initNewGame() {
        playGameOverAudio();
        updateGameOverNode();
        setTopLevelNodeVisible( gameOverNode );
    }

    private void setTopLevelNodeVisible( PNode topLevelNode ) {
        // hide all top-level nodes
        gameSettingsNode.setVisible( false );
        gamePlayParentNode.setVisible( false );
        gameOverNode.setVisible( false );
        // make one visible
        topLevelNode.setVisible( true );
    }

    private void setButtonNodeVisible( ButtonNode buttonNode ) {
        // hide all button nodes
        checkButton.setVisible( false );
        tryAgainButton.setVisible( false );
        showAnswerButton.setVisible( false );
        nextButton.setVisible( false );
        // make one visible
        buttonNode.setVisible( true );
    }

    private void setBalancedHighlightEnabled( boolean enabled ) {
        equationNode.setBalancedHighlightEnabled( enabled );
        boxesNode.setBalancedHighlightEnabled( enabled );
    }

    private void setBalancedIndicatorVisible( boolean visible ) {
        balancedNode.setVisible( false );
        balancedNotSimplifiedNode.setVisible( false );
        notBalancedNode.setVisible( false );
        if ( visible ) {
            updateGameResultsLayout();
            if ( model.getCurrentEquation().isBalancedWithLowestCoefficients() ) {
                balancedNode.setVisible( true );
            }
            else if ( model.getCurrentEquation().isBalanced() ) {
                balancedNotSimplifiedNode.setVisible( true );
            }
            else {
                notBalancedNode.setVisible( true );
            }
        }
    }

    private void playGuessAudio() {
        if ( model.getCurrentEquation().isBalancedWithLowestCoefficients() ) {
            audioPlayer.correctAnswer();
        }
        else {
            audioPlayer.wrongAnswer();
        }
    }

    private void playGameOverAudio() {
        if ( model.getPoints() == 0 ) {
            audioPlayer.gameOverZeroScore();
        }
        else if ( model.getPoints() < model.getMaxScore() ) {
            audioPlayer.gameOverImperfectScore();
        }
        else {
            audioPlayer.gameOverPerfectScore();
        }
    }

    private void updateGameOverNode() {

        // remove the old node
        removeChild( gameOverNode );

        // add a new node
        int level = model.getGameSettings().level.getValue();
        gameOverNode = new GameOverNode( level, model.getPoints(), model.getMaxScore(), new DecimalFormat( "0" ), model.getTime(), model.getBestTime( level ), model.isNewBestTime(), model.getGameSettings().timerEnabled.getValue() );
        gameOverNode.scale( BCEConstants.SWING_SCALE );
        addChild( gameOverNode );

        // listen for "New Game" button press
        gameOverNode.addGameOverListener( new GameOverListener() {
            public void newGamePressed() {
                model.newGame();
            }
        } );

        // center
        double x = gamePlayParentNode.getFullBoundsReference().getCenterX() - ( gameOverNode.getFullBoundsReference().getWidth() / 2 );
        double y = gamePlayParentNode.getFullBoundsReference().getCenterY() - ( gameOverNode.getFullBoundsReference().getHeight() / 2 );
        gameOverNode.setOffset( x, y );
    }

    private void updateGameResultsLayout() {

        // centered between boxes
        PNodeLayoutUtils.alignInside( balancedNode, boxesNode, SwingConstants.CENTER, SwingConstants.CENTER );
        PNodeLayoutUtils.alignInside( balancedNotSimplifiedNode, boxesNode, SwingConstants.CENTER, SwingConstants.CENTER );
        PNodeLayoutUtils.alignInside( notBalancedNode, boxesNode, SwingConstants.CENTER, SwingConstants.CENTER );

        // ensure that the most verbose results don't cover the buttons
        if ( notBalancedNode.getFullBoundsReference().getMaxY() >= checkButton.getFullBoundsReference().getMinY() ) {
            PNodeLayoutUtils.alignInside( notBalancedNode, boxesNode, SwingConstants.BOTTOM, SwingConstants.CENTER );
        }
    }
}
