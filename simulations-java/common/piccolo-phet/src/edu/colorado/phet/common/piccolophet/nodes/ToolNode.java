// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * The ToolNode is a node created by dragging out of a toolbox, and destroyed by dropping it back in the toolbox.
 * It is an abstract class instead of an interface for convenience in usage, an alternative pattern would be to
 * change this to an interface with a getNode() method, which is usually implemented as 'return this'
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
