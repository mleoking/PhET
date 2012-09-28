// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view.graphtheline;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.LineManipulatorNode;
import edu.colorado.phet.linegraphing.common.view.LineNode;
import edu.colorado.phet.linegraphing.common.view.SlopeDragHandler;
import edu.colorado.phet.linegraphing.common.view.X1Y1DragHandler;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.graphtheline.GTL_Challenge;
import edu.colorado.phet.linegraphing.linegame.view.GameConstants;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * View component for a "Graph the Line" (GTL) challenge.
 * Given an equation in slope-intercept (SI) form, graph the line by manipulating the Slope and Intercept.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GTL_SI_SlopeIntercept_ChallengeNode extends GTL_SI_ChallengeNode {

    public GTL_SI_SlopeIntercept_ChallengeNode( final LineGameModel model, GTL_Challenge challenge, final GameAudioPlayer audioPlayer, PDimension challengeSize ) {
        super( model, challenge, audioPlayer, challengeSize );
    }

    // Creates the graph portion of the view.
    @Override protected GTL_GraphNode createGraphNode( GTL_Challenge challenge ) {
        return new ThisGraphNode( challenge );
    }

    // Graph for this challenge
    private static class ThisGraphNode extends GTL_SI_GraphNode {

        private final LineNode answerNode;
        private final LineManipulatorNode slopeManipulatorNode, interceptManipulatorNode;

        public ThisGraphNode( final GTL_Challenge challenge ) {
            super( challenge );

            // parent for the guess node, to maintain rendering order
            final PNode guessNodeParent = new PComposite();

            // the correct answer, initially hidden
            answerNode = createAnswerLineNode( challenge.answer, challenge.graph, challenge.mvt );
            answerNode.setEquationVisible( false );
            answerNode.setVisible( false );

            // dynamic ranges
            final Property<DoubleRange> riseRange = new Property<DoubleRange>( new DoubleRange( challenge.graph.yRange ) );
            final Property<DoubleRange> y1Range = new Property<DoubleRange>( new DoubleRange( challenge.graph.yRange ) );

            // line manipulators
            final double manipulatorDiameter = challenge.mvt.modelToViewDeltaX( GameConstants.MANIPULATOR_DIAMETER );

            // slope manipulator
            slopeManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.SLOPE );
            slopeManipulatorNode.addInputEventListener( new SlopeDragHandler( UserComponents.slopeManipulator, UserComponentTypes.sprite,
                                                                              slopeManipulatorNode, challenge.mvt, challenge.guess,
                                                                              riseRange,
                                                                              new Property<DoubleRange>( new DoubleRange( challenge.graph.xRange ) ) ) );

            // point (y intercept) manipulator
            interceptManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.INTERCEPT );
            interceptManipulatorNode.addInputEventListener( new X1Y1DragHandler( UserComponents.pointManipulator, UserComponentTypes.sprite,
                                                                                 interceptManipulatorNode, challenge.mvt, challenge.guess,
                                                                                 new Property<DoubleRange>( new DoubleRange( 0, 0 ) ), /* x1 is fixed */
                                                                                 y1Range,
                                                                                 true /* constantSlope */ ) );
            // Rendering order
            addChild( guessNodeParent );
            addChild( answerNode );
            addChild( slopeManipulatorNode );
            addChild( interceptManipulatorNode );

            // Show the user's current guess
            challenge.guess.addObserver( new VoidFunction1<Line>() {
                public void apply( Line line ) {

                    // draw the line
                    guessNodeParent.removeAllChildren();
                    LineNode guessNode = createGuessLineNode( line, challenge.graph, challenge.mvt );
                    guessNode.setEquationVisible( false );
                    guessNodeParent.addChild( guessNode );

                    // move the manipulators
                    slopeManipulatorNode.setOffset( challenge.mvt.modelToView( line.x2, line.y2 ) );
                    interceptManipulatorNode.setOffset( challenge.mvt.modelToView( line.x1, line.y1 ) );

                    //TODO this was copied from LineFormsModel constructor, apply strategy pattern
                    // adjust the rise range
                    final double riseMin = challenge.graph.yRange.getMin() - line.y1;
                    final double riseMax = challenge.graph.yRange.getMax() - line.y1;
                    riseRange.set( new DoubleRange( riseMin, riseMax ) );

                    //TODO this was copied from LineFormsModel constructor, apply strategy pattern
                    // adjust the y-intercept range
                    final double y1Min = ( line.rise >= 0 ) ? challenge.graph.yRange.getMin() : challenge.graph.yRange.getMin() - line.rise;
                    final double y1Max = ( line.rise <= 0 ) ? challenge.graph.yRange.getMax() : challenge.graph.yRange.getMax() - line.rise;
                    y1Range.set( new DoubleRange( y1Min, y1Max ) );
                }
            } );
        }

        // Sets the visibility of the correct answer. When answer is visible, manipulators are hidden.
        public void setAnswerVisible( boolean visible ) {
            answerNode.setVisible( visible );
        }
    }
}
