package edu.colorado.phet.fractionsintro.buildafraction.view;

import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Sam Reid
 */
public interface DragContext {
    void endDrag( NumberNode draggableNumberNode, PInputEvent event );
}