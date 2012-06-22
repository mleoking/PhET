package edu.colorado.phet.buildafraction.view.numbers;

import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Sam Reid
 */
public interface FractionDraggingContext {
    void endDrag( FractionNode fractionGraphic, PInputEvent event );
}