// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.LineFormsGraphNode;
import edu.colorado.phet.linegraphing.common.view.LineFormsViewProperties;
import edu.colorado.phet.linegraphing.common.view.LineNode;
import edu.colorado.phet.linegraphing.common.view.manipulator.LineManipulatorNode;
import edu.colorado.phet.linegraphing.common.view.manipulator.SlopeDragHandler;
import edu.colorado.phet.linegraphing.common.view.manipulator.YInterceptDragHandler;
import edu.colorado.phet.linegraphing.slopeintercept.model.SlopeInterceptModel;

/**
 * Graph that provides direct manipulation of a line in slope-intercept form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptGraphNode extends LineFormsGraphNode {

    private final LineManipulatorNode interceptManipulatorNode, slopeManipulatorNode;

    public SlopeInterceptGraphNode( SlopeInterceptModel model, LineFormsViewProperties viewProperties ) {
        super( model, viewProperties );

        // slope manipulator
        slopeManipulatorNode = new LineManipulatorNode( getManipulatorDiameter(), LGColors.SLOPE );
        slopeManipulatorNode.addInputEventListener( new SlopeDragHandler( UserComponents.slopeManipulator, UserComponentTypes.sprite,
                                                                          slopeManipulatorNode, model.mvt, model.interactiveLine, model.riseRange, model.runRange ) );

        // intercept manipulator
        interceptManipulatorNode = new LineManipulatorNode( getManipulatorDiameter(), LGColors.INTERCEPT );
        interceptManipulatorNode.addInputEventListener( new YInterceptDragHandler( UserComponents.interceptManipulator, UserComponentTypes.sprite,
                                                                                   interceptManipulatorNode, model.mvt, model.interactiveLine, model.y1Range ) );

        addChild( slopeManipulatorNode );
        addChild( interceptManipulatorNode );

        //TODO unfortunate to have to do this in all LineFormGraphNode subclasses...
        updateLinesVisibility( viewProperties.linesVisible.get(), viewProperties.interactiveLineVisible.get(), viewProperties.slopeVisible.get() );
        updateInteractiveLine( model.interactiveLine.get() );
    }

    // Creates a node that displays the line in slope-intercept form.
    protected LineNode createLineNode( Line line, Graph graph, ModelViewTransform mvt ) {
        return new SlopeInterceptLineNode( line, graph, mvt );
    }

    @Override protected void updateLinesVisibility( boolean linesVisible, boolean interactiveLineVisible, boolean slopeVisible ) {
        super.updateLinesVisibility( linesVisible, interactiveLineVisible, slopeVisible );

        // Hide the manipulators at appropriate times (when dragging or based on visibility of lines).
        if ( slopeManipulatorNode != null && interceptManipulatorNode != null ) {
            slopeManipulatorNode.setVisible( linesVisible && interactiveLineVisible );
            interceptManipulatorNode.setVisible( linesVisible && interactiveLineVisible );
        }
    }

    @Override protected void updateInteractiveLine( final Line line ) {
        super.updateInteractiveLine( line );

        // move the manipulators
        if ( slopeManipulatorNode != null && interceptManipulatorNode != null ) {
            slopeManipulatorNode.setOffset( model.mvt.modelToView( new Point2D.Double( line.x1 + line.run, line.y1 + line.rise ) ) );
            interceptManipulatorNode.setOffset( model.mvt.modelToView( new Point2D.Double( line.x1, line.y1 ) ) );
        }
    }
}
