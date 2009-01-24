package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;


public class PureWater {
    
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

}
