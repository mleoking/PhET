// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.Mass;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author John Blanco
 */
public class MassDragHandler extends PDragEventHandler {
    private final Mass mass;
    private final PNode node;
    private final ModelViewTransform mvt;
    private final PhetPCanvas canvas;

    public MassDragHandler( Mass mass, PNode node, PhetPCanvas canvas, ModelViewTransform mvt ) {
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
        System.out.println( "yOffset = " + yOffset );
        mass.setPosition( modelPosition.getX(), modelPosition.getY() - yOffset );
    }

    @Override
    public void mouseDragged( PInputEvent event ) {
        PDimension viewDelta = event.getDeltaRelativeTo( node.getParent() );
        ImmutableVector2D modelDelta = mvt.viewToModelDelta( new ImmutableVector2D( viewDelta ) );
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
        Point2D modelPos = mvt.viewToModel( worldPos );
        return modelPos;
    }
}
