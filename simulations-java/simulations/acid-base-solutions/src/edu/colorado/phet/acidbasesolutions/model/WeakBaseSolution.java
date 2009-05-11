package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.model.Base.WeakBase;



public class WeakBaseSolution {

    private final WeakBase base;
    private double initialConcentration;
    
    public WeakBaseSolution( WeakBase base, double initialConcentration ) {
        this.base = base;
        this.initialConcentration = initialConcentration;
    }
    
    public WeakBase getBase() {
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
