/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.module;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.help.OTWiggleMe;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * AbstractCanvas is the base class for all canvases (aka "play areas") in this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractCanvas extends PhetPCanvas {

    private PNode _rootNode;
    
    /**
     * Constructor.
     * 
     * @param renderingSize
     */
    public AbstractCanvas( Dimension renderingSize ) {
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
