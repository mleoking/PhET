/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;


/**
 * An aqueous solution whose solute is a weak base.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WeakBaseSolution extends AqueousSolution {

    public WeakBaseSolution( Solute solute, double concentration ) {
        super( solute, concentration );
    }
    
    // [B] = c - [BH+]
    public double getReactantConcentration() {
        return getInitialConcentration() - getProductConcentration();
    }
    
    // [BH+] = ( -Kb + sqrt( Kb*Kb + 4*Kb*c ) ) / 2 
    public double getProductConcentration() {
        final double Kb = getSolute().getStrength();
        final double c = getInitialConcentration();
        return (-Kb + Math.sqrt( ( Kb * Kb ) + ( 4 * Kb * c ) ) ) / 2;
    }
    
    // [H3O+] = Kw / [OH-]
    public double getH3OConcentration() {
        return getWaterEquilibriumConstant() / getOHConcentration();
    }
    
    // [OH-] = [BH+]
    public double getOHConcentration() {
        return getProductConcentration();
    }
    
    // [H2O] = W - [BH+]
    public double getH2OConcentration() {
        return getWaterConcentration() - getProductConcentration();
    }
}
