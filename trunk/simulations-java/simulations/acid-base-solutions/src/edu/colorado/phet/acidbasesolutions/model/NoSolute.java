package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.ABSStrings;


public class NoSolute extends Solute {
    
    public NoSolute() {
        super( ABSStrings.NO_SOLUTE, "", null, "", null, 0 );
    }
    
    public boolean isZeroNegligible() {
        return false;
    }

    protected boolean isValidStrength( double strength ) {
        return true;
    }
    
    public String toString() {
        return getName();
    }
}
