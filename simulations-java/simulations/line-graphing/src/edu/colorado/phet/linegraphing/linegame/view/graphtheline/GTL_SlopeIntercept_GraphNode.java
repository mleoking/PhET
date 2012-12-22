// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view.graphtheline;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.LineNode;
import edu.colorado.phet.linegraphing.common.view.PlottedPointNode;
import edu.colorado.phet.linegraphing.common.view.manipulator.LineManipulatorNode;
import edu.colorado.phet.linegraphing.common.view.manipulator.SlopeDragHandler;
import edu.colorado.phet.linegraphing.common.view.manipulator.X1Y1DragHandler;
import edu.colorado.phet.linegraphing.linegame.LineGameConstants;
import edu.colorado.phet.linegraphing.linegame.model.GTL_Challenge;
import edu.colorado.phet.linegraphing.linegame.model.ManipulationMode;
import edu.colorado.phet.linegraphing.slopeintercept.model.SlopeInterceptParameterRange;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Graph for a "Graph the Line" (GTL) challenge that involves manipulating components of a slope-intercept line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class GTL_SlopeIntercept_GraphNode extends GTL_GraphNode {

    private final LineNode answerNode;
    private final LineManipulatorNode slopeManipulatorNode, interceptManipulatorNode;
    private final PNode interceptNode;

    public GTL_SlopeIntercept_GraphNode( final GTL_Challenge challenge ) {
        super( challenge );

        // parent for the guess node, to maintain rendering order
        final PNode guessNodeParent = new PComposite();

        // the correct answer, initially hidden
        answerNode = createLineNode( challenge.answer, challenge.graph, challenge.mvt );
        answerNode.setEquationVisible( false );
        answerNode.setVisible( false );

        // dynamic ranges
        final Property<DoubleRange> riseRange = new Property<DoubleRange>( new DoubleRange( challenge.graph.yRange ) );
        final Property<DoubleRange> y1Range = new Property<DoubleRange>( new DoubleRange( challenge.graph.yRange ) );

        // line manipulators
        final double manipulatorDiameter = challenge.mvt.modelToViewDeltaX( LineGameConstants.MANIPULATOR_DIAMETER );

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
        // intercept as a plotted point
        final double pointDiameter = challenge.mvt.modelToViewDeltaX( LineGameConstants.POINT_DIAMETER );
        interceptNode = new PlottedPointNode( pointDiameter, LGColors.PLOTTED_POINT );

        // Rendering order
        addLineNode( guessNodeParent );
        addLineNode( answerNode );
        if ( challenge.manipulationMode == ManipulationMode.INTERCEPT || challenge.manipulationMode == ManipulationMode.SLOPE_INTERCEPT ) {
            addManipulatorNode( interceptManipulatorNode );
        }
        else {
            addManipulatorNode( interceptNode );
        }
        if ( challenge.manipulationMode == ManipulationMode.SLOPE || challenge.manipulationMode == ManipulationMode.SLOPE_INTERCEPT ) {
            addManipulatorNode( slopeManipulatorNode );
        }

        // Show the user's current guess
        challenge.guess.addObserver( new VoidFunction1<Line>() {
            public void apply( Line line ) {

                // draw the line
                guessNodeParent.removeAllChildren();
                LineNode guessNode = createLineNode( line, challenge.graph, challenge.mvt );
                guessNode.setEquationVisible( false );
                guessNodeParent.addChild( guessNode );

                // move the manipulators
                slopeManipulatorNode.setOffset( challenge.mvt.modelToView( line.x2, line.y2 ) );
                interceptManipulatorNode.setOffset( challenge.mvt.modelToView( line.x1, line.y1 ) );
                interceptNode.setOffset( interceptManipulatorNode.getOffset() );

                // adjust ranges
                SlopeInterceptParameterRange parameterRange = new SlopeInterceptParameterRange();
                riseRange.set( parameterRange.rise( line, challenge.graph ) );
                y1Range.set( parameterRange.y1( line, challenge.graph ) );
            }
        } );
    }

    // Sets the visibility of the correct answer. When answer is visible, manipulators are hidden.
    public void setAnswerVisible( boolean visible ) {
        answerNode.setVisible( visible );
    }
}
