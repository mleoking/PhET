// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * When dragging a tool, move it to the front so it won't go behind other tools.
 *
 * @author Sam Reid
 */
public class MoveToFront extends PBasicInputEventHandler {
    private final PNode node;

    public MoveToFront( PNode node ) {
        this.node = node;
    }

    @Override public void mousePressed( PInputEvent event ) {
        node.moveToFront();
    }
}