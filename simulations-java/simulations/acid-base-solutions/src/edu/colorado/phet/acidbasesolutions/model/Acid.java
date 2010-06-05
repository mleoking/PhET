/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.constants.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.Molecule.GenericAcidMolecule;
import edu.colorado.phet.acidbasesolutions.model.Molecule.GenericAcidProductMolecule;

/**
 * Base class for all solutes that are acids.
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
    
    public static class GenericAcid extends Acid {
        
        public GenericAcid( double strength ) {
            super( new GenericAcidMolecule(), new GenericAcidProductMolecule(), strength );
        }
    }
    
    public static class TestWeakAcid extends GenericAcid {

        public TestWeakAcid() {
            super( 0.1 );
        }
    }
    
    public static class TestStrongAcid extends GenericAcid {

        public TestStrongAcid() {
            super( 2 );
        }
    }
    
    public static class CustomWeakAcid extends GenericAcid implements ICustomSolute {
        
        public CustomWeakAcid( double strength ) {
            super( strength );
        }
        
        @Override
        public void setStrength( double strength ) {
            assert( ABSConstants.WEAK_STRENGTH_RANGE.contains( strength ) );
            super.setStrength( strength );
        }
    }
    
    public static class CustomStrongAcid extends GenericAcid implements ICustomSolute {
        
        public CustomStrongAcid( double strength ) {
            super( strength );
        }
        
        @Override
        public void setStrength( double strength ) {
            assert( strength > ABSConstants.WEAK_STRENGTH_RANGE.getMax() );
            super.setStrength( strength );
        }
    }
}
