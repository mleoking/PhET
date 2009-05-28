package edu.colorado.phet.acidbasesolutions.view.graph;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.umd.cs.piccolo.util.PDimension;


class StrongBaseConcentrationGraphNode extends AbstractConcentrationGraphNode{

    public StrongBaseConcentrationGraphNode( PDimension outlineSize ) {
        super( outlineSize );
        setMolecule( getReactantIndex(), ABSImages.MOH_MOLECULE, ABSSymbols.MOH, ABSConstants.MOH_COLOR );
        setMolecule( getProductIndex(), ABSImages.M_PLUS_MOLECULE, ABSSymbols.M_PLUS, ABSConstants.M_COLOR );
    }
}
