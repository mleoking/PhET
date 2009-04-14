package edu.colorado.phet.acidbasesolutions.model.solutions;


/**
 * A solution is formed when a solvent dissolves some substance.
 * In an aqueous solution, water is the solvent.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface IAqueousSolution {
    
    // pH
    public double getPH();

    // [H3O+]
    public double getHydroniumConcentration();

    // [OH-]
    public double getHydroxideConcentration();

    // [H2O]
    public double getWaterConcentration();
    
    // count of H3O+ molecules
    public int getHydroniumMoleculeCount();
    
    // count of OH- molecules
    public int getHydroxideMoleculeCount();
    
    // count of H2O molecules
    public int getWaterMoleculeCount();
}
