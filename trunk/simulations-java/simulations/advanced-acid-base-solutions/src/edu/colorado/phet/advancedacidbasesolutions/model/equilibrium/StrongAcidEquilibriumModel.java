package edu.colorado.phet.acidbasesolutions.model.equilibrium;

import edu.colorado.phet.acidbasesolutions.model.Acid;
import edu.colorado.phet.acidbasesolutions.model.Water;

/**
 * Equilibrium model for strong acids.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class StrongAcidEquilibriumModel extends AbstractEquilibriumModel {
    
    protected StrongAcidEquilibriumModel( Acid acid ) {
        super( acid );
    }
    
    // [HA] = 0
    public double getReactantConcentration() {
        return 0;
    }
    
    // [A-] = c
    public double getProductConcentration() {
        return getSolute().getConcentration();
    }
    
    // [H3O+] = c
    public double getH3OConcentration() {
        return getSolute().getConcentration();
    }
    
    // [OH-] = Kw / [H3O+]
    public double getOHConcentration() {
        return Water.getEquilibriumConstant() / getH3OConcentration();
    }
    
    // [H2O] = W - c
    public double getH2OConcentration() {
        return Water.getConcentration() - getSolute().getConcentration();
    }
}
