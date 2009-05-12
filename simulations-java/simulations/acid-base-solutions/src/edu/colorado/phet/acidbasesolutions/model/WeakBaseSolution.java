package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.model.Base.WeakBase;



public class WeakBaseSolution extends AqueousSolution {

    private final WeakBase base;
    
    public WeakBaseSolution( WeakBase base ) {
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
}
