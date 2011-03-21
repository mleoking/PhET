// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.intro;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * The ToolNode is a node created by dragging out of a toolbox, and destroyed by dropping it back in the toolbox.
 *
 * @author Sam Reid
 */
public abstract class ToolNode extends PNode {
    public abstract void dragAll( PDimension viewDelta );//translate all components of this tool by the specified view delta (dx,dy)

    //Override if there are several components that can be dropped in the toolbox.
    public PNode[] getDroppableComponents() {
        return new PNode[] { this };
    }
}
