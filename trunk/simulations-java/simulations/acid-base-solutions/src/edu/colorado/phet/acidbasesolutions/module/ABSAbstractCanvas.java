/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module;

import javax.swing.JComponent;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.control.ABSResetAllButton;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Base class for all canvases.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSAbstractCanvas extends PhetPCanvas {

    private final PNode _rootNode;
    private final PNode _resetAllButtonWrapper;
    
    public ABSAbstractCanvas( Resettable resettable ) {
        super( ABSConstants.CANVAS_RENDERING_SIZE );
        setBackground( ABSConstants.CANVAS_BACKGROUND );
        
        // Root of our scene graph
        _rootNode = new PNode();
        addWorldChild( _rootNode );
        
        JComponent resetAllButton = new ABSResetAllButton( resettable, this );
        _resetAllButtonWrapper = new PSwing( resetAllButton );
        _rootNode.addChild( _resetAllButtonWrapper );
    }    
    
    protected void addNode( PNode node ) {
        _rootNode.addChild( node );
    }
    
    protected PNode getResetAllButton() {
        return _resetAllButtonWrapper;
    }
}
