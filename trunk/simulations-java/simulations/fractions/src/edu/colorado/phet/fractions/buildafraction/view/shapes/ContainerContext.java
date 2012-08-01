// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Context in which a ContainerNode can be dragged/dropped.
 *
 * @author Sam Reid
 */
public interface ContainerContext {
    void endDrag( ContainerNode containerNode, PInputEvent event );

    void syncModelFractions();
}