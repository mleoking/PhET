/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.module;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
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
    
    /*
     * Keeps a node inside the canvas.
     * Assumes that the node is a direct parent of rootNode.
     */
    protected void keepInsideCanvas( PNode node ) {
        
        assert( isRootChild( node ) );
        
        Rectangle2D worldBounds = new Rectangle2D.Double( 0, 0, getWorldSize().getWidth(), getWorldSize().getHeight() );
        Rectangle2D nodeBounds = node.getFullBoundsReference();
        if ( !worldBounds.contains( nodeBounds ) ) {
            
            double x = nodeBounds.getX();
            if ( nodeBounds.getX() < worldBounds.getMinX() ) {
                x = worldBounds.getMinX();
            }
            else if ( nodeBounds.getMaxX() > worldBounds.getMaxX() ) {
                x = worldBounds.getMaxX() - nodeBounds.getWidth();
            }
            
            double y = nodeBounds.getY();
            if ( nodeBounds.getY() < worldBounds.getMinY() ) {
                y = worldBounds.getMinY();
            }
            else if ( nodeBounds.getMaxY() > worldBounds.getMaxY() ) {
                y = worldBounds.getMaxY() - nodeBounds.getHeight();
            }
           
            x -= PNodeLayoutUtils.getOriginXOffset( node );
            y -= PNodeLayoutUtils.getOriginYOffset( node );
            
            node.setOffset( x, y );
        }
    }
}
