/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.acidbasesolutions.constants.ABSColors;
import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Base class for all canvases.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class ABSCanvas extends PhetPCanvas {
    
    private static final double MIN_MARGIN = 10;

    private final PNode rootNode;
    
    public ABSCanvas() {
        super( ABSConstants.CANVAS_RENDERING_SIZE );
        setBackground( ABSColors.CANVAS_BACKGROUND );
        
        // Root of our scene graph
        rootNode = new PNode();
        
        // add to "world" so that we get automatic scaling
        addWorldChild( rootNode );
    }    
    
    protected void addNode( PNode node ) {
        rootNode.addChild( node );
    }
    
    protected void centerRootNode() {
        Dimension2D worldSize = getWorldSize();
        PBounds b = rootNode.getFullBoundsReference();
        double xOffset = Math.max( MIN_MARGIN, ( worldSize.getWidth() -  b.getWidth() ) / 2 );
        double yOffset = Math.max( MIN_MARGIN, ( worldSize.getHeight() -  b.getHeight() ) / 2 );
        rootNode.setOffset( xOffset, yOffset );
    }
}
