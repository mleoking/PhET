package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.model.Acid.StrongAcid;




public class StrongAcidSolution extends AqueousSolution {
    
    private final StrongAcid acid;
    
    public StrongAcidSolution( StrongAcid acid, double initialConcentration ) {
        super( initialConcentration );
        this.acid = acid;
    }
    
    public StrongAcid getAcid() {
        return acid;
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
    public double getHydroniumConcentration() {
        return getInitialConcentration();
    }
    
    // [OH-] = Kw / [H3O+]
    public double getHydroxideConcentration() {
        return Water.getEquilibriumConstant() / getHydroniumConcentration();
    }
    
    // [H2O] = W - c
    public double getWaterConcentration() {
        return getWater().getConcentration() - getInitialConcentration();
    }
}
