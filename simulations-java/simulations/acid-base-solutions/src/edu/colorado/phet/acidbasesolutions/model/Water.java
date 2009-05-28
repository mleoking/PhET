package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;

/**
 * Water.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Water extends Molecule {
    
    private static final double W = 55.6; // concentration, mol/L
    private static final double Kw = 1E-14; // equilibrium constant

    public Water() {
        super( ABSStrings.WATER, ABSSymbols.H2O, ABSImages.H2O_MOLECULE );
    }
    
    public static double getConcentration() {
        return W;
    }
    
    public static double getEquilibriumConstant() {
        return Kw;
    }
}
