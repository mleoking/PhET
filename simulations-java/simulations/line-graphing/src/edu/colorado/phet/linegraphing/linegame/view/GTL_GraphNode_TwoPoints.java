// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.manipulator.LineManipulatorNode;
import edu.colorado.phet.linegraphing.common.view.manipulator.X1Y1DragHandler;
import edu.colorado.phet.linegraphing.common.view.manipulator.X2Y2DragHandler;
import edu.colorado.phet.linegraphing.linegame.LineGameConstants;
import edu.colorado.phet.linegraphing.linegame.model.GTL_Challenge;

/**
 * Graph for a "Graph the Line" (GTL) challenge that involves manipulating 2 arbitrary points.
 * Note that this graph has no dynamic ranges, because there are no dependencies between the 2 points.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class GTL_GraphNode_TwoPoints extends GTL_GraphNode {

    public GTL_GraphNode_TwoPoints( final GTL_Challenge challenge ) {
        super( challenge );

        final double manipulatorDiameter = challenge.mvt.modelToViewDeltaX( LineGameConstants.MANIPULATOR_DIAMETER );

        // (x1,y1) manipulator
        final LineManipulatorNode x1y1ManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.POINT_X1_Y1 );
        x1y1ManipulatorNode.addInputEventListener( new X1Y1DragHandler( UserComponents.x1y1Manipulator, UserComponentTypes.sprite,
                                                                        x1y1ManipulatorNode, challenge.mvt, challenge.guess,
                                                                        new Property<DoubleRange>( new DoubleRange( challenge.graph.xRange ) ),
                                                                        new Property<DoubleRange>( new DoubleRange( challenge.graph.yRange ) ),
                                                                        false /* constantSlope */ ) );

        // (x2,y2) manipulator
        final LineManipulatorNode x2y2ManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.POINT_X2_Y2 );
        x2y2ManipulatorNode.addInputEventListener( new X2Y2DragHandler( UserComponents.x2y2Manipulator, UserComponentTypes.sprite,
                                                                        x2y2ManipulatorNode, challenge.mvt, challenge.guess,
                                                                        new Property<DoubleRange>( new DoubleRange( challenge.graph.xRange ) ),
                                                                        new Property<DoubleRange>( new DoubleRange( challenge.graph.yRange ) ) ) );

        // Rendering order
        addChild( x1y1ManipulatorNode );
        addChild( x2y2ManipulatorNode );

        // Sync with the guess
        challenge.guess.addObserver( new VoidFunction1<Line>() {
            public void apply( Line line ) {
                // move the manipulators
                x1y1ManipulatorNode.setOffset( challenge.mvt.modelToView( line.x1, line.y1 ) );
                x2y2ManipulatorNode.setOffset( challenge.mvt.modelToView( line.x2, line.y2 ) );
            }
        } );
    }
}
