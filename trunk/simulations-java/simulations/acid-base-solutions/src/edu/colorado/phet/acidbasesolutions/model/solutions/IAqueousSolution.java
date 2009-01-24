package edu.colorado.phet.acidbasesolutions.model.solutions;

/**
 * IAqueousSolution is a solution in which the solvent is water.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface IAqueousSolution {

    // [H3O+]
    public double getHydroniumConcentration();

    // [OH-]
    public double getHydroxideConcentration();

    // [H2O]
    public double getWaterConcentration();
    
//    // count of H3O+ molecules
//    public int getHyrdroniumCount();
//    
//    // count of OH- molecules
//    public int getHydroxideCount();
//    
//    // count of H2O molecules
//    public int getWaterCount();
}
