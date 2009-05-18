/* Copyright 2009, University of Colorado */
package edu.colorado.phet.acidbasesolutions.view.moleculecounts;

import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;

public class AcidMoleculeCountsNode extends AbstractMoleculeCountsNode {
    
    private static int ACID_ROW = 0;
    private static int BASE_ROW = 1;

    public AcidMoleculeCountsNode() {
        super();
        setIconAndLabel( ACID_ROW, ABSImages.HA_MOLECULE, ABSSymbols.HA );
        setIconAndLabel( BASE_ROW, ABSImages.A_MINUS_MOLECULE, ABSSymbols.A );
        setNegligibleEnabled( ACID_ROW, true, 0 /* negligibleThreshold */ );
    }
    
    public void setAcidLabel( String text ) {
        setLabel( ACID_ROW, text );
    }

    public void setAcidCount( double count ) {
        setCount( ACID_ROW, count );
    }
    
    public void setBaseLabel( String text ) {
        setLabel( BASE_ROW, text );
    }

    public void setBaseCount( double count ) {
        setCount( BASE_ROW, count );
    }
}