package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.model.Acid.*;



public class WeakAcidSolution extends AqueousSolution {

    private final WeakAcid acid;
    
    protected WeakAcidSolution( WeakAcid acid ) {
        super( acid );
        this.acid = acid;
    }
    
    public WeakAcid getAcid() {
        return acid;
    }
    
    // [HA] = c - [A-]
    public double getAcidConcentration() {
        return getInitialConcentration() - getBaseConcentration();
    }
    
    // [A-] = ( -Ka + sqrt( Ka*Ka + 4*Ka*c ) ) / 2 
    public double getBaseConcentration() {
        final double Ka = acid.getStrength();
        final double c = getInitialConcentration();
        return ( -Ka + Math.sqrt( ( Ka * Ka ) + ( 4 * Ka * c ) ) ) / 2;
    }
    
    // [H3O+] = [A-]
    public double getH3OConcentration() {
        return getBaseConcentration();
    }
    
    // [OH-] = Kw / [H3O+]
    public double getOHConcentration() {
        return Water.getEquilibriumConstant() / getH3OConcentration();
    }
    
    // [H2O] = W - [A-]
    public double getH2OConcentration() {
        return getWater().getConcentration() - getBaseConcentration();
    }
    
    public static class AceticAcidSolution extends WeakAcidSolution {
        public AceticAcidSolution() {
            super( new AceticAcid() );
        }
    }
    
    public static class ChlorousAcidSolution extends WeakAcidSolution {
        public ChlorousAcidSolution() {
            super( new ChlorousAcid() );
        }
    }
    
    public static class HydrofluoricAcidSolution extends WeakAcidSolution {
        public HydrofluoricAcidSolution() {
            super( new HydrofluoricAcid() );
        }
    }
    
    public static class HypochlorusAcidSolution extends WeakAcidSolution {
        public HypochlorusAcidSolution() {
            super( new HypochlorusAcid() );
        }
    }

    public static class CustomWeakAcidSolution extends WeakAcidSolution {

        public CustomWeakAcidSolution() {
            super( new CustomWeakAcid() );
        }

        // public for custom
        public void setStrength( double strength ) {
            super.setStrength( strength );
        }
    }
}
