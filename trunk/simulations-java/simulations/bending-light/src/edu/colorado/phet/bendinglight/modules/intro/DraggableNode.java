// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.intro;

import java.awt.geom.Rectangle2D;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Sam Reid
 */
public abstract class DraggableNode extends PNode {
    public abstract void dragAll( PInputEvent event );

    public abstract Rectangle2D[] getDragComponents();
}
