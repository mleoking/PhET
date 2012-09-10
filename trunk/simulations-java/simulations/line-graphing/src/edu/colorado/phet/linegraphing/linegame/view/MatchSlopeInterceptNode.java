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
import edu.colorado.phet.linegraphing.slopeintercept.view.SlopeInterceptLineNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Given an equation in slope-intercept form, graph the matching line by manipulating the slope and intercept.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MatchSlopeInterceptNode extends MatchNode {

    public MatchSlopeInterceptNode( final LineGameModel model, final GameAudioPlayer audioPlayer, PDimension challengeSize ) {
        super( model, audioPlayer, challengeSize );
    }

    @Override public MatchGraphNode createChallengeGraphNode( Graph graph, Property<Line> guessLine, Line answerLine, ModelViewTransform mvt ) {
        return new SlopeInterceptGraphNode( graph, guessLine, answerLine, mvt );
    }

    private static class SlopeInterceptGraphNode extends MatchGraphNode {

        private final SlopeInterceptLineNode answerNode;
        private final LineManipulatorNode slopeManipulatorNode, interceptManipulatorNode;

        public SlopeInterceptGraphNode( final Graph graph,
                                        Property<Line> guessLine,
                                        Line answerLine,
                                        final ModelViewTransform mvt ) {
            super( graph, mvt );

            // parent for the guess node, to maintain rendering order
            final PNode guessNodeParent = new PComposite();

            // the correct answer, initially hidden
            answerNode = new SlopeInterceptLineNode( answerLine.withColor( GameConstants.CORRECT_ANSWER_COLOR ), graph, mvt );
            answerNode.setEquationVisible( false );
            addChild( answerNode );
            answerNode.setVisible( false );

            // ranges
            final Property<DoubleRange> riseRange = new Property<DoubleRange>( new DoubleRange( graph.yRange.getMin(), graph.yRange.getMax() ) );
            final Property<DoubleRange> runRange = new Property<DoubleRange>( new DoubleRange( graph.xRange.getMin(), graph.xRange.getMax() ) );
            final Property<DoubleRange> x1Range = new Property<DoubleRange>( new DoubleRange( 0, 0 ) ); /* x1 is fixed */
            final Property<DoubleRange> y1Range = new Property<DoubleRange>( new DoubleRange( graph.yRange.getMin(), graph.yRange.getMax() ) );

            // line manipulators
            final double manipulatorDiameter = mvt.modelToViewDeltaX( GameConstants.MANIPULATOR_DIAMETER );

            // interactivity for slope manipulator
            slopeManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.SLOPE );
            slopeManipulatorNode.addInputEventListener( new SlopeDragHandler( UserComponents.slopeManipulator, UserComponentTypes.sprite,
                                                                              slopeManipulatorNode, mvt, guessLine, riseRange, runRange ) );

            // interactivity for point (intercept) manipulator
            interceptManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.INTERCEPT );
            interceptManipulatorNode.addInputEventListener( new X1Y1DragHandler( UserComponents.pointManipulator, UserComponentTypes.sprite,
                                                                                 interceptManipulatorNode, mvt, guessLine,
                                                                                 x1Range, y1Range,
                                                                                 true /* constantSlope */ ) );
            // Rendering order
            addChild( guessNodeParent );
            addChild( interceptManipulatorNode );
            addChild( slopeManipulatorNode ); // add slope after intercept, so that slope can be changed when x=0

            // Show the user's current guess
            guessLine.addObserver( new VoidFunction1<Line>() {
                public void apply( Line line ) {

                    // draw the line
                    guessNodeParent.removeAllChildren();
                    LineNode guessNode = new SlopeInterceptLineNode( line, graph, mvt );
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
                    interceptManipulatorNode.setOffset( mvt.modelToView( new Point2D.Double( 0, line.y1 ) ) );

                    //TODO this was copied from LineFormsModel constructor, apply strategy pattern
                    // adjust the rise range
                    final double riseMin = graph.yRange.getMin() - line.y1;
                    final double riseMax = graph.yRange.getMax() - line.y1;
                    riseRange.set( new DoubleRange( riseMin, riseMax ) );

                    //TODO this was copied from LineFormsModel constructor, apply strategy pattern
                    // adjust the y-intercept range
                    final double y1Min = ( line.rise >= 0 ) ? graph.yRange.getMin() : graph.yRange.getMin() - line.rise;
                    final double y1Max = ( line.rise <= 0 ) ? graph.yRange.getMax() : graph.yRange.getMax() - line.rise;
                    y1Range.set( new DoubleRange( y1Min, y1Max ) );
                }
            } );
        }

        // Sets the visibility of the correct answer. When answer is visible, manipulators are hidden.
        public void setAnswerVisible( boolean visible ) {
            answerNode.setVisible( visible );
            slopeManipulatorNode.setVisible( !visible );
            interceptManipulatorNode.setVisible( !visible );
        }
    }
}
