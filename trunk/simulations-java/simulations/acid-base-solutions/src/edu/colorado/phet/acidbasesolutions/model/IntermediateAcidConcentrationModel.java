package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.model.Acid.IntermediateAcid;



public class IntermediateAcidConcentrationModel extends ConcentrationModel {

    public IntermediateAcidConcentrationModel( IntermediateAcid acid ) {
        super( acid );
    }
    
    // [HA] = [HA for weak acid with Ka=Kmin]*10^(4*(K-Kmin)/(K-Kmax))
    public double getAcidConcentration() {
        final double Ka = ABSConstants.INTERMEDIATE_STRENGTH_RANGE.getMin();
        final double c = getInitialConcentration();
        final double baseConcentration = ( -Ka + Math.sqrt( ( Ka * Ka ) + ( 4 * Ka * c ) ) ) / 2;
        final double acidConcentration = c - baseConcentration;
        return acidConcentration * Math.pow( 10, -4 * getKScale() );
    }
    
    private double getKScale() {
        final double K = getSolute().getStrength();
        final double Kmin = ABSConstants.INTERMEDIATE_STRENGTH_RANGE.getMin();
        final double Kmax = ABSConstants.INTERMEDIATE_STRENGTH_RANGE.getMax();
        return ( K - Kmin ) / ( Kmax - Kmin );
    }
    
    // [A-] = c -[HA]
    public double getBaseConcentration() {
        return getInitialConcentration() - getAcidConcentration();
    }
    
    // [H3O+] = [A-]
    public double getH3OConcentration() {
        return getBaseConcentration();
    }
    
    // [OH-] = Kw / [H3O+]
    public double getOHConcentration() {
        return Water.getEquilibriumConstant() / getH3OConcentration();
    }
    
    // [H2O] = W - [A-]
    public double getH2OConcentration() {
        return Water.getConcentration() - getBaseConcentration();
    }
}
