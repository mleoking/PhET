package edu.colorado.phet.acidbasesolutions.view.graph;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.umd.cs.piccolo.util.PDimension;


class AcidConcentrationGraphNode extends AbstractConcentrationGraphNode {
    
    public AcidConcentrationGraphNode( PDimension outlineSize ) {
        super( outlineSize );
        setMolecule( getReactantIndex(), ABSImages.HA_MOLECULE, ABSSymbols.HA, ABSConstants.HA_COLOR );
        setMolecule( getProductIndex(), ABSImages.A_MINUS_MOLECULE, ABSSymbols.A_MINUS, ABSConstants.A_COLOR );
        setNegligibleEnabled( getReactantIndex(), true, 0 /* negligibleThreshold */ );
    }
}
