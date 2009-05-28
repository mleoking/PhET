package edu.colorado.phet.acidbasesolutions.view.graph;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.umd.cs.piccolo.util.PDimension;


class WeakBaseConcentrationGraphNode extends AbstractConcentrationGraphNode{

    public WeakBaseConcentrationGraphNode( PDimension outlineSize ) {
        super( outlineSize );
        setMolecule( getReactantIndex(), ABSImages.B_MOLECULE, ABSSymbols.B, ABSConstants.B_COLOR );
        setMolecule( getProductIndex(), ABSImages.BH_PLUS_MOLECULE, ABSSymbols.BH_PLUS, ABSConstants.BH_COLOR );
    }
}
