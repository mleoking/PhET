/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import edu.colorado.phet.glaciers.model.Glacier;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;


public class GlacierNode extends PComposite {

    private PNode _iceVelocitiesNode;
    
    public GlacierNode( Glacier glacier, ModelViewTransform mvt ) {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        PNode iceNode = new IceNode( glacier, mvt );
        addChild( iceNode );
        
        _iceVelocitiesNode = new IceVelocitiesNode( glacier, mvt );
        addChild( _iceVelocitiesNode );
    }
    
    public void setIceVelocitiesNodeVisible( boolean visible ) {
        _iceVelocitiesNode.setVisible( visible );
    }
}
