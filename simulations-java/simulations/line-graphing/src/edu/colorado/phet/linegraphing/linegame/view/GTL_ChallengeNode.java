// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import java.awt.Color;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.linegraphing.common.LGResources.Images;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.EquationNode;
import edu.colorado.phet.linegraphing.linegame.LineGameConstants;
import edu.colorado.phet.linegraphing.linegame.model.GTL_Challenge;
import edu.colorado.phet.linegraphing.linegame.model.LineForm;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.ManipulationMode;
import edu.colorado.phet.linegraphing.linegame.model.PlayState;
import edu.colorado.phet.linegraphing.pointslope.view.PointSlopeEquationNode;
import edu.colorado.phet.linegraphing.slopeintercept.view.SlopeInterceptEquationNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * View for "Graph the Line" (GTL) challenges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GTL_ChallengeNode extends ChallengeNode {

    public GTL_ChallengeNode( final LineGameModel model, final GTL_Challenge challenge, final GameAudioPlayer audioPlayer, final PDimension challengeSize ) {
        super( model, challenge, audioPlayer, challengeSize );

        final PDimension boxSize = new PDimension( 0.35 * challengeSize.getWidth(), 0.2 * challengeSize.getHeight() );

        // The equation for the answer.
        final PNode answerBoxNode = new EquationBoxNode( Strings.LINE_TO_GRAPH, challenge.answer.color, boxSize,
                                                         createEquationNode( challenge.lineForm, challenge.answer, LineGameConstants.STATIC_EQUATION_FONT, challenge.answer.color ) );

        // The equation for the current guess will be a child of this node, to maintain rendering order.
        final PNode guessEquationParent = new PNode();
        guessEquationParent.setVisible( false );

        // icons for indicating correct vs incorrect
        final PNode answerCorrectNode = new PImage( Images.CHECK_MARK );
        final PNode guessCorrectNode = new PImage( Images.CHECK_MARK );
        final PNode guessIncorrectNode = new PImage( Images.X_MARK );

        final GTL_GraphNode graphNode = createGraphNode( challenge );

        // rendering order
        {
            parentNode.addChild( answerBoxNode );
            parentNode.addChild( answerCorrectNode );
            parentNode.addChild( guessEquationParent );
            parentNode.addChild( graphNode );
            parentNode.addChild( answerCorrectNode );
            parentNode.addChild( guessCorrectNode );
            parentNode.addChild( guessIncorrectNode );
        }

        // layout
        final int iconXMargin = 10;
        final int iconYMargin = 5;
        {
            // graphNode is positioned automatically based on mvt's origin offset.

            // equation in left half of challenge space
            answerBoxNode.setOffset( ( challengeSize.getWidth() / 2 ) - answerBoxNode.getFullBoundsReference().getWidth() - 40,
                                     graphNode.getFullBoundsReference().getMinY() + 70 );

            // correct/incorrect icons in upper-right corner of equation boxes
            answerCorrectNode.setOffset( answerBoxNode.getFullBoundsReference().getMaxX() - answerCorrectNode.getFullBoundsReference().getWidth() - iconXMargin,
                                         answerBoxNode.getFullBoundsReference().getMinY() + iconYMargin );

            // face centered below equation boxes
            faceNode.setOffset( answerBoxNode.getFullBoundsReference().getCenterX() - ( faceNode.getFullBoundsReference().getWidth() / 2 ),
                                checkButton.getFullBoundsReference().getMaxY() - faceNode.getFullBoundsReference().getHeight() );
        }

        // Update visibility of the correct/incorrect icons.
        final VoidFunction0 updateIcons = new VoidFunction0() {
            public void apply() {
                answerCorrectNode.setVisible( model.state.get() == PlayState.NEXT );
                guessCorrectNode.setVisible( answerCorrectNode.getVisible() && challenge.isCorrect() );
                guessIncorrectNode.setVisible( answerCorrectNode.getVisible() && !challenge.isCorrect() );
            }
        };

        // Function that keeps the guess equation updated as the user manipulates the line.
        challenge.guess.addObserver( new VoidFunction1<Line>() {
            public void apply( Line line ) {

                // update the equation
                guessEquationParent.removeAllChildren();
                guessEquationParent.addChild( new EquationBoxNode( Strings.YOUR_LINE, line.color, boxSize,
                                                                   createEquationNode( challenge.lineForm, line, LineGameConstants.STATIC_EQUATION_FONT, line.color ) ) );
                guessEquationParent.setOffset( answerBoxNode.getXOffset(), answerBoxNode.getFullBoundsReference().getMaxY() + 20 );

                // keep icons in correct place on box
                guessCorrectNode.setOffset( guessEquationParent.getFullBoundsReference().getMaxX() - guessCorrectNode.getFullBoundsReference().getWidth() - iconXMargin,
                                            guessEquationParent.getFullBoundsReference().getMinY() + iconYMargin );
                guessIncorrectNode.setOffset( guessEquationParent.getFullBoundsReference().getMaxX() - guessIncorrectNode.getFullBoundsReference().getWidth() - iconXMargin,
                                              guessEquationParent.getFullBoundsReference().getMinY() + iconYMargin );

                // make relevant icons visible
                updateIcons.apply();
            }
        } );

        // state changes
        model.state.addObserver( new VoidFunction1<PlayState>() {
            public void apply( PlayState state ) {

                // states in which the graph is interactive
                graphNode.setPickable( state == PlayState.FIRST_CHECK || state == PlayState.SECOND_CHECK || ( state == PlayState.NEXT && !challenge.isCorrect() ) );
                graphNode.setChildrenPickable( graphNode.getPickable() );

                // Show all equations and lines at the end of the challenge.
                guessEquationParent.setVisible( state == PlayState.NEXT );
                graphNode.setAnswerVisible( state == PlayState.NEXT );

                // visibility of correct/incorrect icons
                updateIcons.apply();

                // slope tool visible when user got it wrong
                graphNode.setSlopeToolVisible( state == PlayState.NEXT && !challenge.isCorrect() );
            }
        } );


    }

    // Creates the equation portion of the view.
    private EquationNode createEquationNode( LineForm lineForm, Line line, PhetFont font, Color color ) {
        if ( lineForm == LineForm.SLOPE_INTERCEPT ) {
            return new SlopeInterceptEquationNode( line, font, color );
        }
        else if ( lineForm == LineForm.POINT_SLOPE ) {
            return new PointSlopeEquationNode( line, font, color );
        }
        else {
            throw new IllegalArgumentException( "unsupported line form: " + lineForm );
        }
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
