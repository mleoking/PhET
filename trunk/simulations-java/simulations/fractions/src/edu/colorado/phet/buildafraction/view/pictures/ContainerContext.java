// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.buildafraction.view.pictures;

import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Sam Reid
 */
public interface ContainerContext {
    void endDrag( ContainerNode containerNode, PInputEvent event );

    void syncModelFractions();
}