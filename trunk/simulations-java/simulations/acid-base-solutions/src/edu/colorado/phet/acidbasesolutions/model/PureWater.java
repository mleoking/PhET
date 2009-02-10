package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;

/**
 * Pure water is not an aqueous solution.
 * A solution is formed when a solvent dissolves some substance.
 * In an aqueous solution, water is the solvent.
 * In pure water, there is no substance that is dissolves, and thus no solution.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PureWater implements IMolecule {
    
    private static final double W = 55.6; // concentration, mol/L
    private static final double Kw = 1E-14; // equilibrium constant

    public PureWater() {}
    
    public String getName() {
        return ABSStrings.PURE_WATER;
    }
    
    public String getSymbol() {
        return ABSSymbols.H2O;
    }
    
    public double getConcentration() {
        return W;
    }
    
    public double getEquilibriumConstant() {
        return Kw;
    }
    
    public double getHydroniumConcentration() {
        return 1E-7;
    }

    public double getHydroxideConcentration() {
        return 1E-7;
    }
}
