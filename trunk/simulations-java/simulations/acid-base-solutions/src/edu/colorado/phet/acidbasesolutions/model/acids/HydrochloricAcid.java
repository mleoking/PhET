package edu.colorado.phet.acidbasesolutions.model.acids;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;


public class HydrochloricAcid implements IStrongAcid {

    public String getName() {
        return ABSStrings.HYDORCHLORIC_ACID;
    }
    
    public String getSymbol() {
        return ABSSymbols.HCl;
    }

    public String getConjugateBaseSymbol() {
        return ABSSymbols.Cl;
    }
}
