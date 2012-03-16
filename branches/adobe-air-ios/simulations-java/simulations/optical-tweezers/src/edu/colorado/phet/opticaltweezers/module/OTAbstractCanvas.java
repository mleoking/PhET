// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.module;

import java.awt.Dimension;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * OTAbstractCanvas is the base class for all canvases (aka "play areas") in this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class OTAbstractCanvas extends PhetPCanvas {

    private PNode _rootNode;
    
    /**
     * Constructor.
     * 
     * @param renderingSize
     */
    public OTAbstractCanvas( Dimension renderingSize ) {
        super( renderingSize );
        
        // Root of our scene graph
        _rootNode = new PNode();
        addWorldChild( _rootNode );
    }
    
    /**
     * Adds a node to the canvas' root node.
     * 
     * @param node
     */
    public void addNode( PNode node ) {
        _rootNode.addChild( node );
    }
}
