/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view.moleculecounts;

import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;

public class StrongBaseMoleculeCountsNode extends AbstractMoleculeCountsNode {

    private static int BASE_ROW = 0;
    private static int METAL_ROW = 1;
    
    public StrongBaseMoleculeCountsNode() {
        super();
        setIconAndLabel( BASE_ROW, ABSImages.MOH_MOLECULE, ABSSymbols.MOH );
        setIconAndLabel( METAL_ROW, ABSImages.M_PLUS_MOLECULE, ABSSymbols.M_PLUS );
    }
    
    public void setBaseLabel( String text ) {
        setLabel( BASE_ROW, text );
    }

    public void setBaseCount( double count ) {
        setCount( BASE_ROW, count );
    }
    
    public void setMetalLabel( String text ) {
        setLabel( METAL_ROW, text );
    }

    public void setMetalCount( double count ) {
        setCount( METAL_ROW, count );
    }
}