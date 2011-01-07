// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * Base class for all canvases in the "Capacitor Lab" sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class CLCanvas extends PhetPCanvas {
    
    private PNode rootNode; // root node of our scenegraph, all nodes added below here

    public CLCanvas() {
        super( CLConstants.CANVAS_RENDERING_SIZE );
        
        setBackground( CLPaints.CANVAS_BACKGROUND );

        rootNode = new PNode();
        addWorldChild( rootNode );
    }
    
    protected void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    protected void removeChild( PNode node ) {
        if ( node != null && rootNode.indexOfChild( node ) != -1 ) {
            rootNode.removeChild( node );
        }
    }
    
    protected boolean isRootChild( PNode node ) {
        return rootNode.getChildrenReference().contains( node );
    }
}
