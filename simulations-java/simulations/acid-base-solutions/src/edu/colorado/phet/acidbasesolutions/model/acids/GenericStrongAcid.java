package edu.colorado.phet.acidbasesolutions.model.acids;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;


public class GenericStrongAcid implements IStrongAcid {

    public GenericStrongAcid() {}
    
    public String getName() {
        return ABSStrings.STRONG_ACID;
    }
    
    public String getSymbol() {
        return ABSSymbols.HA;
    }

    public String getConjugateBaseSymbol() {
        return ABSSymbols.A;
    }
}
