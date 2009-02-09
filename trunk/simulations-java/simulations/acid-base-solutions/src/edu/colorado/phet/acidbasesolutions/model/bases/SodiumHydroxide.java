package edu.colorado.phet.acidbasesolutions.model.bases;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;


public class SodiumHydroxide implements IStrongBase {
    
    public String getName() {
        return ABSStrings.SODIUM_HYDROXIDE;
    }

    public String getSymbol() {
        return ABSSymbols.NaOH;
    }

    public String getCationSymbol() {
        return ABSSymbols.Na;
    }
}
