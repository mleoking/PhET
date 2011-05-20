// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.text.MessageFormat;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.BuildAnAtomClock;
import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.Problem;
import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Abstract base class for Piccolo node that depicts a problem for a specific
 * game Problem.  Each ProblemView instance is expected to be associated with
 * a Problem instance.  This contains the logic that controls the flow of the
 * game, e.g. what happens when the user enters a correct answer, or an
 * incorrect answer, etc.
 *
 * @author Sam Reid
 */
public abstract class ProblemView extends StateView {
    private static final Color FACE_COLOR = new Color( 255, 255, 0, 180 ); // translucent yellow
    private static final Point2D BUTTON_OFFSET = new Point2D.Double( 720, 550 );

    // Transform used by all problem views that present a schematic on the
    // left side.
    public static final ModelViewTransform SCHEMATIC_PROBLEM_MVT = ModelViewTransform.createSinglePointScaleInvertedYMapping(
            new Point2D.Double( 0, 0 ),
            new Point( (int) Math.round( BuildAnAtomDefaults.STAGE_SIZE.width * 0.27 ), (int) Math.round( BuildAnAtomDefaults.STAGE_SIZE.height * 0.45 ) ),
            2.0 );

    private final GameButtonNode checkButton;
    private final PText problemNumberDisplay;
    private final PNode resultNode = new PNode();
    private final GameAudioPlayer gameAudioPlayer;
    private final Problem problem;
    private final BuildAnAtomClock clock = new BuildAnAtomClock();

    /**
     * Constructor.  Sets up the user interface for the portions that are
     * common to all problem views.
     */
    ProblemView( final BuildAnAtomGameModel model, BuildAnAtomGameCanvas gameCanvas, final Problem problem ) {
        super( model, problem, gameCanvas );
        this.problem = problem;
        gameAudioPlayer = new GameAudioPlayer( model.isSoundEnabled() );
        final int problemIndex = model.getProblemIndex( problem ) + 1;
        final int maxProblems = model.getNumberProblems();
        problemNumberDisplay = new PText( MessageFormat.format( BuildAnAtomStrings.GAME_PROBLEM_INDEX_READOUT_PATTERN, problemIndex, maxProblems ) ) {
            {
                setFont( new PhetFont( 20, true ) );
            }
        };
        problemNumberDisplay.setOffset( 30, 30 );
        checkButton = new GameButtonNode( BuildAnAtomStrings.GAME_CHECK, BUTTON_OFFSET, new ActionListener() {
            // Process the user's guess.
            public void actionPerformed( ActionEvent e ) {
                getModel().processGuess( getGuess() );
                final FaceNode faceNode = new FaceNode( 400, FACE_COLOR, new Color( 180, 180, 180, 120 ), new Color( 180, 180, 180, 120 ) );
                faceNode.setOffset( BuildAnAtomDefaults.STAGE_SIZE.getWidth() / 2 - faceNode.getFullBounds().getWidth() / 2,
                                    BuildAnAtomDefaults.STAGE_SIZE.getHeight() / 2 - faceNode.getFullBounds().getHeight() / 2 );
                resultNode.addChild( faceNode );

                if ( problem.isSolvedCorrectly() ) {
                    // The user answered correctly, so show the happy face and
                    // score change, play sound (if enabled) and get set up for
                    // the next problem.
                    setGuessEditable( false );
                    faceNode.smile();
                    faceNode.addChild( new PText( "+" + problem.getScore() ) {{
                        setOffset( faceNode.getFullBounds().getWidth() / 2, faceNode.getFullBounds().getHeight() );
                        setFont( new PhetFont( 24, true ) );
                    }} );
                    gameAudioPlayer.correctAnswer();
                    GameButtonNode nextProblemButton = new GameButtonNode( BuildAnAtomStrings.GAME_NEXT, BUTTON_OFFSET, new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            getModel().next();
                        }
                    } );
                    checkButton.setVisible( false );
                    resultNode.addChild( nextProblemButton );
                }
                else {
                    // The user answered incorrectly.  Show the frowny face
                    // and play the unhappy sound (if enabled).
                    faceNode.frown();
                    gameAudioPlayer.wrongAnswer();
                    setGuessEditable( false );
                    if ( problem.getNumGuesses() == 1 ) {
                        // Give the user another chance.
                        GameButtonNode tryAgainButton = new GameButtonNode( BuildAnAtomStrings.GAME_TRY_AGAIN, BUTTON_OFFSET, new ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                                resultNode.removeAllChildren();
                                checkButton.setVisible( true );
                                setGuessEditable( true );
                            }
                        } );
                        resultNode.addChild( tryAgainButton );
                        checkButton.setVisible( false );
                    }
                    else if ( problem.getNumGuesses() == 2 ) {
                        // The user has exhausted their guesses, so give them
                        // the opportunity to show the correct answer.
                        GameButtonNode showAnswerButton = new GameButtonNode( BuildAnAtomStrings.GAME_SHOW_ANSWER, BUTTON_OFFSET, new ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                                displayAnswer( problem.getAnswer() );
                                setGuessEditable( false );
                                resultNode.removeAllChildren();
                                GameButtonNode nextProblemButton = new GameButtonNode( BuildAnAtomStrings.GAME_NEXT, BUTTON_OFFSET, new ActionListener() {
                                    public void actionPerformed( ActionEvent e ) {
                                        getModel().next();
                                    }
                                } );
                                resultNode.addChild( nextProblemButton );
                            }
                        } );
                        resultNode.addChild( showAnswerButton );
                        checkButton.setVisible( false );
                    }
                }
                resultNode.moveToFront();
            }
        } );
        checkButton.setEnabled( false );
    }

    public BuildAnAtomClock getClock() {
        return clock;
    }

    public void enableCheckButton() {
        checkButton.setEnabled( true );
    }

    /**
     * Get the guess that the user has submitted.
     *
     * @return the guessed value
     */
    protected abstract ImmutableAtom getGuess();

    /**
     * Display the answer to the user, generally called when the user has
     * exhausted their guesses and not gotten the correct answer.
     *
     * @param answer
     */
    protected abstract void displayAnswer( ImmutableAtom answer );

    //disable controls during feedback stages
    abstract protected void setGuessEditable( boolean guessEditable );

    public Problem getProblem() {
        return problem;
    }

    @Override
    public void init() {
        getScoreboard().setOffset(
                BuildAnAtomDefaults.STAGE_SIZE.width / 2 - getScoreboard().getFullBoundsReference().width / 2,
                BuildAnAtomDefaults.STAGE_SIZE.height - ( 1.3 * getScoreboard().getFullBoundsReference().height ) );
        addChild( checkButton );
        addChild( getScoreboard() );
        addChild( problemNumberDisplay );
        addChild( resultNode );
        clock.start();
    }

    @Override
    public void teardown() {
        removeChild( getScoreboard() );
        removeChild( checkButton );
        removeChild( problemNumberDisplay );
        removeChild( resultNode );
        clock.stop();
    }

    /**
     * Convenience class for centering the game button and hooking up its
     * action listener.
     */
    private static class GameButtonNode extends ButtonNode {
        /**
         * Constructor.
         *
         * @param centerLocation location on which to center the node
         * @param listener       callback when the button is pressed
         */
        public GameButtonNode( String label, Point2D centerLocation, ActionListener listener ) {
            super( label, new PhetFont( Font.BOLD, BuildAnAtomGameCanvas.BUTTONS_FONT_SIZE ), BuildAnAtomGameCanvas.BUTTONS_COLOR );
            addActionListener( listener );
            centerFullBoundsOnPoint( centerLocation.getX(), centerLocation.getY() );
        }
    }
}
