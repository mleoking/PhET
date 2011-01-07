// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.advancedacidbasesolutions.model.equilibrium;

import edu.colorado.phet.advancedacidbasesolutions.AABSConstants;
import edu.colorado.phet.advancedacidbasesolutions.model.Base;
import edu.colorado.phet.advancedacidbasesolutions.model.Water;

/**
 * Equilibrium model for an "intermediate" base.
 * Intermediate is a term that PhET invented to describe a hypothetical range
 * between weak and strong.  Bases in this range are represented as weak,
 * but the model provides a smooth transition between weak and strong.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntermediateBaseEquilibriumModel extends AbstractEquilibriumModel {

    public IntermediateBaseEquilibriumModel( Base base ) {
        super( base );
    }
    
    // [B] = [B for weak acid with Kb=Kmin]*10^(4*(K-Kmin)/(K-Kmax))
    public double getReactantConcentration() {
        final double Kb = AABSConstants.INTERMEDIATE_STRENGTH_RANGE.getMin();
        final double c = getSolute().getConcentration();
        final double acidConcentration = ( -Kb + Math.sqrt( ( Kb * Kb ) + ( 4 * Kb * c ) ) ) / 2;
        final double baseConcentration = c - acidConcentration;
        return baseConcentration * Math.pow( 10, -4 * getKScale() );
    }
    
    private double getKScale() {
        final double K = getSolute().getStrength();
        final double Kmin = AABSConstants.INTERMEDIATE_STRENGTH_RANGE.getMin();
        final double Kmax = AABSConstants.INTERMEDIATE_STRENGTH_RANGE.getMax();
        return ( K - Kmin ) / ( Kmax - Kmin );
    }
    
    // [BH+] = c -[B]
    public double getProductConcentration() {
        return getSolute().getConcentration() - getReactantConcentration();
    }
    
    // [H3O+] = Kw / [OH-]
    public double getH3OConcentration() {
        return Water.getEquilibriumConstant() / getOHConcentration();
    }
    
    // [OH-] = [BH+]
    public double getOHConcentration() {
        return getProductConcentration();
    }
    
    // [H2O] = W - [BH+]
    public double getH2OConcentration() {
        return Water.getConcentration() - getProductConcentration();
    }
}
