// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.control;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Makes a node visible only when the cursor is over it or it's being dragged.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MouseOverVisibilityHandler extends PDragSequenceEventHandler {

    private final PNode node;
    private boolean mouseInside, dragging;

    public MouseOverVisibilityHandler( PNode node ) {
        this.node = node;
    }

    @Override public void mouseEntered( PInputEvent event ) {
        super.mouseEntered( event );
        mouseInside = true;
        updateVisibility();
    }

    @Override public void mouseExited( PInputEvent event ) {
        super.mouseExited( event );
        mouseInside = false;
        updateVisibility();
    }

    @Override public void startDrag( PInputEvent event ) {
        super.startDrag( event );
        dragging = true;
        updateVisibility();
    }

    @Override public void endDrag( PInputEvent event ) {
        super.endDrag( event );
        dragging = false;
        updateVisibility();
    }

    private void updateVisibility() {
        node.setVisible( mouseInside || dragging );
    }
}
