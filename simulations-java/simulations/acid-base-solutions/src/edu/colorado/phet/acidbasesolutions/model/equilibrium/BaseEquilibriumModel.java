package edu.colorado.phet.acidbasesolutions.model.equilibrium;

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
