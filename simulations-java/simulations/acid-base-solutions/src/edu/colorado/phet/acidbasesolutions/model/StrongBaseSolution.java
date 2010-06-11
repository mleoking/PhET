/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.model.Base.CustomStrongBase;
import edu.colorado.phet.acidbasesolutions.model.Base.TestStrongBase;
import edu.colorado.phet.acidbasesolutions.model.Solute.ICustomSolute;

/**
 * An aqueous solution whose solute is a strong base.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class StrongBaseSolution extends AqueousSolution {

    public StrongBaseSolution( Solute solute, double initialConcentration ) {
        super( solute, initialConcentration );
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
    
    /**
     * A generic "test" solution whose solute is a strong base.
     * Strength and concentration are immutable.
     */
    public static class TestStrongBaseSolution extends StrongBaseSolution {
        public TestStrongBaseSolution() {
            super( new TestStrongBase(), 1E-2 /* concentration */ );
        }
    }

    /**
     * A generic "custom" solution whose solute is a strong base.
     * Strength and concentration are mutable.
     */
    public static class CustomStrongBaseSolution extends StrongBaseSolution implements ICustomSolution {
        
        public CustomStrongBaseSolution( double strength, double initialConcentration ) {
            super( new CustomStrongBase( strength ), initialConcentration );
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
