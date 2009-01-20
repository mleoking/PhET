package edu.colorado.phet.acidbasesolutions.module;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * Base class for all canvases.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSAbstractCanvas extends PhetPCanvas {

    private final PNode _rootNode;
    
    public ABSAbstractCanvas() {
        super( ABSConstants.CANVAS_RENDERING_SIZE );
        setBackground( ABSConstants.CANVAS_BACKGROUND );
        
        // Root of our scene graph
        _rootNode = new PNode();
        addWorldChild( _rootNode );
    }    
    
    public void addNode( PNode node ) {
        _rootNode.addChild( node );
    }
}
