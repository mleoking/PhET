/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.model.Acid.CustomStrongAcid;
import edu.colorado.phet.acidbasesolutions.model.Acid.TestStrongAcid;
import edu.colorado.phet.acidbasesolutions.model.Solute.ICustomSolute;


/**
 * An aqueous solution whose solute is a strong acid.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class StrongAcidSolution extends AqueousSolution {

    public StrongAcidSolution( Solute solute, double initialConcentration ) {
        super( solute, initialConcentration );
    }

    // [HA] = 0
    public double getReactantConcentration() {
        return 0;
    }
    
    // [A-] = c
    public double getProductConcentration() {
        return getInitialConcentration();
    }
    
    // [H3O+] = c
    public double getH3OConcentration() {
        return getInitialConcentration();
    }
    
    // [OH-] = Kw / [H3O+]
    public double getOHConcentration() {
        return getWaterEquilibriumConstant() / getH3OConcentration();
    }
    
    // [H2O] = W - c
    public double getH2OConcentration() {
        return getWaterConcentration() - getInitialConcentration();
    }
    
    /**
     * A generic "test" solution whose solute is a strong acid.
     * Strength and concentration are immutable.
     */
    public static class TestStrongAcidSolution extends StrongAcidSolution {
        public TestStrongAcidSolution() {
            super( new TestStrongAcid(), 1E-2 /* concentration */ );
        }
    }
    
    /**
     * A generic "custom" solution whose solute is a strong acid.
     * Strength and concentration are mutable.
     */
    public static class CustomStrongAcidSolution extends StrongAcidSolution implements ICustomSolution {
        
        public CustomStrongAcidSolution( double strength, double initialConcentration ) {
            super( new CustomStrongAcid( strength ), initialConcentration );
        }
        
        public void setSoluteStrength( double strength ) {
            ( (ICustomSolute) getSolute() ).setStrength( strength );
        }
        
        @Override
        public void setInitialConcentration( double initialConcentration ) {
            super.setInitialConcentration( initialConcentration );
        }
    }
}
