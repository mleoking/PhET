package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.model.Base.CustomStrongBase;
import edu.colorado.phet.acidbasesolutions.model.Base.StrongBase;




public class StrongBaseSolution extends AqueousSolution {
    
    private final StrongBase base;
    
    public StrongBaseSolution( StrongBase base ) {
        super( base );
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
    public double getH3OConcentration() {
        return Water.getEquilibriumConstant() / getH3OConcentration();
    }
    
    // [OH-] = c
    public double getOHConcentration() {
        return getInitialConcentration();
    }
    
    // [H2O] = W
    public double getH2OConcentration() {
        return getWater().getConcentration();
    }
    
    public static class CustomStrongBaseSolution extends StrongBaseSolution {
        
        public CustomStrongBaseSolution() {
            super( new CustomStrongBase() );
        }
        
        // public for custom
        public void setStrength( double strength ) {
            super.setStrength( strength );
        }
    }
}
