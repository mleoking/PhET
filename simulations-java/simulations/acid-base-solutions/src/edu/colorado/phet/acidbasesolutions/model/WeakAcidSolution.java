/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

/**
 * An aqueous solution whose solute is a weak acid.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WeakAcidSolution extends AqueousSolution {
    
    public WeakAcidSolution( Solute solute, double concentration ) {
        super( solute, concentration );
    }

    // [HA] = c - [H3O+]
    public double getReactantConcentration() {
        return getInitialConcentration() - getH3OConcentration();
    }
    
    // [A-] = [H3O+] 
    public double getProductConcentration() {
        return getH3OConcentration();
    }

    // [H3O+] = ( -Ka + sqrt( Ka*Ka + 4*Ka*c ) ) / 2 
    public double getH3OConcentration() {
        final double Ka = getSolute().getStrength();
        final double c = getInitialConcentration();
        return ( -Ka + Math.sqrt( ( Ka * Ka ) + ( 4 * Ka * c ) ) ) / 2;
    }
    
    // [OH-] = Kw / [H3O+]
    public double getOHConcentration() {
        return getWaterEquilibriumConstant() / getH3OConcentration();
    }
    
    // [H2O] = W - [A-]
    public double getH2OConcentration() {
        return getWaterConcentration() - getProductConcentration();
    }
}
