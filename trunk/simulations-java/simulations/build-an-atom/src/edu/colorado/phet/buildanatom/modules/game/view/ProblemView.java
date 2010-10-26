package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.modules.game.model.AtomValue;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.Problem;
import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public abstract class ProblemView extends StateView {
    private static final Color FACE_COLOR = new Color( 255, 255, 0, 180 ); // translucent yellow
    private static final Point2D BUTTON_OFFSET = new Point2D.Double( 720, 550 );
    private final PText text = new PText( "<debug info for guesses>" );
    private final GameButtonNode checkButton;
    private final PText problemNumberDisplay;
    private final PNode resultNode = new PNode();
    private final GameAudioPlayer gameAudioPlayer;
    private final Problem problem;

    ProblemView( final BuildAnAtomGameModel model, GameCanvas gameCanvas, final Problem problem ) {
        super( model, problem, gameCanvas );
        this.problem = problem;
        gameAudioPlayer = new GameAudioPlayer( model.getSoundEnabledProperty().getValue() );
        problemNumberDisplay = new PText( "Problem " + ( model.getProblemIndex( problem ) + 1 ) + " of " + model.getNumberProblems() ) {
            {//todo i18n
                setFont( new PhetFont( 20, true ) );
            }
        };
        problemNumberDisplay.setOffset( 30, 30 );
        checkButton = new GameButtonNode( "Check", BUTTON_OFFSET, new ActionListener() {//TODO: i18n
            public void actionPerformed( ActionEvent e ) {
                getModel().processGuess( getGuess() );
                text.setText( "num guesses = " + problem.getNumGuesses() + ", correctlySolved = " + problem.isSolvedCorrectly() );
                resultNode.addChild( new FaceNode( 400, FACE_COLOR, new Color( 180, 180, 180, 120 ), new Color( 180, 180, 180, 120 ) ) {
                    {
                        final FaceNode faceNode = this;
                        if ( problem.isSolvedCorrectly() ) {
                            setGuessEditable( false );
                            smile();
                            addChild( new PText( "+" + problem.getScore() ) {
                                {//TODO: i18n, consider messageformat
                                    setOffset( faceNode.getFullBounds().getWidth() / 2, faceNode.getFullBounds().getHeight() );
                                    setFont( new PhetFont( 24, true ) );
                                }
                            } );
                            gameAudioPlayer.correctAnswer();
                            GameButtonNode nextProblemButton = new GameButtonNode( "Next", BUTTON_OFFSET, new ActionListener() {//TODO: i18n
                                public void actionPerformed( ActionEvent e ) {
                                    getModel().next();
                                    }
                                    } );
                            checkButton.setVisible( false );
                            resultNode.addChild( nextProblemButton );
                        }
                        else {
                            frown();
                            gameAudioPlayer.wrongAnswer();
                            setGuessEditable( false );
                            if ( problem.getNumGuesses() == 1 ) {
                                GameButtonNode tryAgainButton = new GameButtonNode( "Try Again", BUTTON_OFFSET, new ActionListener() {// TODO: i18n
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
                                GameButtonNode showAnswerButton = new GameButtonNode( "Show Answer", BUTTON_OFFSET, new ActionListener() {// TODO: i18n
                                    public void actionPerformed( ActionEvent e ) {
                                        displayAnswer( problem.getAnswer() );
                                        setGuessEditable( false );
                                        resultNode.removeAllChildren();
                                        GameButtonNode nextProblemButton = new GameButtonNode( "Next", BUTTON_OFFSET, new ActionListener() {// TODO: i18n
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
                        setOffset( BuildAnAtomDefaults.STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, BuildAnAtomDefaults.STAGE_SIZE.getHeight() / 2 - getFullBounds().getHeight() / 2 );
                        resultNode.moveToFront();
                        }
                } );
                }
                } );
    }

    /**
     * Applies the user's guess from the view/controller to the model (if it has not done so already).
     */
    protected abstract AtomValue getGuess();

    protected abstract void displayAnswer( AtomValue answer );

    //disable controls during feedback stages
    abstract protected void setGuessEditable( boolean guessEditable );

    @Override
    public void init() {
        getScoreboard().setOffset(
                BuildAnAtomDefaults.STAGE_SIZE.width / 2 - getScoreboard().getFullBoundsReference().width / 2,
                BuildAnAtomDefaults.STAGE_SIZE.height - ( 1.3 * getScoreboard().getFullBoundsReference().height ) );
        addChild( checkButton );
        addChild( getScoreboard() );
        addChild( problemNumberDisplay );
        addChild( text );
        addChild( resultNode );
    }

    @Override
    public void teardown() {
        removeChild( getScoreboard() );
        removeChild( checkButton );
        removeChild( problemNumberDisplay );
        removeChild( text );
        removeChild( resultNode );

    }

    private static class GameButtonNode extends GradientButtonNode {
        /**
         * Constructor.
         *
         * @param centerLocation TODO
         * @param listener       TODO
         */
        public GameButtonNode( String label, Point2D centerLocation, ActionListener listener ) {
            super( label, GameCanvas.BUTTONS_FONT_SIZE, GameCanvas.BUTTONS_COLOR );
            addActionListener( listener );
            centerFullBoundsOnPoint( centerLocation.getX(), centerLocation.getY() );
        }
    }

    public Problem getProblem() {
        return problem;
    }
}
