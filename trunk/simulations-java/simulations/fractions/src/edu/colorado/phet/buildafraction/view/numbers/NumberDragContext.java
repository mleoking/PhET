// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.buildafraction.view.numbers;

import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Sam Reid
 */
public interface NumberDragContext {
    void endDrag( NumberCardNode draggableNumberNode, PInputEvent event );
}