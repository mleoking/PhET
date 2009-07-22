package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.ABSStrings;

/**
 * Represents the absences of a solute.
 * Using an instance of this instead of null simplifies code.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NoSolute extends Solute {
    
    public NoSolute() {
        super( ABSStrings.NO_SOLUTE, "", null, null, null, "", null, null, null, 0 );
    }
    
    public String getStrengthSymbol() {
        return null;
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
    
    public boolean isReactionBidirectional() {
        return false;
    }
}
