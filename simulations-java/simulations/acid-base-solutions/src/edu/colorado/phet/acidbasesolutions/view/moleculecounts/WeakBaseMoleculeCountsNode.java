/* Copyright 2009, University of Colorado */
package edu.colorado.phet.acidbasesolutions.view.moleculecounts;

import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;

class WeakBaseMoleculeCountsNode extends AbstractMoleculeCountsNode {

    public WeakBaseMoleculeCountsNode() {
        super();
        setIconAndLabel( getReactantRow(), ABSImages.B_MOLECULE, ABSSymbols.B );
        setIconAndLabel( getProductRow(), ABSImages.BH_PLUS_MOLECULE, ABSSymbols.BH_PLUS );
    }
}