package edu.colorado.phet.acidbasesolutions.model.bases;

import edu.colorado.phet.acidbasesolutions.model.PureWater;



public class StrongBaseSolution {
    
    private final StrongBase base;
    private double initialConcentration;
    
    public StrongBaseSolution( StrongBase base, double initialConcentration ) {
        this.base = base;
        this.initialConcentration = initialConcentration;
    }
    
    public StrongBase getBase() {
        return base;
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
    
    // [MOH] = 0
    public double getBaseConcentration() {
        return 0;
    }
    
    // [M+] = c
    public double getMetalConcentration() {
        return getInitialConcentration();
    }
    
    // [H3O+] = Kw / [OH-]
    public double getHydroniumConcentration() {
        return PureWater.getInstance().getEquilibriumConstant() / getHydroniumConcentration();
    }
    
    // [OH-] = c
    public double getHydroxideConcentration() {
        return getInitialConcentration();
    }
    
    // [H2O] = W
    public double getWaterConcentration() {
        return PureWater.getInstance().getConcentration();
    }
}
