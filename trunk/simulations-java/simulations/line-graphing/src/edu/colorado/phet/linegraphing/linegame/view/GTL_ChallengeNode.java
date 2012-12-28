// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import java.awt.Color;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.linegame.LineGameConstants;
import edu.colorado.phet.linegraphing.linegame.model.GTL_Challenge;
import edu.colorado.phet.linegraphing.linegame.model.LineForm;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.ManipulationMode;
import edu.colorado.phet.linegraphing.linegame.model.PlayState;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * View for "Graph the Line" (GTL) challenges.
 * User manipulates a graphed line on the right, equations are displayed on the left.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GTL_ChallengeNode extends ChallengeNode {

    private EquationBoxNode guessBoxNode;

    public GTL_ChallengeNode( final LineGameModel model, final GTL_Challenge challenge, final GameAudioPlayer audioPlayer, final PDimension challengeSize ) {
        super( model, challenge, audioPlayer, challengeSize );

        final PDimension boxSize = new PDimension( 0.35 * challengeSize.getWidth(), 0.2 * challengeSize.getHeight() );

        // Answer
        final EquationBoxNode answerBoxNode = new EquationBoxNode( Strings.LINE_TO_GRAPH, challenge.answer.color, boxSize,
                                                                   createEquationNode( challenge.lineForm, challenge.answer, LineGameConstants.STATIC_EQUATION_FONT, challenge.answer.color ) );

        // Guess
        guessBoxNode = new EquationBoxNode( Strings.YOUR_LINE, Color.BLACK, boxSize, new PNode() ); // dummy node

        // Graph
        final GTL_GraphNode graphNode = createGraphNode( challenge );

        // rendering order
        subclassParent.addChild( graphNode );
        subclassParent.addChild( answerBoxNode );
        subclassParent.addChild( guessBoxNode );

        // layout
        {
            // graphNode is positioned automatically based on mvt's origin offset.

            // equation in left half of challenge space
            answerBoxNode.setOffset( ( challengeSize.getWidth() / 2 ) - answerBoxNode.getFullBoundsReference().getWidth() - 40,
                                     graphNode.getFullBoundsReference().getMinY() + 70 );

            // face centered below equation boxes
            faceNode.setOffset( answerBoxNode.getFullBoundsReference().getCenterX() - ( faceNode.getFullBoundsReference().getWidth() / 2 ),
                                checkButton.getFullBoundsReference().getMaxY() - faceNode.getFullBoundsReference().getHeight() );
        }

        // Update visibility of the correct/incorrect icons.
        final VoidFunction0 updateIcons = new VoidFunction0() {
            public void apply() {
                answerBoxNode.setCorrectIconVisible( model.state.get() == PlayState.NEXT );
                guessBoxNode.setCorrectIconVisible( model.state.get() == PlayState.NEXT && challenge.isCorrect() );
                guessBoxNode.setIncorrectIconVisible( model.state.get() == PlayState.NEXT && !challenge.isCorrect() );
            }
        };

        // sync with guess
        challenge.guess.addObserver( new VoidFunction1<Line>() {
            public void apply( Line line ) {

                // update the equation
                subclassParent.removeChild( guessBoxNode );
                guessBoxNode = new EquationBoxNode( Strings.YOUR_LINE, line.color, boxSize,
                                                    createEquationNode( challenge.lineForm, line, LineGameConstants.STATIC_EQUATION_FONT, line.color ) );
                guessBoxNode.setOffset( answerBoxNode.getXOffset(), answerBoxNode.getFullBoundsReference().getMaxY() + 20 );
                subclassParent.addChild( guessBoxNode );
                guessBoxNode.setVisible( model.state.get() == PlayState.NEXT );

                // visibility of correct/incorrect icons
                updateIcons.apply();
            }
        } );

        // sync with game state
        model.state.addObserver( new VoidFunction1<PlayState>() {
            public void apply( PlayState state ) {

                // states in which the graph is interactive
                graphNode.setPickable( state == PlayState.FIRST_CHECK || state == PlayState.SECOND_CHECK || ( state == PlayState.NEXT && !challenge.isCorrect() ) );
                graphNode.setChildrenPickable( graphNode.getPickable() );

                // Show all equations and lines at the end of the challenge.
                guessBoxNode.setVisible( state == PlayState.NEXT );
                graphNode.setAnswerVisible( state == PlayState.NEXT );

                // slope tool visible when user got it wrong
                graphNode.setSlopeToolVisible( state == PlayState.NEXT && !challenge.isCorrect() );

                // visibility of correct/incorrect icons
                updateIcons.apply();
            }
        } );
    }

    // Creates the graph portion of the view.
    protected GTL_GraphNode createGraphNode( GTL_Challenge challenge ) {
        if ( challenge.manipulationMode == ManipulationMode.POINTS ) {
            return new GTL_Points_GraphNode( challenge );
        }
        else if ( challenge.lineForm == LineForm.SLOPE_INTERCEPT ) {
            return new GTL_SlopeIntercept_GraphNode( challenge );
        }
        else if ( challenge.lineForm == LineForm.POINT_SLOPE ) {
            return new GTL_PointSlope_GraphNode( challenge );
        }
        else {
            throw new IllegalArgumentException( "unsupported lineForm (" + challenge.lineForm + ") and manipulatorMode (" + challenge.manipulationMode + ") combination" );
        }
    }
}
