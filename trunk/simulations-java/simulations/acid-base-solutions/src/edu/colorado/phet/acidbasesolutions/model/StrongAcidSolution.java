package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.model.Acid.StrongAcid;




public class StrongAcidSolution {
    
    private final StrongAcid acid;
    private double initialConcentration;
    
    public StrongAcidSolution( StrongAcid acid, double initialConcentration ) {
        this.acid = acid;
        this.initialConcentration = initialConcentration;
    }
    
    public StrongAcid getAcid() {
        return acid;
    }
    
    // c
    public void setInitialAcidConcentration( double initialConcentration ) {
        if ( initialConcentration != this.initialConcentration ) {
            this.initialConcentration = initialConcentration;
        }
    }
    
    public double getInitialConcentration() {
        return initialConcentration;
    }
    
    // [HA] = 0
    public double getAcidConcentration() {
        return 0;
    }
    
    // [A-] = c
    public double getConjugateBaseConcentration() {
        return getInitialConcentration();
    }
    
    // [H3O+] = c
    public double getHydroniumConcentration() {
        return getInitialConcentration();
    }
    
    // [OH-] = Kw / [H3O+]
    public double getHydroxideConcentration() {
        return PureWater.getInstance().getEquilibriumConstant() / getHydroniumConcentration();
    }
    
    // [H2O] = W - c
    public double getWaterConcentration() {
        return PureWater.getInstance().getConcentration() - getInitialConcentration();
    }
}
