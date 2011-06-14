// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.advancedacidbasesolutions.module;

import java.awt.geom.Dimension2D;

import javax.swing.JComponent;

import edu.colorado.phet.advancedacidbasesolutions.AABSColors;
import edu.colorado.phet.advancedacidbasesolutions.AABSConstants;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.ResetAllButton;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Base class for all canvases.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AABSAbstractCanvas extends PhetPCanvas {
    
    private static final double MIN_MARGIN = 10;

    private final PNode rootNode;
    private final PNode resetAllButtonWrapper;
    
    public AABSAbstractCanvas( Resettable resettable ) {
        super( AABSConstants.CANVAS_RENDERING_SIZE );
        setBackground( AABSColors.CANVAS_BACKGROUND );
        
        // Root of our scene graph
        rootNode = new PNode();
        
        // add to "world" so that we get automatic scaling
        addWorldChild( rootNode );
        
        JComponent resetAllButton = new ResetAllButton( resettable, this );
        resetAllButton.setOpaque( false ); // Mac OS 10.4
        resetAllButtonWrapper = new PSwing( resetAllButton );
        resetAllButtonWrapper.scale( AABSConstants.PSWING_SCALE );
        resetAllButtonWrapper.addInputEventListener( new CursorHandler() );
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
        double xOffset = Math.max( MIN_MARGIN, ( worldSize.getWidth() -  b.getWidth() ) / 2 );
        double yOffset = Math.max( MIN_MARGIN, ( worldSize.getHeight() -  b.getHeight() ) / 2 );
        rootNode.setOffset( xOffset, yOffset );
    }
}
