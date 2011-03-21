// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.intro;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Sam Reid
 */
public abstract class ToolNode extends PNode {
    public abstract void dragAll( PInputEvent event );

    //Override if there are several components that can be dropped in the toolbox.
    public PNode[] getDroppableComponents() {
        return new PNode[] { this };
    }
}
