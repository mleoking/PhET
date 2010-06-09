/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

/**
 * An aqueous solution whose solute is a strong base.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class StrongBaseSolution extends AqueousSolution {

    public StrongBaseSolution( Solute solute, double concentration ) {
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
