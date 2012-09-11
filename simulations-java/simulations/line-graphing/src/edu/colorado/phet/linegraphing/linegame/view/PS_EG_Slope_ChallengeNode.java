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
import edu.colorado.phet.linegraphing.common.view.PlottedPointNode;
import edu.colorado.phet.linegraphing.common.view.SlopeDragHandler;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.pointslope.view.PointSlopeLineNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Given an equation in point-slope form, graph the matching line by manipulating the slope.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PS_EG_Slope_ChallengeNode extends PS_ChallengeNode {

    public PS_EG_Slope_ChallengeNode( final LineGameModel model, final GameAudioPlayer audioPlayer, PDimension challengeSize ) {
        super( model, audioPlayer, challengeSize );
    }

    // Creates the graph portion of the view.
    @Override public ChallengeGraphNode createChallengeGraphNode( Graph graph, Property<Line> guessLine, Line answerLine, ModelViewTransform mvt ) {
        return new SlopeGraphNode( graph, guessLine, answerLine, mvt );
    }

    // Graph for this challenge
    private static class SlopeGraphNode extends ChallengeGraphNode {

        private final LineNode answerNode;
        private final LineManipulatorNode slopeManipulatorNode;
        private final PNode pointNode;

        public SlopeGraphNode( final Graph graph,
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

            // plotted point
            final double pointDiameter = mvt.modelToViewDeltaX( GameConstants.POINT_DIAMETER );
            pointNode = new PlottedPointNode( pointDiameter, LGColors.PLOTTED_POINT );
            pointNode.setOffset( mvt.modelToView( new Point2D.Double( guessLine.get().x1, guessLine.get().y1 ) ) );

            // ranges
            final Property<DoubleRange> riseRange = new Property<DoubleRange>( new DoubleRange( graph.yRange ) );
            final Property<DoubleRange> runRange = new Property<DoubleRange>( new DoubleRange( graph.xRange ) );

            // interactivity for slope manipulator
            final double manipulatorDiameter = mvt.modelToViewDeltaX( GameConstants.MANIPULATOR_DIAMETER );
            slopeManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.SLOPE );
            slopeManipulatorNode.addInputEventListener( new SlopeDragHandler( UserComponents.slopeManipulator, UserComponentTypes.sprite,
                                                                              slopeManipulatorNode, mvt, guessLine, riseRange, runRange ) );
            // Rendering order
            addChild( guessNodeParent );
            addChild( pointNode );
            addChild( slopeManipulatorNode ); // add slope after intercept, so that slope can be changed when x=0

            // Show the user's current guess
            guessLine.addObserver( new VoidFunction1<Line>() {
                public void apply( Line line ) {

                    // draw the line
                    guessNodeParent.removeAllChildren();
                    LineNode guessNode = new PointSlopeLineNode( line, graph, mvt );
                    guessNode.setEquationVisible( false );
                    guessNodeParent.addChild( guessNode );

                    // move the manipulator
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

                    //TODO this was copied from LineFormsModel constructor
                    // adjust the rise range
                    final double riseMin = graph.yRange.getMin() - line.y1;
                    final double riseMax = graph.yRange.getMax() - line.y1;
                    riseRange.set( new DoubleRange( riseMin, riseMax ) );
                }
            } );
        }

        // Sets the visibility of the correct answer. When answer is visible, manipulators are hidden.
        public void setAnswerVisible( boolean visible ) {
            answerNode.setVisible( visible );
            slopeManipulatorNode.setVisible( !visible );
        }
    }
}
