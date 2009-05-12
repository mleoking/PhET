package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;

/**
 * Pure water.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Water {
    
    private static final double W = 55.6; // concentration, mol/L
    private static final double Kw = 1E-14; // equilibrium constant
    private static final double H3O_CONCENTRATION = 1E-7;
    private static final double OH_CONCENTRATION = 1E-7;

    public Water() {}
    
    public String getName() {
        return ABSStrings.PURE_WATER;
    }
    
    public String getSymbol() {
        return ABSSymbols.H2O;
    }
    
    public double getConcentration() {
        return W;
    }
    
    public static double getEquilibriumConstant() {
        return Kw;
    }
    
    public double getH3OConcentration() {
        return H3O_CONCENTRATION;
    }

    public double getOHConcentration() {
        return OH_CONCENTRATION;
    }
}
