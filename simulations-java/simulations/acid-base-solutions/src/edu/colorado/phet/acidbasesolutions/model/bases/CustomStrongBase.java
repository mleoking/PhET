package edu.colorado.phet.acidbasesolutions.model.bases;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;


public class CustomStrongBase implements IStrongBase {

    public CustomStrongBase() {}
    
    public String getName() {
        return ABSStrings.CUSTOM_STRONG_BASE;
    }
    
    public String getSymbol() {
        return ABSSymbols.MOH;
    }

    public String getProductCationSymbol() {
        return ABSSymbols.M;
    }
    
    public String getProductAnionSymbol() {
        return ABSSymbols.OH;
    }
}
