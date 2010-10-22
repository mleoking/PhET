package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    PText text=new PText( "<debug info for guesses>");
    // TODO: i18n
    private final GradientButtonNode checkButton = new GradientButtonNode( "Check", GameCanvas.BUTTONS_FONT_SIZE, GameCanvas.BUTTONS_COLOR );//todo i18n
    private final PText problemNumberDisplay;
    private PNode resultNode = new PNode( );

    private Problem problem;
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
        checkButton.setOffset( 700, 500 );
        checkButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getModel().processGuess();
                text.setText( "num guesses = "+problem.getNumGuesses()+", correctlySolved = "+problem.isSolvedCorrectly());
                resultNode.addChild( new FaceNode( 300 ) {{
                    if ( problem.isSolvedCorrectly() ) {
                        smile();
                    }
                    else {
                        frown();
                        if ( problem.getNumGuesses() == 1 ) {
                            GradientButtonNode tryAgainButton = new GradientButtonNode( "Try again" );//todo i18n
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
                            GradientButtonNode showAnswerButton = new GradientButtonNode( "Show answer" );//todo i18n
                            showAnswerButton.addActionListener( new ActionListener() {
                                public void actionPerformed( ActionEvent e ) {
                                    resultNode.removeAllChildren();
                                    GradientButtonNode nextProblemButton = new GradientButtonNode( "Next Problem" );//todo i18n
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
}
