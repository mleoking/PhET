// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.view;


import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.reactantsproductsandleftovers.controls.ValueNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Base class for nodes that arrange images in the Before and After boxes.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class ImageLayoutNode extends PhetPNode {
    
    static final boolean ENABLE_DEBUG_OUTPUT = false;
    
    private final PDimension boxSize;
    
    public ImageLayoutNode( PDimension boxSize ) {
        this.boxSize = new PDimension( boxSize );
    }
    
    protected double getBoxWidth() {
        return boxSize.getWidth();
    }
    
    protected double getBoxHeight() {
        return boxSize.getHeight();
    }
    
    /**
     * Adds a substance image node to the scenegraph.
     * The corresponding ValueNode is provided in case nodes need to be aligned with it.
     * @param node
     * @param valueNode
     * @return
     */
    public abstract void addNode( SubstanceImageNode node, ValueNode valueNode );
    
    /**
     * Removes a substance image node from the scenegraph.
     * @param node
     * @param parent
     */
    public abstract void removeNode( SubstanceImageNode node );
}