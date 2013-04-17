// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.advancedacidbasesolutions.model.equilibrium;

import edu.colorado.phet.advancedacidbasesolutions.model.Base;
import edu.colorado.phet.advancedacidbasesolutions.model.Water;

/**
 * Equilibrium model for strong bases.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class StrongBaseEquilibriumModel extends AbstractEquilibriumModel {
    
    protected StrongBaseEquilibriumModel( Base base ) {
        super( base );
    }
    
    // [MOH] = 0
    public double getReactantConcentration() {
        return 0;
    }
    
    // [M+] = c
    public double getProductConcentration() {
        return getSolute().getConcentration();
    }
    
    // [H3O+] = Kw / [OH-]
    public double getH3OConcentration() {
        return Water.getEquilibriumConstant() / getOHConcentration();
    }
    
    // [OH-] = c
    public double getOHConcentration() {
        return getSolute().getConcentration();
    }
    
    // [H2O] = W
    public double getH2OConcentration() {
        return Water.getConcentration();
    }
}
