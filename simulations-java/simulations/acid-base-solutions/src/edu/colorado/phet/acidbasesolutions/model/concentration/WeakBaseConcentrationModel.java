package edu.colorado.phet.acidbasesolutions.model.concentration;

import edu.colorado.phet.acidbasesolutions.model.Base;
import edu.colorado.phet.acidbasesolutions.model.Water;



public class WeakBaseConcentrationModel extends BaseConcentrationModel {

    protected WeakBaseConcentrationModel( Base base ) {
        super( base );
    }
    
    // [B] = c - [BH+]
    public double getBaseConcentration() {
        return getSolute().getConcentration() - getAcidConcentration();
    }
    
    // [BH+] = ( -Kb + sqrt( Kb*Kb + 4*Kb*c ) ) / 2 
    public double getAcidConcentration() {
        final double Kb = getSolute().getStrength();
        final double c = getSolute().getConcentration();
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
    
    public double getAcidMoleculeCount() {
        return getMoleculeCount( getAcidConcentration() );
    }
}
