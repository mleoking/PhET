/* Copyright 2009, University of Colorado */
package edu.colorado.phet.acidbasesolutions.view.moleculecounts;

import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;

class AcidMoleculeCountsNode extends AbstractMoleculeCountsNode {
    
    public AcidMoleculeCountsNode() {
        super();
        setIconAndLabel( getReactantRow(), ABSImages.HA_MOLECULE, ABSSymbols.HA );
        setIconAndLabel( getProductRow(), ABSImages.A_MINUS_MOLECULE, ABSSymbols.A );
        setNegligibleEnabled( getReactantRow(), true, 0 /* negligibleThreshold */ );
    }
}