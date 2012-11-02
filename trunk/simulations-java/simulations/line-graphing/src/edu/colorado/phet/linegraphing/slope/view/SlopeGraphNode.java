// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slope.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.slope.model.SlopeModel;
import edu.colorado.phet.linegraphing.common.view.LineFormsGraphNode;
import edu.colorado.phet.linegraphing.common.view.LineFormsViewProperties;
import edu.colorado.phet.linegraphing.common.view.manipulator.LineManipulatorNode;
import edu.colorado.phet.linegraphing.common.view.LineNode;
import edu.colorado.phet.linegraphing.common.view.manipulator.X1Y1DragHandler;
import edu.colorado.phet.linegraphing.common.view.manipulator.X2Y2DragHandler;

/**
 * Graph that provides direct manipulation of a line in point-slope form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeGraphNode extends LineFormsGraphNode {

    private final LineManipulatorNode x1y1ManipulatorNode, x2y2ManipulatorNode;

    public SlopeGraphNode( SlopeModel model, LineFormsViewProperties viewProperties ) {
        super( model, viewProperties );

        // interactivity for point (x1,y1) manipulator
        x1y1ManipulatorNode = new LineManipulatorNode( getManipulatorDiameter(), LGColors.POINT_X1_Y1 );
        x1y1ManipulatorNode.addInputEventListener( new X1Y1DragHandler( UserComponents.x1y1Manipulator, UserComponentTypes.sprite,
                                                                        x1y1ManipulatorNode, model.mvt, model.interactiveLine, model.x1Range, model.y1Range,
                                                                        false /* constantSlope */ ) );

        // interactivity for point (x1,y1) manipulator
        x2y2ManipulatorNode = new LineManipulatorNode( getManipulatorDiameter(), LGColors.POINT_X2_Y2 );
        x2y2ManipulatorNode.addInputEventListener( new X2Y2DragHandler( UserComponents.x2y2Manipulator, UserComponentTypes.sprite,
                                                                        x2y2ManipulatorNode, model.mvt, model.interactiveLine, model.x2Range, model.y2Range ) );

        addChild( x1y1ManipulatorNode );
        addChild( x2y2ManipulatorNode );

        //TODO unfortunate to have to do this in all LineFormGraphNode subclasses...
        updateLinesVisibility( viewProperties.linesVisible.get(), viewProperties.interactiveLineVisible.get(), viewProperties.slopeVisible.get() );
        updateInteractiveLine( model.interactiveLine.get() );
    }

    // Creates a node that draws the line labeled with its slope value.
    protected LineNode createLineNode( Line line, Graph graph, ModelViewTransform mvt ) {
        return new SlopeLineNode( line, graph, mvt );
    }

    @Override protected void updateLinesVisibility( boolean linesVisible, boolean interactiveLineVisible, boolean slopeVisible ) {
        super.updateLinesVisibility( linesVisible, interactiveLineVisible, slopeVisible );

        // Hide the manipulators at appropriate times (when dragging or based on visibility of lines).
        if ( x1y1ManipulatorNode != null && x2y2ManipulatorNode != null ) {
            x1y1ManipulatorNode.setVisible( linesVisible && interactiveLineVisible );
            x2y2ManipulatorNode.setVisible( linesVisible && interactiveLineVisible );
        }
    }

    @Override protected void updateInteractiveLine( final Line line ) {
        super.updateInteractiveLine( line );

        // move the manipulators
        if ( x1y1ManipulatorNode != null && x2y2ManipulatorNode != null ) {
            x1y1ManipulatorNode.setOffset( model.mvt.modelToView( new Point2D.Double( line.x1, line.y1 ) ) );
            x2y2ManipulatorNode.setOffset( model.mvt.modelToView( new Point2D.Double( line.x2, line.y2 ) ) );
        }
    }
}
