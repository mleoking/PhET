package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.model.Acid.WeakAcid;



public class WeakAcidSolution extends Solution {

    private final WeakAcid acid;
    
    public WeakAcidSolution( WeakAcid acid, double initialConcentration ) {
        super( initialConcentration );
        this.acid = acid;
    }
    
    public WeakAcid getAcid() {
        return acid;
    }
    
    // [HA] = c - [A-]
    public double getAcidConcentration() {
        return getInitialConcentration() - getConjugateBaseConcentration();
    }
    
    // [A-]
    public double getConjugateBaseConcentration() {
        final double Ka = acid.getStrength();
        final double c = getInitialConcentration();
        return -Ka + Math.sqrt( ( Ka * Ka ) + ( 4 * Ka * c ) );
    }
    
    // [H3O+] = [A-]
    public double getH3OConcentration() {
        return getConjugateBaseConcentration();
    }
    
    // [OH-] = Kw / [H3O+]
    public double getOHConcentration() {
        return PureWater.getInstance().getEquilibriumConstant() / getH3OConcentration();
    }
    
    // [H2O] = W - [A-]
    public double getH2OConcentration() {
        return PureWater.getInstance().getConcentration() - getConjugateBaseConcentration();
    }
}
