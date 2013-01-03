// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.linegraphing.common.LGResources;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.linegame.LineGameConstants;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.P3P_Challenge;
import edu.colorado.phet.linegraphing.linegame.model.PlayState;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * View for "Place 3 Points" (P3P) challenges.
 * User manipulates 3 points on a graph on the right, equations are displayed on the left.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class P3P_ChallengeNode extends ChallengeNode {

    private static final PNode NOT_A_LINE = new PhetPText( Strings.NOT_A_LINE, new PhetFont( Font.BOLD, 24 ), Color.BLACK );

    private EquationBoxNode guessBoxNode;

    public P3P_ChallengeNode( final LineGameModel model, final P3P_Challenge challenge, final GameAudioPlayer audioPlayer, PDimension challengeSize ) {
        super( model, challenge, audioPlayer, challengeSize );

        final PDimension boxSize = new PDimension( 0.35 * challengeSize.getWidth(), 0.2 * challengeSize.getHeight() );

        // Answer
        final EquationBoxNode answerBoxNode =
                new EquationBoxNode( Strings.LINE_TO_GRAPH, challenge.answer.color, boxSize,
                                     createEquationNode( challenge.lineForm, challenge.answer, LineGameConstants.STATIC_EQUATION_FONT, challenge.answer.color ) );

        // Guess
        guessBoxNode = new EquationBoxNode( Strings.YOUR_LINE, Color.BLACK, boxSize, NOT_A_LINE ); // dummy

        // Graph
        final P3P_GraphNode graphNode = new P3P_GraphNode( challenge );

        // rendering order
        {
            subclassParent.addChild( graphNode );
            subclassParent.addChild( answerBoxNode );
            subclassParent.addChild( guessBoxNode );
        }

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

                // update the equation (line is null if points don't make a line)
                subclassParent.removeChild( guessBoxNode );
                PNode equationNode = ( line == null ) ? NOT_A_LINE : createEquationNode( challenge.lineForm, line, LineGameConstants.STATIC_EQUATION_FONT, line.color );
                Color color = ( line == null ) ? LineGameConstants.GUESS_COLOR : line.color;
                guessBoxNode = new EquationBoxNode( Strings.YOUR_LINE, color, boxSize, equationNode );
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

                // visibility of correct/incorrect icons
                updateIcons.apply();
            }
        } );
    }
}
