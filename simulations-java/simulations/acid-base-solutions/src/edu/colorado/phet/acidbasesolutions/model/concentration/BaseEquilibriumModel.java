package edu.colorado.phet.acidbasesolutions.model.concentration;

import edu.colorado.phet.acidbasesolutions.model.Base;


public abstract class BaseEquilibriumModel extends EquilibriumModel {

    protected BaseEquilibriumModel( Base base ) {
        super( base );
    }

    public abstract double getBaseConcentration();
    
    public double getBaseMoleculeCount() {
        return getMoleculeCount( getBaseConcentration() );
    }
}
