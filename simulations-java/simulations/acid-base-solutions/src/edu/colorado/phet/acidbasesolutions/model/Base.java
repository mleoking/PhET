/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.constants.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.Molecule.GenericStrongBaseMolecule;
import edu.colorado.phet.acidbasesolutions.model.Molecule.GenericStrongBaseProductMolecule;
import edu.colorado.phet.acidbasesolutions.model.Molecule.GenericWeakBaseMolecule;
import edu.colorado.phet.acidbasesolutions.model.Molecule.GenericWeakBaseProductMolecule;

/**
 * Base class for all solutes that are bases.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Base extends Solute {

    public Base( Molecule molecule, Molecule conjugate, double strength ) {
        super( molecule, conjugate, strength );
    }

    public String getStrengthSymbol() {
        return ABSSymbols.Kb;
    }
    
    public static class TestWeakBase extends Base {

        public TestWeakBase() {
            super( new GenericWeakBaseMolecule(), new GenericWeakBaseProductMolecule(), 0.1 );
        }
    }
    
    public static class TestStrongBase extends Base {

        public TestStrongBase() {
            super( new GenericStrongBaseMolecule(), new GenericStrongBaseProductMolecule(), 2 );
        }
    }
    
    public static class CustomWeakBase extends Base implements ICustomSolute {
        
        public CustomWeakBase( double strength ) {
            super( new GenericWeakBaseMolecule(), new GenericWeakBaseProductMolecule(), strength );
        }
        
        @Override
        public void setStrength( double strength ) {
            assert( ABSConstants.WEAK_STRENGTH_RANGE.contains( strength ) );
            super.setStrength( strength );
        }
    }
    
    public static class CustomStrongBase extends Base implements ICustomSolute {
        
        public CustomStrongBase( double strength ) {
            super( new GenericStrongBaseMolecule(), new GenericStrongBaseProductMolecule(), strength );
        }
        
        @Override
        public void setStrength( double strength ) {
            assert( strength > ABSConstants.WEAK_STRENGTH_RANGE.getMax() );
            super.setStrength( strength );
        }
    }
}
