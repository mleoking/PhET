package edu.colorado.phet.acidbasesolutions.model.concentration;

import edu.colorado.phet.acidbasesolutions.model.Solute;


public abstract class BaseConcentrationModel extends ConcentrationModel {

    protected BaseConcentrationModel( Solute solute ) {
        super( solute );
    }

    public abstract double getBaseConcentration();
    
    public int getBaseMoleculeCount() {
        return getMoleculeCount( getBaseConcentration() );
    }
}
