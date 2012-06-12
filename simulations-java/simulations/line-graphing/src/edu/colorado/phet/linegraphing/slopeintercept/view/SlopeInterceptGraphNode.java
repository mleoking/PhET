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
import edu.colorado.phet.linegraphing.common.view.SlopeDragHandler;
import edu.colorado.phet.linegraphing.common.view.StraightLineNode;

/**
 * Graph that provides direct manipulation of a line in slope-intercept form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptGraphNode extends LineGraphNode {

    private final LineManipulatorNode slopeManipulatorNode, interceptManipulatorNode;

    /**
     * Constructor
     * @param graph
     * @param mvt transform between model and view coordinate frames
     * @param interactiveLine the line that can be manipulated by the user
     * @param savedLines lines that have been saved by the user
     * @param standardLines standard lines (eg, y=x) that are available for viewing
     * @param linesVisible are lines visible on the graph?
     * @param interactiveLineVisible is the interactive line visible visible on the graph?
     * @param interactiveEquationVisible is the equation visible on the interactive line?
     * @param slopeVisible are the slope (rise/run) brackets visible on the graphed line?
     * @param riseRange
     * @param runRange
     * @param yInterceptRange
     */
    public SlopeInterceptGraphNode( final Graph graph,
                                    final ModelViewTransform mvt,
                                    Property<StraightLine> interactiveLine,
                                    ObservableList<StraightLine> savedLines,
                                    ObservableList<StraightLine> standardLines,
                                    Property<Boolean> linesVisible, Property<Boolean> interactiveLineVisible, Property<Boolean> interactiveEquationVisible, Property<Boolean> slopeVisible, Property<DoubleRange> riseRange,
                                    Property<DoubleRange> runRange,
                                    Property<DoubleRange> yInterceptRange ) {
        super( graph, mvt, interactiveLine, savedLines, standardLines, linesVisible, interactiveLineVisible, interactiveEquationVisible, slopeVisible );

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
                                                                                  interceptManipulatorNode, mvt, interactiveLine, yInterceptRange ) );

        addChild( interceptManipulatorNode );
        addChild( slopeManipulatorNode ); // add slope after intercept, so that slope can be changed when x=0
    }

    // Hides the manipulators at appropriate times (when dragging or based on visibility of lines).
    @Override protected void updateLinesVisibility( boolean linesVisible, boolean interactiveLineVisible, boolean slopeVisible ) {
        super.updateLinesVisibility( linesVisible, interactiveLineVisible, slopeVisible );
        if ( slopeManipulatorNode != null && interceptManipulatorNode != null ) {
            slopeManipulatorNode.setVisible( linesVisible && interactiveLineVisible );
            interceptManipulatorNode.setVisible( linesVisible && interactiveLineVisible );
        }
    }

    // Updates the positions of the manipulators
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

    // True if either manipulator is in use.
    protected boolean isInteracting() {
        if ( slopeManipulatorNode != null && interceptManipulatorNode != null ) {
            return slopeManipulatorNode.isDragging() || interceptManipulatorNode.isDragging();
        }
        else {
            return false;
        }
    }

    // Creates a node that displays the line in slope-intercept form.
    protected StraightLineNode createLineNode( StraightLine line, Graph graph, ModelViewTransform mvt ) {
       return new SlopeInterceptLineNode( line, graph, mvt );
    }
}
