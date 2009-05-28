package edu.colorado.phet.acidbasesolutions.model.concentration;

import edu.colorado.phet.acidbasesolutions.model.Acid;


public abstract class AcidEquilibriumModel extends EquilibriumModel {

    protected AcidEquilibriumModel( Acid acid ) {
        super( acid );
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
