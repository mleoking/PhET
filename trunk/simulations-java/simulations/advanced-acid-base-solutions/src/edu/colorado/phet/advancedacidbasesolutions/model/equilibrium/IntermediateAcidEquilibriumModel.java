// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.advancedacidbasesolutions.model.equilibrium;

import edu.colorado.phet.advancedacidbasesolutions.AABSConstants;
import edu.colorado.phet.advancedacidbasesolutions.model.Acid;
import edu.colorado.phet.advancedacidbasesolutions.model.Water;

/**
 * Equilibrium model for an "intermediate" acid.
 * Intermediate is a term that PhET invented to describe a hypothetical range
 * between weak and strong.  Acids in this range are represented as weak,
 * but the model provides a smooth transition between weak and strong.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntermediateAcidEquilibriumModel extends AbstractEquilibriumModel {

    public IntermediateAcidEquilibriumModel( Acid acid ) {
        super( acid );
    }
    
    // [HA] = [HA for weak acid with Ka=Kmin]*10^(4*(K-Kmin)/(K-Kmax))
    public double getReactantConcentration() {
        final double Ka = AABSConstants.INTERMEDIATE_STRENGTH_RANGE.getMin();
        final double c = getSolute().getConcentration();
        final double baseConcentration = ( -Ka + Math.sqrt( ( Ka * Ka ) + ( 4 * Ka * c ) ) ) / 2;
        final double acidConcentration = c - baseConcentration;
        return acidConcentration * Math.pow( 10, -4 * getKScale() );
    }
    
    private double getKScale() {
        final double K = getSolute().getStrength();
        final double Kmin = AABSConstants.INTERMEDIATE_STRENGTH_RANGE.getMin();
        final double Kmax = AABSConstants.INTERMEDIATE_STRENGTH_RANGE.getMax();
        return ( K - Kmin ) / ( Kmax - Kmin );
    }
    
    // [A-] = c -[HA]
    public double getProductConcentration() {
        return getSolute().getConcentration() - getReactantConcentration();
    }
    
    // [H3O+] = [A-]
    public double getH3OConcentration() {
        return getProductConcentration();
    }
    
    // [OH-] = Kw / [H3O+]
    public double getOHConcentration() {
        return Water.getEquilibriumConstant() / getH3OConcentration();
    }
    
    // [H2O] = W - [A-]
    public double getH2OConcentration() {
        return Water.getConcentration() - getProductConcentration();
    }
}
