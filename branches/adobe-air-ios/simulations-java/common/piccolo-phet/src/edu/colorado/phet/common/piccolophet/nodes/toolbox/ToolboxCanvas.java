// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.toolbox;

import edu.umd.cs.piccolo.PNode;

/**
 * Interface used by toolboxes to add and remove children.  These methods are here since different layers may be necessary in some circumstances.
 *
 * @author Sam Reid
 */
public interface ToolboxCanvas {

    //Add a child to the canvas
    void addChild( PNode node );

    //Remove a child from the canvas
    void removeChild( PNode node );

    //Get the root node used in the stage of the canvas
    PNode getRootNode();
}
