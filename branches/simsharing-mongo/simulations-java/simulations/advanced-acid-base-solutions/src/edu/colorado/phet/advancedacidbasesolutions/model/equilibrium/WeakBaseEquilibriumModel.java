// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.advancedacidbasesolutions.model.equilibrium;

import edu.colorado.phet.advancedacidbasesolutions.model.Base;
import edu.colorado.phet.advancedacidbasesolutions.model.Water;


/**
 * Equilibrium model for weak bases.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WeakBaseEquilibriumModel extends AbstractEquilibriumModel {

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
}
