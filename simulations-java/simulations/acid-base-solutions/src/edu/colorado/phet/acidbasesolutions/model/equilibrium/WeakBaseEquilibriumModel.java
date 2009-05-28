package edu.colorado.phet.acidbasesolutions.model.equilibrium;

import edu.colorado.phet.acidbasesolutions.model.Base;
import edu.colorado.phet.acidbasesolutions.model.Water;



public class WeakBaseEquilibriumModel extends EquilibriumModel {

    protected WeakBaseEquilibriumModel( Base base ) {
        super( base );
    }
    
    // [B] = c - [BH+]
    public double getReactantConcentration() {
        return getSolute().getConcentration() - getProductConcentration();
    }
    
    // [BH+] = ( -Kb + sqrt( Kb*Kb + 4*Kb*c ) ) / 2 
    public double getProductConcentration() {
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
        return getProductConcentration();
    }
    
    // [H2O] = W - [BH+]
    public double getH2OConcentration() {
        return Water.getConcentration() - getProductConcentration();
    }
    
    public double getAcidMoleculeCount() {
        return getMoleculeCount( getProductConcentration() );
    }
}
