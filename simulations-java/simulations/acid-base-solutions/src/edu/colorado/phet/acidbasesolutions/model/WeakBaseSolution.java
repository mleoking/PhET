package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.model.Base.WeakBase;



public class WeakBaseSolution extends Solution {

    private final WeakBase base;
    
    public WeakBaseSolution( WeakBase base, double initialConcentration ) {
        super( initialConcentration );
        this.base = base;
    }
    
    public WeakBase getBase() {
        return base;
    }
    
    // [B] = c - [BH+]
    public double getBaseConcentration() {
        return getInitialConcentration() - getConjugateAcidConcentration();
    }
    
    // [BH+]
    public double getConjugateAcidConcentration() {
        final double Kb = base.getStrength();
        final double c = getInitialConcentration();
        return -Kb + Math.sqrt( ( Kb * Kb ) + ( 4 * Kb * c ) );
    }
    
    // [H3O+] = Kw / [OH-]
    public double getH3OConcentration() {
        return PureWater.getInstance().getEquilibriumConstant() / getOHConcentration();
    }
    
    // [OH-] = [BH+]
    public double getOHConcentration() {
        return getConjugateAcidConcentration();
    }
    
    // [H2O] = W - [BH+]
    public double getH2OConcentration() {
        return PureWater.getInstance().getConcentration() - getConjugateAcidConcentration();
    }
}
