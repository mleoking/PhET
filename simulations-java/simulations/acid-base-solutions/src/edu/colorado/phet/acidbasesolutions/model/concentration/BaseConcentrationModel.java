package edu.colorado.phet.acidbasesolutions.model.concentration;

import edu.colorado.phet.acidbasesolutions.model.Base;


public abstract class BaseConcentrationModel extends ConcentrationModel {

    protected BaseConcentrationModel( Base base ) {
        super( base );
    }

    public abstract double getBaseConcentration();
    
    public double getBaseMoleculeCount() {
        return getMoleculeCount( getBaseConcentration() );
    }
}
