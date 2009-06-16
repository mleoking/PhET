/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module;

import java.awt.geom.Dimension2D;

import javax.swing.JComponent;

import edu.colorado.phet.acidbasesolutions.ABSColors;
import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.control.ABSResetAllButton;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Base class for all canvases.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSAbstractCanvas extends PhetPCanvas {

    private final PNode rootNode;
    private final PNode resetAllButtonWrapper;
    
    public ABSAbstractCanvas( Resettable resettable ) {
        super( ABSConstants.CANVAS_RENDERING_SIZE );
        setBackground( ABSColors.CANVAS_BACKGROUND );
        
        // Root of our scene graph
        rootNode = new PNode();
        
        // add to "world" so that we get automatic scaling
        addWorldChild( rootNode );
        
        JComponent resetAllButton = new ABSResetAllButton( resettable, this );
        resetAllButtonWrapper = new PSwing( resetAllButton );
        rootNode.addChild( resetAllButtonWrapper );
    }    
    
    protected void addNode( PNode node ) {
        rootNode.addChild( node );
    }
    
    protected PNode getResetAllButton() {
        return resetAllButtonWrapper;
    }
    
    protected void centerRootNode() {
        Dimension2D worldSize = getWorldSize();
        PBounds b = rootNode.getFullBoundsReference();
        double xOffset = Math.max( 10, ( worldSize.getWidth() -  b.getWidth() ) / 2 );
        double yOffset = Math.max( 10, ( worldSize.getHeight() -  b.getHeight() ) / 2 );
        rootNode.setOffset( xOffset, yOffset );
    }
}
