/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.model.Acid.CustomWeakAcid;
import edu.colorado.phet.acidbasesolutions.model.Acid.TestWeakAcid;
import edu.colorado.phet.acidbasesolutions.model.Solute.ICustomSolute;

/**
 * An aqueous solution whose solute is a weak acid.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class WeakAcidSolution extends AqueousSolution {
    
    public WeakAcidSolution( Solute solute, double initialConcentration ) {
        super( solute, initialConcentration );
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
    
    /**
     * A generic "test" solution whose solute is a weak acid.
     * Strength and concentration are immutable.
     */
    public static class TestWeakAcidSolution extends WeakAcidSolution {
        public TestWeakAcidSolution() {
            super( new TestWeakAcid(), 1E-2 /* concentration */ );
        }
    }
    
    /**
     * A generic "custom" solution whose solute is a weak acid.
     * Strength and concentration are mutable.
     */
    public static class CustomWeakAcidSolution extends WeakAcidSolution implements ICustomSolution {
        
        public CustomWeakAcidSolution( double strength, double initialConcentration ) {
            super( new CustomWeakAcid( strength ), initialConcentration );
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
