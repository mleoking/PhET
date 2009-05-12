package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.model.Base.WeakBase;



public class WeakBaseConcentrationModel extends ConcentrationModel {

    protected WeakBaseConcentrationModel( WeakBase base ) {
        super( base );
    }
    
    // [B] = c - [BH+]
    public double getBaseConcentration() {
        return getInitialConcentration() - getAcidConcentration();
    }
    
    // [BH+] = ( -Kb + sqrt( Kb*Kb + 4*Kb*c ) ) / 2 
    public double getAcidConcentration() {
        final double Kb = getSolute().getStrength();
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
        return Water.getConcentration() - getAcidConcentration();
    }
}
