package edu.colorado.phet.acidbasesolutions.model.solutions;

import edu.colorado.phet.acidbasesolutions.model.bases.IBase;


public interface IBaseSolution extends IAqueousSolution {

    public IBase getBase();
    
    // c
    public void setInitialBaseConcentration( double c );

    // c
    public double getInitialBaseConcentration();

    // [B]
    public double getBaseConcentration();

    // [BH+]
    public double getConjugateAcidConcentration();


    
}
