/* Copyright 2009, University of Colorado */
package edu.colorado.phet.acidbasesolutions.view.moleculecounts;

import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;

class WeakBaseMoleculeCountsNode extends AbstractMoleculeCountsNode {

    private static int BASE_ROW = 0;
    private static int ACID_ROW = 1;
    
    public WeakBaseMoleculeCountsNode() {
        super();
        setIconAndLabel( BASE_ROW, ABSImages.B_MOLECULE, ABSSymbols.B );
        setIconAndLabel( ACID_ROW, ABSImages.BH_PLUS_MOLECULE, ABSSymbols.BH_PLUS );
    }

    public void setBaseLabel( String text ) {
        setLabel( BASE_ROW, text );
    }
    
    public void setBaseCount( double count ) {
        setCount( BASE_ROW, count );
    }
    
    public void setAcidLabel( String text ) {
        setLabel( ACID_ROW, text );
    }

    public void setAcidCount( double count ) {
        setCount( ACID_ROW, count );
    }
}