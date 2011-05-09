// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.advancedacidbasesolutions.model.equilibrium;

import edu.colorado.phet.advancedacidbasesolutions.model.Acid;
import edu.colorado.phet.advancedacidbasesolutions.model.Water;


/**
 * Equilibrium model for weak acids.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WeakAcidEquilibriumModel extends AbstractEquilibriumModel {

    protected WeakAcidEquilibriumModel( Acid acid ) {
        super( acid );
    }
    
    // [HA] = c - [A-]
    public double getReactantConcentration() {
        return getSolute().getConcentration() - getProductConcentration();
    }
    
    // [A-] = ( -Ka + sqrt( Ka*Ka + 4*Ka*c ) ) / 2 
    public double getProductConcentration() {
        final double Ka = getSolute().getStrength();
        final double c = getSolute().getConcentration();
        return ( -Ka + Math.sqrt( ( Ka * Ka ) + ( 4 * Ka * c ) ) ) / 2;
    }
    
    // [H3O+] = [A-]
    public double getH3OConcentration() {
        return getProductConcentration();
    }
    
    // [OH-] = Kw / [H3O+]
    public double getOHConcentration() {
        return Water.getEquilibriumConstant() / getH3OConcentration();
    }
    
    // [H2O] = W - [A-]
    public double getH2OConcentration() {
        return Water.getConcentration() - getProductConcentration();
    }
}
