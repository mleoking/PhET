// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.StraightLine;
import edu.colorado.phet.linegraphing.common.view.LineGraphNode;
import edu.colorado.phet.linegraphing.common.view.LineManipulatorNode;
import edu.colorado.phet.linegraphing.common.view.StraightLineNode;
import edu.colorado.phet.linegraphing.pointslope.view.PointSlopeDragHandler.SlopeDragHandler;
import edu.colorado.phet.linegraphing.pointslope.view.PointSlopeDragHandler.X1Y1DragHandler;

/**
 * Graph that provides direct manipulation of a line in point-slope form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointSlopeGraphNode extends LineGraphNode {

    private final LineManipulatorNode pointManipulator, slopeManipulatorNode;

    public PointSlopeGraphNode( final Graph graph, final ModelViewTransform mvt,
                                Property<StraightLine> interactiveLine,
                                ObservableList<StraightLine> savedLines,
                                ObservableList<StraightLine> standardLines,
                                Property<DoubleRange> riseRange,
                                Property<DoubleRange> runRange,
                                Property<DoubleRange> x1Range,
                                Property<DoubleRange> y1Range,
                                Property<Boolean> interactiveEquationVisible,
                                Property<Boolean> linesVisible,
                                Property<Boolean> interactiveLineVisible,
                                Property<Boolean> slopeVisible ) {
        super( graph, mvt, interactiveLine, savedLines, standardLines, interactiveEquationVisible, linesVisible, interactiveLineVisible, slopeVisible );

        // Manipulators for the interactive line
        final double manipulatorDiameter = mvt.modelToViewDeltaX( MANIPULATOR_DIAMETER );

        // interactivity for point (x1,y1) manipulator
        pointManipulator = new LineManipulatorNode( manipulatorDiameter, LGColors.POINT_X1_Y1 );
        pointManipulator.addInputEventListener( new CursorHandler() );
        pointManipulator.addInputEventListener( new X1Y1DragHandler( UserComponents.interceptManipulator, UserComponentTypes.sprite,
                                                                     pointManipulator, mvt, interactiveLine, x1Range, y1Range ) );
        // interactivity for slope manipulator
        slopeManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.SLOPE );
        slopeManipulatorNode.addInputEventListener( new CursorHandler() );
        slopeManipulatorNode.addInputEventListener( new SlopeDragHandler( UserComponents.slopeManipulator, UserComponentTypes.sprite,
                                                                          slopeManipulatorNode, mvt, interactiveLine, riseRange, runRange ) );

        addChild( pointManipulator );
        addChild( slopeManipulatorNode ); // add slope after intercept, so that slope can be changed when x=0

        updateInteractiveLine( interactiveLine.get(), graph, mvt ); //TODO delete this after pointManipulator drag handler is working
    }

    // Hides the manipulators at appropriate times (when dragging or based on visibility of lines).
    @Override protected void updateLinesVisibility( boolean linesVisible, boolean interactiveLineVisible, boolean slopeVisible ) {
        super.updateLinesVisibility( linesVisible, interactiveLineVisible, slopeVisible );
        if ( slopeManipulatorNode != null && pointManipulator != null ) {
            slopeManipulatorNode.setVisible( linesVisible && interactiveLineVisible );
            pointManipulator.setVisible( linesVisible && interactiveLineVisible );
        }
    }

    // Updates the positions of the manipulators
    @Override protected void updateInteractiveLine( final StraightLine line, final Graph graph, final ModelViewTransform mvt ) {
        super.updateInteractiveLine( line, graph, mvt );
        if ( slopeManipulatorNode != null && pointManipulator != null ) {
            // move the manipulators
            slopeManipulatorNode.setOffset( mvt.modelToView( new Point2D.Double( line.x1 + line.run, line.x1 + line.rise ) ) );
            pointManipulator.setOffset( mvt.modelToView( new Point2D.Double( line.x1, line.y1 ) ) );
        }
    }

    // True if either manipulator is in use.
    protected boolean isInteracting() {
        if ( slopeManipulatorNode != null && pointManipulator != null ) {
            return slopeManipulatorNode.isDragging() || pointManipulator.isDragging();
        }
        else {
            return false;
        }
    }

    // Creates a node that displays the line in slope-intercept form.
    protected StraightLineNode createLineNode( StraightLine line, Graph graph, ModelViewTransform mvt ) {
       return new PointSlopeLineNode( line, graph, mvt );
    }
}
