package edu.colorado.phet.acidbasesolutions.view.graph;

import edu.umd.cs.piccolo.util.PDimension;



class NoSoluteConcentrationGraphNode extends AbstractConcentrationGraphNode {
    
    public NoSoluteConcentrationGraphNode( PDimension outlineSize ) {
        super( outlineSize );
        setVisible( getReactantIndex(), false );
        setVisible( getProductIndex(), false );
    }
}
