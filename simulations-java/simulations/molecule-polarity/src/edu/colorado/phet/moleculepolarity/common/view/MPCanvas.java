// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.geom.Dimension2D;
import java.util.logging.Logger;

import edu.colorado.phet.common.phetcommon.util.logging.LoggingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.umd.cs.piccolo.PNode;

/**
 * Base class for all canvases.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MPCanvas extends PhetPCanvas {

    private static final Logger LOGGER = LoggingUtils.getLogger( MPCanvas.class.getCanonicalName() );

    private final PNode rootNode;

    protected MPCanvas() {
        super( MPConstants.CANVAS_RENDERING_SIZE );
        setBackground( MPConstants.CANVAS_COLOR );
        rootNode = new PNode();
        addWorldChild( rootNode );
    }

    // Adds a child node to the root node.
    protected void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    // Removes a child node from the root node.
    protected void removeChild( PNode node ) {
        if ( node != null && rootNode.indexOfChild( node ) != -1 ) {
            rootNode.removeChild( node );
        }
    }

    @Override protected void updateLayout() {
        super.updateLayout();

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        LOGGER.info( "size=" + getSize() + " worldSize=" + worldSize );
    }
}
