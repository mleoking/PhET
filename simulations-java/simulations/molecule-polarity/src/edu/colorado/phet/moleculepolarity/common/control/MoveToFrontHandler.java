// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.control;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Handler that moves a node to the front when a drag starts.
 * Note that the node to move to the front is not necessarily the picked node.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MoveToFrontHandler extends PDragSequenceEventHandler {

    private final PNode node;

    public MoveToFrontHandler( PNode node ) {
        this.node = node;
    }

    @Override
    protected void startDrag( PInputEvent event ) {
        super.startDrag( event );
        node.moveToFront();
    }
}
