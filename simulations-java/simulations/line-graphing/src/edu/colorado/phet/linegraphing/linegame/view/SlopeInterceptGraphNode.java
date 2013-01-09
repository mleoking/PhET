// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.PlottedPointNode;
import edu.colorado.phet.linegraphing.common.view.manipulator.LineManipulatorNode;
import edu.colorado.phet.linegraphing.common.view.manipulator.SlopeDragHandler;
import edu.colorado.phet.linegraphing.common.view.manipulator.X1Y1DragHandler;
import edu.colorado.phet.linegraphing.linegame.LineGameConstants;
import edu.colorado.phet.linegraphing.linegame.model.Challenge;
import edu.colorado.phet.linegraphing.linegame.model.ManipulationMode;
import edu.colorado.phet.linegraphing.slopeintercept.model.SlopeInterceptParameterRange;
import edu.umd.cs.piccolo.PNode;

/**
 * Challenge graph with manipulators for slope and y-intercept of the guess line.
 * The answer line is initially hidden.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class SlopeInterceptGraphNode extends ChallengeGraphNode {

    public SlopeInterceptGraphNode( final Challenge challenge ) {
        super( challenge, true /* slopeToolEnabled */ );

        setAnswerVisible( false );
        setGuessVisible( true );

        // dynamic ranges
        SlopeInterceptParameterRange parameterRange = new SlopeInterceptParameterRange();
        final Property<DoubleRange> riseRange = new Property<DoubleRange>( parameterRange.rise( challenge.guess.get(), challenge.graph ) );
        final Property<DoubleRange> runRange = new Property<DoubleRange>( parameterRange.run( challenge.guess.get(), challenge.graph ) );
        final Property<DoubleRange> y1Range = new Property<DoubleRange>( new DoubleRange( challenge.graph.yRange ) );

        final double manipulatorDiameter = challenge.mvt.modelToViewDeltaX( LineGameConstants.MANIPULATOR_DIAMETER );

        // slope manipulator
        final LineManipulatorNode slopeManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.SLOPE );
        slopeManipulatorNode.addInputEventListener( new SlopeDragHandler( UserComponents.slopeManipulator, UserComponentTypes.sprite,
                                                                          slopeManipulatorNode, challenge.mvt, challenge.guess,
                                                                          riseRange, runRange ) );

        // y-intercept manipulator
        final LineManipulatorNode interceptManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.INTERCEPT );
        interceptManipulatorNode.addInputEventListener( new X1Y1DragHandler( UserComponents.pointManipulator, UserComponentTypes.sprite,
                                                                             interceptManipulatorNode, challenge.mvt, challenge.guess,
                                                                             new Property<DoubleRange>( new DoubleRange( 0, 0 ) ), /* x1 is fixed */
                                                                             y1Range,
                                                                             true /* constantSlope */ ) );
        // plotted y-intercept
        final double pointDiameter = challenge.mvt.modelToViewDeltaX( LineGameConstants.POINT_DIAMETER );
        final PNode interceptNode = new PlottedPointNode( pointDiameter, LGColors.PLOTTED_POINT );

        // Rendering order
        if ( challenge.manipulationMode == ManipulationMode.INTERCEPT || challenge.manipulationMode == ManipulationMode.SLOPE_INTERCEPT ) {
            addChild( interceptManipulatorNode );
        }
        else {
            addChild( interceptNode );
        }
        if ( challenge.manipulationMode == ManipulationMode.SLOPE || challenge.manipulationMode == ManipulationMode.SLOPE_INTERCEPT ) {
            addChild( slopeManipulatorNode );
        }

        // Sync with the guess
        challenge.guess.addObserver( new VoidFunction1<Line>() {
            public void apply( Line line ) {

                // move the manipulators
                slopeManipulatorNode.setOffset( challenge.mvt.modelToView( line.x2, line.y2 ) );
                interceptManipulatorNode.setOffset( challenge.mvt.modelToView( line.x1, line.y1 ) );
                interceptNode.setOffset( interceptManipulatorNode.getOffset() );

                // adjust ranges
                if ( challenge.manipulationMode == ManipulationMode.SLOPE_INTERCEPT ) {
                    SlopeInterceptParameterRange parameterRange = new SlopeInterceptParameterRange();
                    riseRange.set( parameterRange.rise( line, challenge.graph ) );
                    y1Range.set( parameterRange.y1( line, challenge.graph ) );
                }
            }
        } );
    }
}
