package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.model.Acid.CustomStrongAcid;
import edu.colorado.phet.acidbasesolutions.model.Acid.HydrochloricAcid;
import edu.colorado.phet.acidbasesolutions.model.Acid.PerchloricAcid;
import edu.colorado.phet.acidbasesolutions.model.Acid.StrongAcid;




public class StrongAcidSolution extends AqueousSolution {
    
    private final StrongAcid acid;
    
    protected StrongAcidSolution( StrongAcid acid ) {
        super( acid );
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
    public double getH3OConcentration() {
        return getInitialConcentration();
    }
    
    // [OH-] = Kw / [H3O+]
    public double getOHConcentration() {
        return Water.getEquilibriumConstant() / getH3OConcentration();
    }
    
    // [H2O] = W - c
    public double getH2OConcentration() {
        return getWater().getConcentration() - getInitialConcentration();
    }
    
    public static class HydrochloricAcidSolution extends StrongAcidSolution {
        public HydrochloricAcidSolution() {
            super( new HydrochloricAcid() );
        }
    }
    
    public static class PerchloricAcidSolution extends StrongAcidSolution {
        public PerchloricAcidSolution() {
            super( new PerchloricAcid() );
        }
    }
    
    public static class CustomStrongAcidSolution extends StrongAcidSolution {
        
        public CustomStrongAcidSolution() {
            super( new CustomStrongAcid() );
        }
        
        // public for custom
        public void setStrength( double strength ) {
            super.setStrength( strength );
        }
    }
}
