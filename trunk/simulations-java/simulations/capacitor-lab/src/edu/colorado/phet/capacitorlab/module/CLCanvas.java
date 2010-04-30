/* Copyright 2010, University of Colorado */

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
    
    private PNode rootNode;

    public CLCanvas() {
        super( CLConstants.CANVAS_RENDERING_SIZE );
        
        setBackground( CLPaints.CANVAS_BACKGROUND );

        // Root of our scene graph
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
}
