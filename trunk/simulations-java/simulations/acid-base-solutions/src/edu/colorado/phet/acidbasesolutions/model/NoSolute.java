package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.ABSStrings;


public class NoSolute extends Solute {
    
    public NoSolute() {
        super( ABSStrings.NO_SOLUTE, "", 0 );
    }

    protected boolean isValidStrength( double strength ) {
        return true;
    }
    
    public String toString() {
        return getName();
    }

}
