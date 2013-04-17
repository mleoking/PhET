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
import edu.colorado.phet.linegraphing.common.view.manipulator.SlopeDragHandler;
import edu.colorado.phet.linegraphing.common.view.manipulator.X1Y1DragHandler;
import edu.colorado.phet.linegraphing.linegame.LineGameConstants;
import edu.colorado.phet.linegraphing.linegame.model.Challenge;
import edu.colorado.phet.linegraphing.linegame.model.ManipulationMode;
import edu.colorado.phet.linegraphing.pointslope.model.PointSlopeParameterRange;

/**
 * Challenge graph with manipulators for point (x1,y1) and slope of the guess line.
 * The answer line is initially hidden.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class PointSlopeGraphNode extends ChallengeGraphNode {

    public PointSlopeGraphNode( final Challenge challenge ) {
        super( challenge, true /* slopeToolEnabled */ );

        setGuessVisible( true );

        // dynamic ranges
        final PointSlopeParameterRange pointSlopeParameterRange = new PointSlopeParameterRange();
        final Property<DoubleRange> x1Range = new Property<DoubleRange>( new DoubleRange( challenge.graph.xRange ) );
        final Property<DoubleRange> y1Range = new Property<DoubleRange>( new DoubleRange( challenge.graph.yRange ) );
        final Property<DoubleRange> riseRange = new Property<DoubleRange>( pointSlopeParameterRange.rise( challenge.guess.get(), challenge.graph ) );
        final Property<DoubleRange> runRange = new Property<DoubleRange>( pointSlopeParameterRange.run( challenge.guess.get(), challenge.graph ) );

        final double manipulatorDiameter = challenge.mvt.modelToViewDeltaX( LineGameConstants.MANIPULATOR_DIAMETER );

        // point (x1,y1) manipulator
        final LineManipulatorNode pointManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.POINT_X1_Y1 );
        pointManipulatorNode.addInputEventListener( new X1Y1DragHandler( UserComponents.pointManipulator, UserComponentTypes.sprite,
                                                                         pointManipulatorNode, challenge.mvt, challenge.guess, x1Range, y1Range,
                                                                         true /* constantSlope */ ) );

        // slope manipulator
        final LineManipulatorNode slopeManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.SLOPE );
        slopeManipulatorNode.addInputEventListener( new SlopeDragHandler( UserComponents.slopeManipulator, UserComponentTypes.sprite,
                                                                          slopeManipulatorNode, challenge.mvt, challenge.guess,
                                                                          riseRange, runRange ) );

        // Rendering order
        final boolean pointIsVariable = ( challenge.manipulationMode == ManipulationMode.POINT || challenge.manipulationMode == ManipulationMode.POINT_SLOPE );
        final boolean slopeIsVariable = ( challenge.manipulationMode == ManipulationMode.SLOPE || challenge.manipulationMode == ManipulationMode.POINT_SLOPE );
        if ( pointIsVariable ) {
            addChild( pointManipulatorNode );
        }
        if ( slopeIsVariable ) {
            addChild( slopeManipulatorNode );
        }

        // Sync with the guess
        challenge.guess.addObserver( new VoidFunction1<Line>() {
            public void apply( Line line ) {

                // move the manipulators
                pointManipulatorNode.setOffset( challenge.mvt.modelToView( line.x1, line.y1 ) );
                slopeManipulatorNode.setOffset( challenge.mvt.modelToView( line.x2, line.y2 ) );

                // adjust ranges
                if ( challenge.manipulationMode == ManipulationMode.POINT_SLOPE ) {
                    final PointSlopeParameterRange pointSlopeParameterRange = new PointSlopeParameterRange();
                    x1Range.set( pointSlopeParameterRange.x1( line, challenge.graph ) );
                    y1Range.set( pointSlopeParameterRange.y1( line, challenge.graph ) );
                    riseRange.set( pointSlopeParameterRange.rise( line, challenge.graph ) );
                    runRange.set( pointSlopeParameterRange.run( line, challenge.graph ) );
                }
            }
        } );
    }
}
