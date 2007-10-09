/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.module;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * GlaciersAbstractCanvas is the base class for all canvases (aka "play areas").
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class GlaciersAbstractCanvas extends PhetPCanvas {

    private PNode _rootNode;
    
    /**
     * Constructor.
     * 
     * @param renderingSize
     */
    public GlaciersAbstractCanvas( Dimension renderingSize ) {
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
