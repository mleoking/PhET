package edu.colorado.phet.acidbasesolutions.model.bases;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;


public class Methylamine implements IWeakBase {
    
    public String getName() {
        return ABSStrings.METHYLAMINE;
    }

    public String getSymbol() {
        return ABSSymbols.CH3NH2;
    }
    
    public double getStrength() {
        return 4.4E-4;
    }
    
    public String getProductCationSymbol() {
        return ABSSymbols.CH3NH3;
    }
    
    public String getProductAnionSymbol() {
        return ABSSymbols.OH_MINUS;
    }
}
