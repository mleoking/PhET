/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.constants.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.Molecule.GenericAcidMolecule;
import edu.colorado.phet.acidbasesolutions.model.Molecule.GenericAcidProductMolecule;

/**
 * Class hierarchy for all acid solutes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Acid extends Solute {

    public Acid( Molecule molecule, Molecule conjugate, double strength ) {
        super( molecule, conjugate, strength );
    }

    public String getStrengthSymbol() {
        return ABSSymbols.Ka;
    }
    
    protected abstract boolean isValidStrength( double strength );
    
    //------------------------------------------------------------------------------
    // Strong
    //------------------------------------------------------------------------------
    
    public static abstract class StrongAcid extends Acid {

        public StrongAcid( Molecule molecule, Molecule conjugate, double strength ) {
            super( molecule, conjugate, strength );
            if ( !isValidStrength( strength ) ) {
                throw new IllegalArgumentException( "strength out of range: " + strength );
            }
        }

        protected boolean isValidStrength( double strength ) {
            return ( strength > ABSConstants.WEAK_STRENGTH_RANGE.getMax() );
        }
    }

    public static abstract class GenericStrongAcid extends StrongAcid {

        public GenericStrongAcid( double strength ) {
            super( new GenericAcidMolecule(), new GenericAcidProductMolecule(), strength );
        }
    }

    public static class TestStrongAcid extends GenericStrongAcid {

        public TestStrongAcid() {
            super( 2 /* strength */);
        }
    }

    public static class CustomStrongAcid extends GenericStrongAcid implements ICustomSolute {

        public CustomStrongAcid( double strength ) {
            super( strength );
        }

        @Override
        public void setStrength( double strength ) {
            if ( !isValidStrength( strength ) ) {
                throw new IllegalArgumentException( "strength out of range: " + strength );
            }
            super.setStrength( strength );
        }
    }
    
    //------------------------------------------------------------------------------
    // Weak
    //------------------------------------------------------------------------------
    
    public static abstract class WeakAcid extends Acid {

        public WeakAcid( Molecule molecule, Molecule conjugate, double strength ) {
            super( molecule, conjugate, strength );
            if ( !isValidStrength( strength ) ) {
                throw new IllegalArgumentException( "strength out of range: " + strength );
            }
        }

        protected boolean isValidStrength( double strength ) {
            return ABSConstants.WEAK_STRENGTH_RANGE.contains( strength );
        }
    }

    public static abstract class GenericWeakAcid extends WeakAcid {

        public GenericWeakAcid( double strength ) {
            super( new GenericAcidMolecule(), new GenericAcidProductMolecule(), strength );
        }
    }

    public static class TestWeakAcid extends GenericWeakAcid {

        public TestWeakAcid() {
            super( 1E-4 /* strength */);
        }
    }

    public static class CustomWeakAcid extends GenericWeakAcid implements ICustomSolute {

        public CustomWeakAcid( double strength ) {
            super( strength );
        }

        @Override
        public void setStrength( double strength ) {
            if ( !isValidStrength( strength ) ) {
                throw new IllegalArgumentException( "strength out of range: " + strength );
            }
            super.setStrength( strength );
        }
    }
}
