package edu.colorado.phet.buildafraction.view.pictures;

import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Sam Reid
 */
public interface ContainerContext {
    void endDrag( DynamicContainerNode containerNode, PInputEvent event );

    void syncModelFractions();
}