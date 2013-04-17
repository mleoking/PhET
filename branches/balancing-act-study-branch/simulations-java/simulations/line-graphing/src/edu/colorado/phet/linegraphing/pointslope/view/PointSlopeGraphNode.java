// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
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
import edu.colorado.phet.linegraphing.common.view.manipulator.X1Y1DragHandler;
import edu.colorado.phet.linegraphing.pointslope.model.PointSlopeModel;

/**
 * Graph that provides direct manipulation of a line in point-slope form.
 * Adds manipulators for point and slope to the base class functionality.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointSlopeGraphNode extends LineFormsGraphNode {

    private final LineManipulatorNode pointManipulatorNode, slopeManipulatorNode;

    public PointSlopeGraphNode( final PointSlopeModel model, final LineFormsViewProperties viewProperties ) {
        super( model, viewProperties );

        // point manipulator
        pointManipulatorNode = new LineManipulatorNode( getManipulatorDiameter(), LGColors.POINT_X1_Y1 );
        pointManipulatorNode.addInputEventListener( new X1Y1DragHandler( UserComponents.pointManipulator, UserComponentTypes.sprite,
                                                                         pointManipulatorNode, model.mvt, model.interactiveLine, model.x1Range, model.y1Range,
                                                                         true /* constantSlope */ ) );
        // slope manipulator
        slopeManipulatorNode = new LineManipulatorNode( getManipulatorDiameter(), LGColors.SLOPE );
        slopeManipulatorNode.addInputEventListener( new SlopeDragHandler( UserComponents.slopeManipulator, UserComponentTypes.sprite,
                                                                          slopeManipulatorNode, model.mvt, model.interactiveLine, model.riseRange, model.runRange ) );
        // rendering order
        addChild( pointManipulatorNode );
        addChild( slopeManipulatorNode );

        // position of manipulators
        model.interactiveLine.addObserver( new VoidFunction1<Line>() {
            public void apply( Line line ) {
                pointManipulatorNode.setOffset( model.mvt.modelToView( new Point2D.Double( line.x1, line.y1 ) ) );
                slopeManipulatorNode.setOffset( model.mvt.modelToView( new Point2D.Double( line.x1 + line.run, line.y1 + line.rise ) ) );
            }
        } );

        // visibility of manipulators
        RichSimpleObserver visibilityObserver = new RichSimpleObserver() {
            @Override public void update() {
                final boolean visible = viewProperties.linesVisible.get() && viewProperties.interactiveLineVisible.get();
                pointManipulatorNode.setVisible( visible );
                slopeManipulatorNode.setVisible( visible );
            }
        };
        visibilityObserver.observe( viewProperties.linesVisible, viewProperties.interactiveLineVisible );
    }

    // Creates a line labeled with its point-slope equation.
    @Override protected LineNode createLineNode( Line line, Graph graph, ModelViewTransform mvt ) {
        return new PointSlopeLineNode( line, graph, mvt );
    }
}
