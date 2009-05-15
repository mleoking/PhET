package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.ABSStrings;


public class NullSolute extends Solute {
    
    public NullSolute() {
        super( null, null, 0 );
    }

    protected boolean isValidStrength( double strength ) {
        return true;
    }
    
    public String toString() {
        return ABSStrings.NO_SOLUTE;
    }

}
