/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

/**
 * An aqueous solution whose solute is a strong base.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class StrongBaseSolution extends AqueousSolution {

    // Marker class for solutes that are strong bases.
    public static abstract class StrongBase extends Base {
        public StrongBase( Molecule molecule, Molecule conjugateMolecule, double strength ) {
            super( molecule, conjugateMolecule, strength );
        }
    }
    
    public StrongBaseSolution( StrongBase solute, double concentration ) {
        super( solute, concentration );
    }
    
    // [MOH] = 0
    public double getReactantConcentration() {
        return 0;
    }
    
    // [M+] = c
    public double getProductConcentration() {
        return getInitialConcentration();
    }
    
    // [H3O+] = Kw / [OH-]
    public double getH3OConcentration() {
        return getWaterEquilibriumConstant() / getOHConcentration();
    }
    
    // [OH-] = c
    public double getOHConcentration() {
        return getInitialConcentration();
    }
    
    // [H2O] = W
    public double getH2OConcentration() {
        return getWaterConcentration();
    }
}
