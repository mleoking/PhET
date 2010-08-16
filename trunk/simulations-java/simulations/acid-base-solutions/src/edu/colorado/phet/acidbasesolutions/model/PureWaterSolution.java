/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

/**
 * A solution of pure water, contains no solute.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PureWaterSolution extends AqueousSolution {

    public PureWaterSolution() {
        super( null, null, 0, 0 );
    }
    
    public String getStrengthLabel() {
        return null;
    }

    public double getSoluteConcentration() {
        return 0;
    }
    
    public double getProductConcentration() {
        return 0;
    }
    
    // [H3O] = sqrt(Kw)
    public double getH3OConcentration() {
        return Math.sqrt( getWaterEquilibriumConstant() ); // Kw = [H30] * [OH-]
    }

    // [OH]=[H3O]
    public double getOHConcentration() {
        return getH3OConcentration();
    }

    // W
    public double getH2OConcentration() {
        return getWaterConcentration();
    }

    protected boolean isValidStrength( double strength ) {
        return false;
    }
}
