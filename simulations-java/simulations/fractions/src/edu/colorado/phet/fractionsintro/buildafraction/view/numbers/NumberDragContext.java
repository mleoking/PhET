package edu.colorado.phet.fractionsintro.buildafraction.view.numbers;

import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Sam Reid
 */
public interface NumberDragContext {
    void endDrag( NumberCardNode draggableNumberNode, PInputEvent event );
}