// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.energyformsandchanges.intro.model.UserMovableModelElement;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Drag handler for movable model elements.  Monitors drag events and moves
 * the model element accordingly.
 *
 * @author John Blanco
 */
public class MovableElementDragHandler extends PDragEventHandler {
    private final UserMovableModelElement movableModelElement;
    private final PNode pNode;
    private final ModelViewTransform mvt;

    public MovableElementDragHandler( UserMovableModelElement movableModelElement, PNode node, ModelViewTransform mvt ) {
        this.movableModelElement = movableModelElement;
        pNode = node;
        this.mvt = mvt;
    }

    @Override protected void startDrag( PInputEvent event ) {
        super.startDrag( event );
        // The user is moving this, so they have control.
        movableModelElement.userControlled.set( true );
    }

    @Override
    public void mouseDragged( PInputEvent event ) {
        PDimension viewDelta = event.getDeltaRelativeTo( pNode.getParent() );
        ImmutableVector2D modelDelta = mvt.viewToModelDelta( new ImmutableVector2D( viewDelta ) );
        movableModelElement.translate( modelDelta );
    }

    @Override protected void endDrag( PInputEvent event ) {
        super.endDrag( event );
        // The user is no longer moving this, so they have relinquished control.
        movableModelElement.userControlled.set( false );
    }
}
