// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.pointslope.model.PointSlopeModel;
import edu.colorado.phet.linegraphing.common.view.LineFormsGraphNode;
import edu.colorado.phet.linegraphing.common.view.LineFormsViewProperties;
import edu.colorado.phet.linegraphing.common.view.manipulator.LineManipulatorNode;
import edu.colorado.phet.linegraphing.common.view.LineNode;
import edu.colorado.phet.linegraphing.common.view.manipulator.SlopeDragHandler;
import edu.colorado.phet.linegraphing.common.view.manipulator.X1Y1DragHandler;

/**
 * Graph that provides direct manipulation of a line in point-slope form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointSlopeGraphNode extends LineFormsGraphNode {

    private final LineManipulatorNode pointManipulatorNode, slopeManipulatorNode;

    public PointSlopeGraphNode( PointSlopeModel model, LineFormsViewProperties viewProperties ) {
        super( model, viewProperties );

        // Diameter of manipulators, in view coordinate frame.
        final double manipulatorDiameter = model.mvt.modelToViewDeltaX( MANIPULATOR_DIAMETER );

        // interactivity for point (x1,y1) manipulator
        pointManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.POINT_X1_Y1 );
        pointManipulatorNode.addInputEventListener( new X1Y1DragHandler( UserComponents.pointManipulator, UserComponentTypes.sprite,
                                                                         pointManipulatorNode, model.mvt, model.interactiveLine, model.x1Range, model.y1Range,
                                                                         true /* constantSlope */ ) );
        // interactivity for slope manipulator
        slopeManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.SLOPE );
        slopeManipulatorNode.addInputEventListener( new SlopeDragHandler( UserComponents.slopeManipulator, UserComponentTypes.sprite,
                                                                          slopeManipulatorNode, model.mvt, model.interactiveLine, model.riseRange, model.runRange ) );

        addChild( pointManipulatorNode );
        addChild( slopeManipulatorNode );

        //TODO unfortunate to have to do this in all LineFormGraphNode subclasses...
        updateLinesVisibility( viewProperties.linesVisible.get(), viewProperties.interactiveLineVisible.get(), viewProperties.slopeVisible.get() );
        updateInteractiveLine( model.interactiveLine.get() );
    }

    // Creates a node that displays the line in point-slope form.
    protected LineNode createLineNode( Line line, Graph graph, ModelViewTransform mvt ) {
        return new PointSlopeLineNode( line, graph, mvt );
    }

    @Override protected void updateLinesVisibility( boolean linesVisible, boolean interactiveLineVisible, boolean slopeVisible ) {
        super.updateLinesVisibility( linesVisible, interactiveLineVisible, slopeVisible );

        // Hide the manipulators at appropriate times (when dragging or based on visibility of lines).
        if ( pointManipulatorNode != null && slopeManipulatorNode != null ) {
            pointManipulatorNode.setVisible( linesVisible && interactiveLineVisible );
            slopeManipulatorNode.setVisible( linesVisible && interactiveLineVisible );
        }
    }

    @Override protected void updateInteractiveLine( final Line line ) {
        super.updateInteractiveLine( line );

        // move the manipulators
        if ( pointManipulatorNode != null && slopeManipulatorNode != null ) {
            pointManipulatorNode.setOffset( model.mvt.modelToView( new Point2D.Double( line.x1, line.y1 ) ) );
            slopeManipulatorNode.setOffset( model.mvt.modelToView( new Point2D.Double( line.x1 + line.run, line.y1 + line.rise ) ) );
        }
    }
}
