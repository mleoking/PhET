package edu.colorado.phet.acidbasesolutions.model.concentration;

import edu.colorado.phet.acidbasesolutions.model.Acid;
import edu.colorado.phet.acidbasesolutions.model.Water;
import edu.colorado.phet.acidbasesolutions.model.Acid.StrongAcid;

public class StrongAcidConcentrationModel extends ConcentrationModel {
    
    protected StrongAcidConcentrationModel( StrongAcid acid ) {
        super( acid );
    }
    
    // [HA] = 0
    public double getAcidConcentration() {
        return 0;
    }
    
    // [A-] = c
    public double getBaseConcentration() {
        return getInitialConcentration();
    }
    
    // [H3O+] = c
    public double getH3OConcentration() {
        return getInitialConcentration();
    }
    
    // [OH-] = Kw / [H3O+]
    public double getOHConcentration() {
        return Water.getEquilibriumConstant() / getH3OConcentration();
    }
    
    // [H2O] = W - c
    public double getH2OConcentration() {
        return Water.getConcentration() - getInitialConcentration();
    }
}
