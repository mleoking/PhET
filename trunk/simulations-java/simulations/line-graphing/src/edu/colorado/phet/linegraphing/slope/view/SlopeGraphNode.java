// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slope.view;

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
import edu.colorado.phet.linegraphing.common.view.manipulator.X1Y1DragHandler;
import edu.colorado.phet.linegraphing.common.view.manipulator.X2Y2DragHandler;
import edu.colorado.phet.linegraphing.slope.model.SlopeModel;

/**
 * Graph that provides direct manipulation of a line in point-slope form.
 * Adds manipulators for (x1,y1) and (x2,y2) to the base class functionality.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeGraphNode extends LineFormsGraphNode {

    private final LineManipulatorNode x1y1ManipulatorNode, x2y2ManipulatorNode;

    public SlopeGraphNode( final SlopeModel model, final LineFormsViewProperties viewProperties ) {
        super( model, viewProperties );

        // (x1,y1) manipulator
        x1y1ManipulatorNode = new LineManipulatorNode( getManipulatorDiameter(), LGColors.POINT_X1_Y1 );
        x1y1ManipulatorNode.addInputEventListener( new X1Y1DragHandler( UserComponents.x1y1Manipulator, UserComponentTypes.sprite,
                                                                        x1y1ManipulatorNode, model.mvt, model.interactiveLine, model.x1Range, model.y1Range,
                                                                        false /* constantSlope */ ) );

        // (x2,y2) manipulator
        x2y2ManipulatorNode = new LineManipulatorNode( getManipulatorDiameter(), LGColors.POINT_X2_Y2 );
        x2y2ManipulatorNode.addInputEventListener( new X2Y2DragHandler( UserComponents.x2y2Manipulator, UserComponentTypes.sprite,
                                                                        x2y2ManipulatorNode, model.mvt, model.interactiveLine, model.x2Range, model.y2Range ) );
        // rendering order
        addChild( x1y1ManipulatorNode );
        addChild( x2y2ManipulatorNode );

        // position of manipulators
        model.interactiveLine.addObserver( new VoidFunction1<Line>() {
            public void apply( Line line ) {
                x1y1ManipulatorNode.setOffset( model.mvt.modelToView( new Point2D.Double( line.x1, line.y1 ) ) );
                x2y2ManipulatorNode.setOffset( model.mvt.modelToView( new Point2D.Double( line.x2, line.y2 ) ) );
            }
        } );

        // visibility of manipulators
        RichSimpleObserver visibilityObserver = new RichSimpleObserver() {
            @Override public void update() {
                final boolean visible = viewProperties.linesVisible.get() && viewProperties.interactiveLineVisible.get();
                x1y1ManipulatorNode.setVisible( visible );
                x2y2ManipulatorNode.setVisible( visible );
            }
        };
        visibilityObserver.observe( viewProperties.linesVisible, viewProperties.interactiveLineVisible );
    }

    // Creates a line labeled with its slope.
    @Override protected LineNode createLineNode( Line line, Graph graph, ModelViewTransform mvt ) {
        return new SlopeLineNode( line, graph, mvt );
    }
}
