package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.model.Base.StrongBase;




public class StrongBaseSolution extends AqueousSolution {
    
    private final StrongBase base;
    
    public StrongBaseSolution( StrongBase base, double initialConcentration ) {
        super( initialConcentration );
        this.base = base;
    }
    
    public StrongBase getBase() {
        return base;
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
        return Water.getEquilibriumConstant() / getHydroniumConcentration();
    }
    
    // [OH-] = c
    public double getHydroxideConcentration() {
        return getInitialConcentration();
    }
    
    // [H2O] = W
    public double getWaterConcentration() {
        return getWater().getConcentration();
    }
}
