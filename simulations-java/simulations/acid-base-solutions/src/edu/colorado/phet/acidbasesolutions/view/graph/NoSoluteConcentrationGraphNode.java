package edu.colorado.phet.acidbasesolutions.view.graph;

import edu.umd.cs.piccolo.util.PDimension;



class NoSoluteConcentrationGraphNode extends AbstractConcentrationGraphNode {
    
    public NoSoluteConcentrationGraphNode( PDimension outlineSize ) {
        super( outlineSize );
        setVisible( 0, false );
        setVisible( 1, false );
    }
}
