/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import java.text.MessageFormat;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.constants.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.Molecule.GenericWeakBaseMolecule;
import edu.colorado.phet.acidbasesolutions.model.Molecule.GenericWeakBaseProductMolecule;

/**
 * An aqueous solution whose solute is a weak base.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class WeakBaseSolution extends AqueousSolution {

    public WeakBaseSolution( Molecule solute, Molecule product, double strength, double initialConcentration ) {
        super( solute, product, strength, initialConcentration );
    }
    
    public String getStrengthLabel() {
        return MessageFormat.format( ABSStrings.PATTERN_STRENGTH_WEAK, ABSSymbols.Kb );
    }
    
    // [B] = c - [BH+]
    public double getSoluteConcentration() {
        return getConcentration() - getProductConcentration();
    }
    
    // [BH+] = ( -Kb + sqrt( Kb*Kb + 4*Kb*c ) ) / 2 
    public double getProductConcentration() {
        final double Kb = getStrength();
        final double c = getConcentration();
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
    
    protected boolean isValidStrength( double strength ) {
        return ABSConstants.WEAK_STRENGTH_RANGE.contains( strength );
    }
    
    /**
     * A generic weak base has solute B, product BH+.
     */
    public static abstract class GenericWeakBaseSolution extends WeakBaseSolution {
        public GenericWeakBaseSolution( double strength, double concentration ) {
            super( new GenericWeakBaseMolecule(), new GenericWeakBaseProductMolecule(), strength, concentration );
        }
    }
    
    /**
     * A generic "test" solution whose solute is a weak base.
     * Strength and concentration are immutable.
     */
    public static class TestWeakBaseSolution extends GenericWeakBaseSolution {
        public TestWeakBaseSolution() {
            super( ABSConstants.WEAK_STRENGTH_RANGE.getDefault(), ABSConstants.CONCENTRATION_RANGE.getDefault() );
        }
    }

    /**
     * A generic "custom" solution whose solute is a weak base.
     * Strength and concentration are mutable.
     */
    public static class CustomWeakBaseSolution extends GenericWeakBaseSolution implements ICustomSolution {

        public CustomWeakBaseSolution() {
            this( ABSConstants.WEAK_STRENGTH_RANGE.getDefault(), ABSConstants.CONCENTRATION_RANGE.getDefault() );
        }
        
        public CustomWeakBaseSolution( double strength, double concentration ) {
            super( strength, concentration );
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
