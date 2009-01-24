
package edu.colorado.phet.acidbasesolutions.model.solutions;

import edu.colorado.phet.acidbasesolutions.model.acids.IAcid;


public interface IAcidSolution extends IAqueousSolution {
    
    public IAcid getAcid();

    // c
    public void setInitialAcidConcentration( double c );

    // c
    public double getInitialAcidConcentration();

    // [HA]
    public double getAcidConcentration();

    // [A-]
    public double getConjugateBaseConcentration();
    
    // count of acid molecules
    public int getAcidMoleculeCount();
    
    // count of conjugate base molecules
    public int getConjugateBaseMoleculeCount();
}
