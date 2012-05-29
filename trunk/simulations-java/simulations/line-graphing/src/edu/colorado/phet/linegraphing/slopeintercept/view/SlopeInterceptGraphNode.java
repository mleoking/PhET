// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.view;

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
import edu.colorado.phet.linegraphing.slopeintercept.view.LineManipulatorDragHandler.InterceptDragHandler;
import edu.colorado.phet.linegraphing.slopeintercept.view.LineManipulatorDragHandler.SlopeDragHandler;

/**
 * Specialization of LineGraphNode that provides direct manipulation of a line in slope-intercept form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptGraphNode extends LineGraphNode {

    private final LineManipulatorNode slopeManipulatorNode, interceptManipulatorNode;

    public SlopeInterceptGraphNode( final Graph graph, final ModelViewTransform mvt,
                                    Property<StraightLine> interactiveLine,
                                    ObservableList<StraightLine> savedLines,
                                    ObservableList<StraightLine> standardLines,
                                    Property<DoubleRange> riseRange,
                                    Property<DoubleRange> runRange,
                                    Property<DoubleRange> interceptRange,
                                    Property<Boolean> interactiveEquationVisible,
                                    Property<Boolean> linesVisible,
                                    Property<Boolean> interactiveLineVisible,
                                    Property<Boolean> slopeVisible ) {
        super( graph, mvt, interactiveLine, savedLines, standardLines, interactiveEquationVisible, linesVisible, interactiveLineVisible, slopeVisible );

        // Manipulators for the interactive line
        final double manipulatorDiameter = mvt.modelToViewDeltaX( MANIPULATOR_DIAMETER );

        // interactivity for slope manipulator
        slopeManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.SLOPE );
        slopeManipulatorNode.addInputEventListener( new CursorHandler() );
        slopeManipulatorNode.addInputEventListener( new SlopeDragHandler( UserComponents.slopeManipulator, UserComponentTypes.sprite,
                                                                          slopeManipulatorNode, mvt, interactiveLine, riseRange, runRange ) );
        // interactivity for intercept manipulator
        interceptManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.INTERCEPT );
        interceptManipulatorNode.addInputEventListener( new CursorHandler() );
        interceptManipulatorNode.addInputEventListener( new InterceptDragHandler( UserComponents.interceptManipulator, UserComponentTypes.sprite,
                                                                                  interceptManipulatorNode, mvt, interactiveLine, interceptRange ) );

        addChild( interceptManipulatorNode );
        addChild( slopeManipulatorNode ); // add slope after intercept, so that slope can be changed when x=0
    }

    @Override protected void updateLinesVisibility( boolean linesVisible, boolean interactiveLineVisible, boolean slopeVisible ) {
        super.updateLinesVisibility( linesVisible, interactiveLineVisible, slopeVisible );
        if ( slopeManipulatorNode != null && interceptManipulatorNode != null ) {
            slopeManipulatorNode.setVisible( linesVisible && interactiveLineVisible );
            interceptManipulatorNode.setVisible( linesVisible && interactiveLineVisible );
        }
    }

    // Updates the line and its associated decorations
    @Override protected void updateInteractiveLine( final StraightLine line, final Graph graph, final ModelViewTransform mvt ) {
        super.updateInteractiveLine( line, graph, mvt );
        if ( slopeManipulatorNode != null && interceptManipulatorNode != null ) {
            // move the manipulators
            final double y = line.rise + line.yIntercept;
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
            interceptManipulatorNode.setOffset( mvt.modelToView( new Point2D.Double( 0, line.yIntercept ) ) );
        }
    }

    protected boolean isInteracting() {
        if ( slopeManipulatorNode != null && interceptManipulatorNode != null ) {
            return slopeManipulatorNode.isDragging() || interceptManipulatorNode.isDragging();
        }
        else {
            return false;
        }
    }

    protected StraightLineNode createLineNode( StraightLine line, Graph graph, ModelViewTransform mvt ) {
       return new SlopeInterceptLineNode( line, graph, mvt );
    }
}
