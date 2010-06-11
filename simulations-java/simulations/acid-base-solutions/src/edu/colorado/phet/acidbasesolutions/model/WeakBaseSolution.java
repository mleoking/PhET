/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.model.Base.CustomWeakBase;
import edu.colorado.phet.acidbasesolutions.model.Base.TestWeakBase;

/**
 * An aqueous solution whose solute is a weak base.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class WeakBaseSolution extends AqueousSolution {

    public WeakBaseSolution( Solute solute, double initialConcentration ) {
        super( solute, initialConcentration );
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
    
    public static class TestWeakBaseSolution extends WeakBaseSolution {
        public TestWeakBaseSolution() {
            super( new TestWeakBase(), 1E-2 /* concentration */ );
        }
    }

    public static class CustomWeakBaseSolution extends WeakBaseSolution implements ICustomSolution {

        public CustomWeakBaseSolution( double strength, double initialConcentration ) {
            super( new CustomWeakBase( strength ), initialConcentration );
        }
        
        @Override
        public void setInitialConcentration( double initialConcentration ) {
            super.setInitialConcentration( initialConcentration );
        }
    }
}
