/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.model.Molecule.GenericStrongBaseMolecule;
import edu.colorado.phet.acidbasesolutions.model.Molecule.GenericStrongBaseProductMolecule;

/**
 * An aqueous solution whose solute is a strong base.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class StrongBaseSolution extends AqueousSolution {

    public StrongBaseSolution( Molecule solute, Molecule product, double strength, double initialConcentration ) {
        super( solute, product, strength, initialConcentration );
    }
    
    // [MOH] = 0
    public double getSoluteConcentration() {
        return 0;
    }
    
    // [M+] = c
    public double getProductConcentration() {
        return getConcentration();
    }
    
    // [H3O+] = Kw / [OH-]
    public double getH3OConcentration() {
        return getWaterEquilibriumConstant() / getOHConcentration();
    }
    
    // [OH-] = c
    public double getOHConcentration() {
        return getConcentration();
    }
    
    // [H2O] = W
    public double getH2OConcentration() {
        return getWaterConcentration();
    }
    
    protected boolean isValidStrength( double strength ) {
        return strength > ABSConstants.WEAK_STRENGTH_RANGE.getMax();
    }
    
    /**
     * A generic strong base has solute MOH, product M+.
     */
    public static abstract class GenericStrongBaseSolution extends StrongBaseSolution {
        public GenericStrongBaseSolution( double strength, double concentration ) {
            super( new GenericStrongBaseMolecule(), new GenericStrongBaseProductMolecule(), strength, concentration );
        }
    }
    
    /**
     * A generic "test" solution whose solute is a strong base.
     * Strength and concentration are immutable.
     */
    public static class TestStrongBaseSolution extends GenericStrongBaseSolution {
        public TestStrongBaseSolution() {
            super( ABSConstants.STRONG_STRENGTH, ABSConstants.CONCENTRATION_RANGE.getDefault() );
        }
    }

    /**
     * A generic "custom" solution whose solute is a strong base.
     * Strength and concentration are mutable.
     */
    public static class CustomStrongBaseSolution extends GenericStrongBaseSolution implements ICustomSolution {
        
        public CustomStrongBaseSolution() {
            this( ABSConstants.STRONG_STRENGTH, ABSConstants.CONCENTRATION_RANGE.getDefault() );
        }
        
        public CustomStrongBaseSolution( double strength, double initialConcentration ) {
            super( strength, initialConcentration );
        }
        
        @Override
        public void setStrength( double strength ) {
            super.setStrength( strength );
        }
        
        @Override
        public void setConcentration( double initialConcentration ) {
            super.setConcentration( initialConcentration );
        }
    }
}
