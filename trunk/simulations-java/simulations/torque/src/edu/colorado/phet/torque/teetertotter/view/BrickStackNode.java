// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.torque.teetertotter.model.weights.ShapeWeight;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * A node that represents a brick in the view.
 *
 * @author John Blanco
 */
public class BrickStackNode extends PNode {
    public BrickStackNode( final ModelViewTransform mvt, final ShapeWeight weight ) {
        addChild( new PhetPPath( Color.RED, new BasicStroke( 1 ), Color.BLACK ) {{
            weight.shapeProperty.addObserver( new VoidFunction1<Shape>() {
                public void apply( Shape shape ) {
                    setPathTo( mvt.modelToView( shape ) );
                }
            } );
        }} );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PDragEventHandler() {
            @Override protected void startDrag( PInputEvent event ) {
                super.startDrag( event );
                // The user is moving this, so they have control.
                weight.userControlled.set( true );
            }

            @Override
            public void mouseDragged( PInputEvent event ) {
                PDimension viewDelta = event.getDeltaRelativeTo( getParent() );
                ImmutableVector2D modelDelta = mvt.viewToModelDelta( new ImmutableVector2D( viewDelta ) );
                weight.translate( modelDelta );
            }

            @Override protected void endDrag( PInputEvent event ) {
                super.endDrag( event );
                // The user is no longer moving this, so they have relinquished control.
                weight.userControlled.set( false );
            }
        } );
    }
}
