package edu.colorado.phet.acidbasesolutions.model.concentration;

import edu.colorado.phet.acidbasesolutions.model.Solute;


public abstract class AcidConcentrationModel extends ConcentrationModel {

    protected AcidConcentrationModel( Solute solute ) {
        super( solute );
    }

    public abstract double getAcidConcentration();
    
    public abstract double getBaseConcentration();
    
    public double getAcidMoleculeCount() {
        return getMoleculeCount( getAcidConcentration() );
    }
    
    public double getBaseMoleculeCount() {
        return getMoleculeCount( getBaseConcentration() );
    }
}
