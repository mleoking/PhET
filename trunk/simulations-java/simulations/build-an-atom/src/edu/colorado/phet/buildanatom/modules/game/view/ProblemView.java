package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.Problem;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
* @author Sam Reid
*/
public class ProblemView extends StateView {
    private static final Color FACE_COLOR = new Color( 255, 255, 0, 180 ); // translucent yellow
    private static final Point2D BUTTON_OFFSET = new Point2D.Double(720, 510);
    PText text=new PText( "<debug info for guesses>");
    // TODO: i18n
    private final GameButtonNode checkButton = new GameButtonNode( "Check" );
    private final PText problemNumberDisplay;
    private final PNode resultNode = new PNode( );

    private final Problem problem;
    ProblemView( BuildAnAtomGameModel model, GameCanvas gameCanvas, Problem problem) {
        super( model, problem, gameCanvas );
        this.problem = problem;
        problemNumberDisplay = new PText( "Problem " + (model.getProblemIndex(problem)+1) + " of " + model.getNumberProblems()) {{//todo i18n
            setFont( new PhetFont( 20, true ) );
        }};
        problemNumberDisplay.setOffset( 30, 30 );
    }

    @Override
    public void init() {
        checkButton.centerFullBoundsOnPoint( BUTTON_OFFSET.getX(), BUTTON_OFFSET.getY() );
        checkButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getModel().processGuess();
                text.setText( "num guesses = "+problem.getNumGuesses()+", correctlySolved = "+problem.isSolvedCorrectly());
                resultNode.addChild( new FaceNode( 400 ,FACE_COLOR, new Color( 180,180,180,120), new Color( 180,180,180,120)) {{
                    if ( problem.isSolvedCorrectly() ) {
                        smile();
                    }
                    else {
                        frown();
                        if ( problem.getNumGuesses() == 1 ) {
                            GameButtonNode tryAgainButton = new GameButtonNode( "Try Again" );//todo i18n
                            tryAgainButton.centerFullBoundsOnPoint( BUTTON_OFFSET.getX(), BUTTON_OFFSET.getY() );
                            tryAgainButton.addActionListener( new ActionListener() {
                                public void actionPerformed( ActionEvent e ) {
                                    resultNode.removeAllChildren();
                                    checkButton.setVisible( true );
                                }
                            } );
                            resultNode.addChild( tryAgainButton );
                            checkButton.setVisible( false );
                        }
                        else if ( problem.getNumGuesses() == 2 ) {
                            GameButtonNode showAnswerButton = new GameButtonNode( "Show Answer" );//todo i18n
                            showAnswerButton.centerFullBoundsOnPoint( BUTTON_OFFSET.getX(), BUTTON_OFFSET.getY() );
                            showAnswerButton.addActionListener( new ActionListener() {
                                public void actionPerformed( ActionEvent e ) {
                                    resultNode.removeAllChildren();
                                    GameButtonNode nextProblemButton = new GameButtonNode( "Next Problem" );//todo i18n
                                    nextProblemButton.centerFullBoundsOnPoint( BUTTON_OFFSET.getX(), BUTTON_OFFSET.getY() );
                                    nextProblemButton.addActionListener( new ActionListener() {
                                        public void actionPerformed( ActionEvent e ) {
                                            getModel().nextProblem();
                                        }
                                    } );
                                    resultNode.addChild( nextProblemButton );
                                }
                            } );
                            resultNode.addChild( showAnswerButton );
                            checkButton.setVisible( false );
                        }
                    }
                    setOffset( BuildAnAtomDefaults.STAGE_SIZE.getWidth()/2-getFullBounds().getWidth()/2,BuildAnAtomDefaults.STAGE_SIZE.getHeight()/2-getFullBounds().getHeight()/2 );
                    resultNode.moveToFront();
                }} );
            }
        } );
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
         */
        public GameButtonNode( String label ) {
            super( label, GameCanvas.BUTTONS_FONT_SIZE, GameCanvas.BUTTONS_COLOR );
        }
    }
}
