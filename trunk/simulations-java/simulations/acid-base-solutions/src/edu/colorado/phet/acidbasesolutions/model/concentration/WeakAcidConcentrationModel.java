package edu.colorado.phet.acidbasesolutions.model.concentration;

import edu.colorado.phet.acidbasesolutions.model.Acid;
import edu.colorado.phet.acidbasesolutions.model.Water;



public class WeakAcidConcentrationModel extends AcidConcentrationModel {

    protected WeakAcidConcentrationModel( Acid acid ) {
        super( acid );
    }
    
    // [HA] = c - [A-]
    public double getAcidConcentration() {
        return getSolute().getConcentration() - getBaseConcentration();
    }
    
    // [A-] = ( -Ka + sqrt( Ka*Ka + 4*Ka*c ) ) / 2 
    public double getBaseConcentration() {
        final double Ka = getSolute().getStrength();
        final double c = getSolute().getConcentration();
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
        return Water.getConcentration() - getBaseConcentration();
    }
}
