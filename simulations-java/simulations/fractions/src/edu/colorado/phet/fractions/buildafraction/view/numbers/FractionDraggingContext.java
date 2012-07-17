// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.numbers;

import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Sam Reid
 */
public interface FractionDraggingContext {
    void endDrag( FractionNode fractionGraphic, PInputEvent event );

    void updateStacks();
}