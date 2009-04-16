package edu.colorado.phet.acidbasesolutions.model.acids;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;


public class CustomStrongAcid implements IStrongAcid {
    
    public CustomStrongAcid() {}
    
    public String getName() {
        return ABSStrings.CUSTOM_STRONG_ACID;
    }
    
    public String getSymbol() {
        return ABSSymbols.HA;
    }
    
    public String getProductCationSymbol() {
        return ABSSymbols.A_MINUS;
    }
    
    public String getProductAnionSymbol() {
        return ABSSymbols.OH_MINUS;
    }
}
