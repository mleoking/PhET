// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import edu.umd.cs.piccolo.PNode;

/**
 * Utility class for creating a node with no content that has the same size as another node.
 * This is used to create a control panel of the right dimensions, which is populated with other nodes.
 *
 * @author Sam Reid
 */
public class EmptyNode extends PNode {
    public EmptyNode( PNode dimension ) {
        setBounds( 0, 0, dimension.getFullBounds().getWidth(), dimension.getFullBounds().getHeight() );
    }
}
