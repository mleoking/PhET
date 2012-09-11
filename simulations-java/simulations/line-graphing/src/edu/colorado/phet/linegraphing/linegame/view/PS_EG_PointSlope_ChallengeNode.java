// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.LineManipulatorNode;
import edu.colorado.phet.linegraphing.common.view.LineNode;
import edu.colorado.phet.linegraphing.common.view.SlopeDragHandler;
import edu.colorado.phet.linegraphing.common.view.X1Y1DragHandler;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.pointslope.view.PointSlopeLineNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Given an equation in point-slope form, graph the matching line by manipulating the point and slope.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PS_EG_PointSlope_ChallengeNode extends PS_ChallengeNode {

    public PS_EG_PointSlope_ChallengeNode( final LineGameModel model, final GameAudioPlayer audioPlayer, PDimension challengeSize ) {
        super( model, audioPlayer, challengeSize );
    }

    // Creates the graph portion of the view.
    @Override public ChallengeGraphNode createChallengeGraphNode( Graph graph, Property<Line> guessLine, Line answerLine, ModelViewTransform mvt ) {
        return new ThisGraphNode( graph, guessLine, answerLine, mvt );
    }

    // Graph for this challenge
    private static class ThisGraphNode extends ChallengeGraphNode {

        private final LineNode answerNode;
        private final LineManipulatorNode pointManipulatorNode, slopeManipulatorNode;

        public ThisGraphNode( final Graph graph,
                              Property<Line> guessLine,
                              Line answerLine,
                              final ModelViewTransform mvt ) {
            super( graph, mvt );

            // parent for the guess node, to maintain rendering order
            final PNode guessNodeParent = new PComposite();

            // the correct answer, initially hidden
            answerNode = new PointSlopeLineNode( answerLine.withColor( GameConstants.CORRECT_ANSWER_COLOR ), graph, mvt );
            answerNode.setEquationVisible( false );
            addChild( answerNode );
            answerNode.setVisible( false );

            // ranges
            final Property<DoubleRange> x1Range = new Property<DoubleRange>( new DoubleRange( graph.xRange ) );
            final Property<DoubleRange> y1Range = new Property<DoubleRange>( new DoubleRange( graph.yRange ) );
            final Property<DoubleRange> riseRange = new Property<DoubleRange>( new DoubleRange( graph.yRange ) );
            final Property<DoubleRange> runRange = new Property<DoubleRange>( new DoubleRange( graph.xRange ) );

            // line manipulators
            final double manipulatorDiameter = mvt.modelToViewDeltaX( GameConstants.MANIPULATOR_DIAMETER );

            // point manipulator
            pointManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.POINT_X1_Y1 );
            pointManipulatorNode.addInputEventListener( new X1Y1DragHandler( UserComponents.pointManipulator, UserComponentTypes.sprite,
                                                                             pointManipulatorNode, mvt, guessLine,
                                                                             x1Range, y1Range,
                                                                             true /* constantSlope */ ) );

            // slope manipulator
            slopeManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.SLOPE );
            slopeManipulatorNode.addInputEventListener( new SlopeDragHandler( UserComponents.slopeManipulator, UserComponentTypes.sprite,
                                                                              slopeManipulatorNode, mvt, guessLine, riseRange, runRange ) );

            // Rendering order
            addChild( guessNodeParent );
            addChild( pointManipulatorNode );
            addChild( slopeManipulatorNode ); // add slope after intercept, so that slope can be changed when x=0

            // Show the user's current guess
            guessLine.addObserver( new VoidFunction1<Line>() {
                public void apply( Line line ) {

                    // draw the line
                    guessNodeParent.removeAllChildren();
                    LineNode guessNode = new PointSlopeLineNode( line, graph, mvt );
                    guessNode.setEquationVisible( false );
                    guessNodeParent.addChild( guessNode );

                    // move the manipulators
                    final double y = line.rise + line.y1;
                    double x;
                    if ( line.run == 0 ) {
                        x = 0;
                    }
                    else if ( line.rise == 0 ) {
                        x = line.run;
                    }
                    else {
                        x = line.solveX( y );
                    }
                    slopeManipulatorNode.setOffset( mvt.modelToView( new Point2D.Double( x, y ) ) );
                    pointManipulatorNode.setOffset( mvt.modelToView( new Point2D.Double( line.x1, line.y1 ) ) );

                    // range of the graph axes
                    final int xMin = graph.xRange.getMin();
                    final int xMax = graph.xRange.getMax();
                    final int yMin = graph.yRange.getMin();
                    final int yMax = graph.yRange.getMax();

                    // x1
                    final double x1Min = Math.max( xMin, xMin - line.run );
                    final double x1Max = Math.min( xMax, xMax - line.run );
                    x1Range.set( new DoubleRange( x1Min, x1Max ) );

                    // y1
                    final double y1Min = Math.max( yMin, yMin - line.rise );
                    final double y1Max = Math.min( yMax, yMax - line.rise );
                    y1Range.set( new DoubleRange( y1Min, y1Max ) );

                    // rise
                    final double riseMin = yMin - line.y1;
                    final double riseMax = yMax - line.y1;
                    riseRange.set( new DoubleRange( riseMin, riseMax ) );

                    // run
                    final double runMin = xMin - line.x1;
                    final double runMax = xMax - line.x1;
                    runRange.set( new DoubleRange( runMin, runMax ) );
                }
            } );
        }

        // Sets the visibility of the correct answer. When answer is visible, manipulators are hidden.
        public void setAnswerVisible( boolean visible ) {
            answerNode.setVisible( visible );
            pointManipulatorNode.setVisible( !visible );
            slopeManipulatorNode.setVisible( !visible );
        }
    }
}
