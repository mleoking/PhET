/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.model.Molecule.GenericAcidMolecule;
import edu.colorado.phet.acidbasesolutions.model.Molecule.GenericAcidProductMolecule;

/**
 * An aqueous solution whose solute is a weak acid.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class WeakAcidSolution extends AqueousSolution {
    
    public WeakAcidSolution( Molecule solute, Molecule product, double strength, double initialConcentration ) {
        super( solute, product, strength, initialConcentration );
    }

    // [HA] = c - [H3O+]
    public double getSoluteConcentration() {
        return getConcentration() - getH3OConcentration();
    }
    
    // [A-] = [H3O+] 
    public double getProductConcentration() {
        return getH3OConcentration();
    }

    // [H3O+] = ( -Ka + sqrt( Ka*Ka + 4*Ka*c ) ) / 2 
    public double getH3OConcentration() {
        final double Ka = getStrength();
        final double c = getConcentration();
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
    
    protected boolean isValidStrength( double strength ) {
        return ABSConstants.WEAK_STRENGTH_RANGE.contains( strength );
    }
    
    /**
     * A generic weak acid has solute HA, product A-.
     */
    public static abstract class GenericWeakAcidSolution extends WeakAcidSolution {
        public GenericWeakAcidSolution( double strength, double concentration ) {
            super( new GenericAcidMolecule(), new GenericAcidProductMolecule(), strength, concentration );
        }
    }
    
    /**
     * A generic "test" solution whose solute is a weak acid.
     * Strength and concentration are immutable.
     */
    public static class TestWeakAcidSolution extends GenericWeakAcidSolution {
        public TestWeakAcidSolution() {
            super( 1E-4 /* strength */, 1E-2 /* concentration */ );
        }
    }
    
    /**
     * A generic "custom" solution whose solute is a weak acid.
     * Strength and concentration are mutable.
     */
    public static class CustomWeakAcidSolution extends GenericWeakAcidSolution implements ICustomSolution {
        
        public CustomWeakAcidSolution( double strength, double initialConcentration ) {
            super( strength, initialConcentration );
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
