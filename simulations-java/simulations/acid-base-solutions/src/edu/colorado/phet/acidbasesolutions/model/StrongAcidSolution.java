/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.model.Molecule.GenericAcidMolecule;
import edu.colorado.phet.acidbasesolutions.model.Molecule.GenericAcidProductMolecule;

/**
 * An aqueous solution whose solute is a strong acid.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class StrongAcidSolution extends AqueousSolution {

    public StrongAcidSolution( Molecule solute, Molecule product, double strength, double initialConcentration ) {
        super( solute, product, strength, initialConcentration );
    }
    
    // [HA] = 0
    public double getSoluteConcentration() {
        return 0;
    }
    
    // [A-] = c
    public double getProductConcentration() {
        return getConcentration();
    }
    
    // [H3O+] = c
    public double getH3OConcentration() {
        return getConcentration();
    }
    
    // [OH-] = Kw / [H3O+]
    public double getOHConcentration() {
        return getWaterEquilibriumConstant() / getH3OConcentration();
    }
    
    // [H2O] = W - c
    public double getH2OConcentration() {
        return getWaterConcentration() - getConcentration();
    }

    protected boolean isValidStrength( double strength ) {
        return strength > ABSConstants.WEAK_STRENGTH_RANGE.getMax();
    }
    
    /**
     * A generic strong acid has solute HA, product A-.
     */
    public static abstract class GenericStrongAcidSolution extends StrongAcidSolution {
        public GenericStrongAcidSolution( double strength, double concentration ) {
            super( new GenericAcidMolecule(), new GenericAcidProductMolecule(), strength, concentration );
        }
    }
    
    /**
     * A generic "test" solution whose solute is a strong acid.
     * Strength and concentration are immutable.
     */
    public static class TestStrongAcidSolution extends GenericStrongAcidSolution {
        public TestStrongAcidSolution() {
            super( ABSConstants.STRONG_STRENGTH, ABSConstants.CONCENTRATION_RANGE.getDefault() );
        }
    }
    
    /**
     * A generic "custom" solution whose solute is a strong acid.
     * Strength and concentration are mutable.
     */
    public static class CustomStrongAcidSolution extends GenericStrongAcidSolution implements ICustomSolution {
        
        public CustomStrongAcidSolution() {
            this( ABSConstants.CONCENTRATION_RANGE.getDefault() );
        }
        
        public CustomStrongAcidSolution( double concentration ) {
            super( ABSConstants.STRONG_STRENGTH, concentration );
        }
        
        @Override
        public void setStrength( double strength ) {
            super.setStrength( strength );
        }
        
        @Override
        public void setConcentration( double concentration ) {
            super.setConcentration( concentration );
        }
    }
}
