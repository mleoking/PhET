// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.balanceandtorque.common.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.common.model.masses.Mass;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Common drag handler for mass nodes.  Positions the corresponding model
 * element based on the movement of the mouse, and also sets and clears the
 * property that indicates whether or not the mass is being controlled by the
 * user.
 *
 * @author John Blanco
 */
public class MassDragHandler extends SimSharingDragHandler {
    private final Mass mass;
    private final PNode node;
    private final ModelViewTransform mvt;
    private final PhetPCanvas canvas;

    public MassDragHandler( Mass mass, PNode node, PhetPCanvas canvas, ModelViewTransform mvt ) {
        super( mass.getUserComponent(), UserComponentTypes.sprite, false );
        this.mass = mass;
        this.node = node;
        this.canvas = canvas;
        this.mvt = mvt;
    }

    @Override protected void startDrag( PInputEvent event ) {
        super.startDrag( event );
        // The user is moving this, so they have assumed control.
        mass.userControlled.set( true );
        Point2D modelPosition = getModelPosition( event.getCanvasPosition() );
        double yOffset = mass.getMiddlePoint().getY() - mass.getPosition().getY();
        mass.setPosition( modelPosition.getX(), modelPosition.getY() - yOffset );
    }

    @Override
    public void mouseDragged( PInputEvent event ) {
        super.mouseDragged( event );
        PDimension viewDelta = event.getDeltaRelativeTo( node.getParent() );
        Vector2D modelDelta = mvt.viewToModelDelta( new Vector2D( viewDelta ) );
        mass.translate( modelDelta );
    }

    @Override protected void endDrag( PInputEvent event ) {
        super.endDrag( event );
        // The user is no longer moving this, so they have relinquished control.
        mass.userControlled.set( false );
    }


    /**
     * Convert the canvas position to the corresponding location in the model.
     */
    private Point2D getModelPosition( Point2D canvasPos ) {
        Point2D worldPos = new Point2D.Double( canvasPos.getX(), canvasPos.getY() );
        canvas.getPhetRootNode().screenToWorld( worldPos );
        return mvt.viewToModel( worldPos );
    }
}
