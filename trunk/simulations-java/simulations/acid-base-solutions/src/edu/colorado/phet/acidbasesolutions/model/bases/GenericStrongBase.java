package edu.colorado.phet.acidbasesolutions.model.bases;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;


public class GenericStrongBase implements IStrongBase {

    public GenericStrongBase() {}
    
    public String getName() {
        return ABSStrings.STRONG_BASE;
    }
    
    public String getSymbol() {
        return ABSSymbols.MOH;
    }

    public String getCationSymbol() {
        return ABSSymbols.M;
    }

}
