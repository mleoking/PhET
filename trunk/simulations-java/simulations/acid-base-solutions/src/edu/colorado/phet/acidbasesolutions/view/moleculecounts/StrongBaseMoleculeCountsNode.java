/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view.moleculecounts;

import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;

class StrongBaseMoleculeCountsNode extends AbstractMoleculeCountsNode {

    public StrongBaseMoleculeCountsNode() {
        super();
        setIconAndLabel( getReactantRow(), ABSImages.MOH_MOLECULE, ABSSymbols.MOH );
        setIconAndLabel( getProductRow(), ABSImages.M_PLUS_MOLECULE, ABSSymbols.M_PLUS );
    }
}