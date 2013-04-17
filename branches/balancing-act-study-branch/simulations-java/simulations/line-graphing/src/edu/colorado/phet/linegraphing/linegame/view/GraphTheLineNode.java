// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.linegame.LineGameConstants;
import edu.colorado.phet.linegraphing.linegame.model.Challenge;
import edu.colorado.phet.linegraphing.linegame.model.GraphTheLine;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.ManipulationMode;
import edu.colorado.phet.linegraphing.linegame.model.PlayState;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * View for "Graph the Line" challenges.
 * User manipulates a graphed line on the right, equations are displayed on the left.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GraphTheLineNode extends ChallengeNode {

    private static final PNode NOT_A_LINE = new PhetPText( Strings.NOT_A_LINE, new PhetFont( Font.BOLD, 24 ), Color.BLACK );

    protected final ChallengeGraphNode graphNode;
    private EquationBoxNode guessBoxNode;

    /**
     * Constructor
     * @param challenge the challenge
     * @param model the game model
     * @param challengeSize dimensions of the view rectangle that is available for rendering the challenge
     * @param audioPlayer the audio player, for providing audio feedback during game play
     */
    public GraphTheLineNode( final GraphTheLine challenge, final LineGameModel model, final PDimension challengeSize, final GameAudioPlayer audioPlayer ) {
        super( challenge, model, challengeSize, audioPlayer );

        // title, possibly scaled for i18n
        PNode titleNode = new PhetPText( challenge.title, LineGameConstants.TITLE_FONT, LineGameConstants.TITLE_COLOR );
        final double maxTitleWidth = 0.45 * challengeSize.getWidth();
        if ( titleNode.getFullBoundsReference().getWidth() > maxTitleWidth ) {
            titleNode.scale( maxTitleWidth / titleNode.getFullBoundsReference().getWidth() );
        }

        final PDimension boxSize = new PDimension( 0.4 * challengeSize.getWidth(), 0.3 * challengeSize.getHeight() );

        // Answer
        final EquationBoxNode answerBoxNode =
                new EquationBoxNode( Strings.LINE_TO_GRAPH, challenge.answer.color, boxSize,
                                     createEquationNode( challenge.equationForm, challenge.answer, LineGameConstants.STATIC_EQUATION_FONT, challenge.answer.color ) );

        // Guess
        guessBoxNode = new EquationBoxNode( Strings.YOUR_LINE, Color.BLACK, boxSize, new PNode() ); // dummy

        // Graph
        graphNode = createGraphNode( challenge );
        graphNode.setGuessPointVisible( challenge.manipulationMode == ManipulationMode.SLOPE ); // plot the point if we're only manipulating slope

        // rendering order
        subclassParent.addChild( titleNode );
        subclassParent.addChild( graphNode );
        subclassParent.addChild( answerBoxNode );
        subclassParent.addChild( guessBoxNode );

        // layout
        {
            // graphNode is positioned automatically based on mvt's origin offset.

            // equation in left half of challenge space
            answerBoxNode.setOffset( ( challengeSize.getWidth() / 2 ) - answerBoxNode.getFullBoundsReference().getWidth() - 50,
                                     challenge.mvt.modelToViewY( 0 ) - answerBoxNode.getFullBoundsReference().getHeight() - 10 );

            // face centered below equation boxes
            faceNode.setOffset( answerBoxNode.getFullBoundsReference().getCenterX() - ( faceNode.getFullBoundsReference().getWidth() / 2 ),
                                checkButton.getFullBoundsReference().getMaxY() - faceNode.getFullBoundsReference().getHeight() );

            // title above answer equation, left justified
            titleNode.setOffset( answerBoxNode.getFullBoundsReference().getMinX(),
                                 answerBoxNode.getFullBoundsReference().getMinY() - titleNode.getFullBoundsReference().getHeight() - 20 );
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

                // update the equation (line is null if ManipulationMode.THREE_POINTS and points don't make a line)
                subclassParent.removeChild( guessBoxNode );
                PNode equationNode = ( line == null ) ? NOT_A_LINE : createEquationNode( challenge.equationForm, line, LineGameConstants.STATIC_EQUATION_FONT, line.color );
                Color color = ( line == null ) ? LineGameConstants.GUESS_COLOR : line.color;
                guessBoxNode = new EquationBoxNode( Strings.YOUR_LINE, color, boxSize, equationNode );

                // adjust position of guess equation so that it's below the answer
                guessBoxNode.setOffset( answerBoxNode.getXOffset(), challenge.mvt.modelToViewY( 0 ) + 10 );
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

                // Graph the answer line at the end of the challenge.
                graphNode.setAnswerVisible( state == PlayState.NEXT );

                // show stuff when the user got the challenge wrong
                if ( state == PlayState.NEXT && !challenge.isCorrect() ) {
                    guessBoxNode.setVisible( true );
                    graphNode.setAnswerPointVisible( true );
                    graphNode.setGuessPointVisible( true );
                    graphNode.setSlopeToolVisible( true );
                }

                // visibility of correct/incorrect icons
                updateIcons.apply();
            }
        } );
    }

    // Creates the graph portion of the view.
    protected ChallengeGraphNode createGraphNode( Challenge challenge ) {
        if ( challenge.manipulationMode == ManipulationMode.POINT || challenge.manipulationMode == ManipulationMode.SLOPE || challenge.manipulationMode == ManipulationMode.POINT_SLOPE ) {
            return new PointSlopeGraphNode( challenge );
        }
        else if ( challenge.manipulationMode == ManipulationMode.INTERCEPT || challenge.manipulationMode == ManipulationMode.SLOPE_INTERCEPT ) {
            assert( challenge.answer.getYIntercept().isInteger() );
            return new SlopeInterceptGraphNode( challenge );
        }
        else if ( challenge.manipulationMode == ManipulationMode.TWO_POINTS ) {
            return new TwoPointsGraphNode( challenge );
        }
        else {
            throw new IllegalArgumentException( "unsupported manipulationMode: " + challenge.manipulationMode );
        }
    }
}
