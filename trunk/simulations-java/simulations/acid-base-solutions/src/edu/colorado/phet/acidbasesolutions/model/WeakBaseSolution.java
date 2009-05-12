package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.model.Base.Ammonia;
import edu.colorado.phet.acidbasesolutions.model.Base.CustomWeakBase;
import edu.colorado.phet.acidbasesolutions.model.Base.Pyridine;
import edu.colorado.phet.acidbasesolutions.model.Base.WeakBase;



public class WeakBaseSolution extends AqueousSolution {

    private final WeakBase base;
    
    protected WeakBaseSolution( WeakBase base ) {
        super( base );
        this.base = base;
    }
    
    public WeakBase getBase() {
        return base;
    }
    
    // [B] = c - [BH+]
    public double getBaseConcentration() {
        return getInitialConcentration() - getAcidConcentration();
    }
    
    // [BH+] = ( -Kb + sqrt( Kb*Kb + 4*Kb*c ) ) / 2 
    public double getAcidConcentration() {
        final double Kb = base.getStrength();
        final double c = getInitialConcentration();
        return (-Kb + Math.sqrt( ( Kb * Kb ) + ( 4 * Kb * c ) ) ) / 2;
    }
    
    // [H3O+] = Kw / [OH-]
    public double getH3OConcentration() {
        return Water.getEquilibriumConstant() / getOHConcentration();
    }
    
    // [OH-] = [BH+]
    public double getOHConcentration() {
        return getAcidConcentration();
    }
    
    // [H2O] = W - [BH+]
    public double getH2OConcentration() {
        return getWater().getConcentration() - getAcidConcentration();
    }
    
    public static class AmmoniaSolution extends WeakBaseSolution {
        public AmmoniaSolution() {
            super( new Ammonia() );
        }
    }
    
    public static class PyridineSolution extends WeakBaseSolution {
        public PyridineSolution() {
            super( new Pyridine() );
        }
    }
    
    public static class CustomWeakBaseSolution extends WeakBaseSolution {
        
        public CustomWeakBaseSolution() {
            super( new CustomWeakBase() );
        }
        
        // public for custom
        public void setStrength( double strength ) {
            super.setStrength( strength );
        }
    }
}
