/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.module;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

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
        
        // When the canvas is resized...
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                // update the layout
                updateLayout();
            }
        } );
    }
    
    /**
     * Updates the layout when the canvas size is changed.
     */
    public abstract void updateLayout();
    
    /**
     * Adds a node to the canvas' root node.
     * 
     * @param node
     */
    public void addNode( PNode node ) {
        _rootNode.addChild( node );
    }
}
