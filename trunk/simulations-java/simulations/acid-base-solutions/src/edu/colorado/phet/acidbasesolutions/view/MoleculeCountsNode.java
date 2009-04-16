package edu.colorado.phet.acidbasesolutions.view;

import edu.colorado.phet.common.piccolophet.PhetPNode;


public class MoleculeCountsNode extends PhetPNode {

    public MoleculeCountsNode() {
        super();
        
        // this node is not interactive
        setPickable( false );
        setChildrenPickable( false );
    }
}
