package edu.colorado.phet.acidbasesolutions.model.concentration;

import edu.colorado.phet.acidbasesolutions.model.Base;
import edu.colorado.phet.acidbasesolutions.model.Water;


public class StrongBaseEquilibriumModel extends BaseEquilibriumModel {
    
    protected StrongBaseEquilibriumModel( Base base ) {
        super( base );
    }
    
    // [MOH] = 0
    public double getBaseConcentration() {
        return 0;
    }
    
    // [M+] = c
    public double getMetalConcentration() {
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
    
    public double getMetalMoleculeCount() {
        return getMoleculeCount( getMetalConcentration() );
    }
}
