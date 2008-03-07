/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import edu.colorado.phet.glaciers.model.Glacier;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * GlacierNode is the parent node for all parts of the glacier's visual representation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlacierNode extends PComposite {

    private PNode _iceFlowNode;
    
    public GlacierNode( Glacier glacier, ModelViewTransform mvt ) {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        PNode iceNode = new IceNode( glacier, mvt );
        addChild( iceNode );
        
        _iceFlowNode = new IceFlowNode( glacier, mvt );
        addChild( _iceFlowNode );
    }
    
    public void setIceFlowVisible( boolean visible ) {
        _iceFlowNode.setVisible( visible );
    }
}
