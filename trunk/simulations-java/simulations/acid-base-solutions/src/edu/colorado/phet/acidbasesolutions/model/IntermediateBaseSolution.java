package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.model.Base.CustomIntermediateBase;
import edu.colorado.phet.acidbasesolutions.model.Base.IntermediateBase;


public class IntermediateBaseSolution extends AqueousSolution {

    private final IntermediateBase base;
    
    public IntermediateBaseSolution( IntermediateBase base ) {
        super( base );
        this.base = base;
    }
    
    public IntermediateBase getBase() {
        return base;
    }
    
    // [B] = [B for weak acid with Kb=Kmin]*10^(4*(K-Kmin)/(K-Kmax))
    public double getBaseConcentration() {
        final double Kb = ABSConstants.INTERMEDIATE_STRENGTH_RANGE.getMin();
        final double c = getInitialConcentration();
        final double acidConcentration = ( -Kb + Math.sqrt( ( Kb * Kb ) + ( 4 * Kb * c ) ) ) / 2;
        final double baseConcentration = c - acidConcentration;
        return baseConcentration * Math.pow( 10, -4 * getKScale() );
    }
    
    private double getKScale() {
        final double K = base.getStrength();
        final double Kmin = ABSConstants.INTERMEDIATE_STRENGTH_RANGE.getMin();
        final double Kmax = ABSConstants.INTERMEDIATE_STRENGTH_RANGE.getMax();
        return ( K - Kmin ) / ( Kmax - Kmin );
    }
    
    // [BH+] = c -[B]
    public double getAcidConcentration() {
        return getInitialConcentration() - getBaseConcentration();
    }
    
    // [H3O+] = Kw / [OH-]
    public double getH3OConcentration() {
        return Water.getEquilibriumConstant() / getOHConcentration();
    }
    
    // [OH-] = [BH+]
    public double getOHConcentration() {
        return getAcidConcentration();
    }
    
    // [H2O] = W - [BH+]
    public double getH2OConcentration() {
        return getWater().getConcentration() - getAcidConcentration();
    }
    
    public static class CustomIntermediateBaseSolution extends IntermediateBaseSolution {
        
        public CustomIntermediateBaseSolution() {
            super( new CustomIntermediateBase() );
        }
        
        // public for custom
        public void setStrength( double strength ) {
            super.setStrength( strength );
        }
    }
}
