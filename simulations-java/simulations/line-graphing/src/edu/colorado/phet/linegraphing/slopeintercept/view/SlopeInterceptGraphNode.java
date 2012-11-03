// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.view;

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
import edu.colorado.phet.linegraphing.common.view.manipulator.YInterceptDragHandler;
import edu.colorado.phet.linegraphing.slopeintercept.model.SlopeInterceptModel;

/**
 * Graph that provides direct manipulation of a line in slope-intercept form.
 * Adds manipulators for slope and intercept to the base class functionality.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptGraphNode extends LineFormsGraphNode {

    private final LineManipulatorNode interceptManipulatorNode, slopeManipulatorNode;

    public SlopeInterceptGraphNode( final SlopeInterceptModel model, final LineFormsViewProperties viewProperties ) {
        super( model, viewProperties );

        // slope manipulator
        slopeManipulatorNode = new LineManipulatorNode( getManipulatorDiameter(), LGColors.SLOPE );
        slopeManipulatorNode.addInputEventListener( new SlopeDragHandler( UserComponents.slopeManipulator, UserComponentTypes.sprite,
                                                                          slopeManipulatorNode, model.mvt, model.interactiveLine, model.riseRange, model.runRange ) );

        // intercept manipulator
        interceptManipulatorNode = new LineManipulatorNode( getManipulatorDiameter(), LGColors.INTERCEPT );
        interceptManipulatorNode.addInputEventListener( new YInterceptDragHandler( UserComponents.interceptManipulator, UserComponentTypes.sprite,
                                                                                   interceptManipulatorNode, model.mvt, model.interactiveLine, model.y1Range ) );
        // rendering order
        addChild( slopeManipulatorNode );
        addChild( interceptManipulatorNode );

        // position of manipulators
        model.interactiveLine.addObserver( new VoidFunction1<Line>() {
            public void apply( Line line ) {
                slopeManipulatorNode.setOffset( model.mvt.modelToView( new Point2D.Double( line.x1 + line.run, line.y1 + line.rise ) ) );
                interceptManipulatorNode.setOffset( model.mvt.modelToView( new Point2D.Double( line.x1, line.y1 ) ) );
            }
        } );

        // visibility of manipulators
        RichSimpleObserver visibilityObserver = new RichSimpleObserver() {
            @Override public void update() {
                final boolean visible = viewProperties.linesVisible.get() && viewProperties.interactiveLineVisible.get();
                slopeManipulatorNode.setVisible( visible );
                interceptManipulatorNode.setVisible( visible );
            }
        };
        visibilityObserver.observe( viewProperties.linesVisible, viewProperties.interactiveLineVisible );
    }

    // Creates a line labeled with its slope-intercept equation.
    @Override protected LineNode createLineNode( Line line, Graph graph, ModelViewTransform mvt ) {
        return new SlopeInterceptLineNode( line, graph, mvt );
    }
}
