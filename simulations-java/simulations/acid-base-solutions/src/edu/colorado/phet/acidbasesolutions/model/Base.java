/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.constants.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.Molecule.GenericStrongBaseMolecule;
import edu.colorado.phet.acidbasesolutions.model.Molecule.GenericStrongBaseProductMolecule;
import edu.colorado.phet.acidbasesolutions.model.Molecule.GenericWeakBaseMolecule;
import edu.colorado.phet.acidbasesolutions.model.Molecule.GenericWeakBaseProductMolecule;

/** 
 * Class hierarchy for all base solutes.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Base extends Solute {

    public Base( Molecule molecule, Molecule conjugate, double strength ) {
        super( molecule, conjugate, strength );
    }

    public String getStrengthSymbol() {
        return ABSSymbols.Kb;
    }

    protected abstract boolean isValidStrength( double strength );

    //------------------------------------------------------------------------------
    // Strong
    //------------------------------------------------------------------------------

    public static abstract class StrongBase extends Base {

        public StrongBase( Molecule molecule, Molecule conjugate, double strength ) {
            super( molecule, conjugate, strength );
        }

        protected boolean isValidStrength( double strength ) {
            return ( strength > ABSConstants.WEAK_STRENGTH_RANGE.getMax() );
        }
    }

    public static abstract class GenericStrongBase extends StrongBase {

        public GenericStrongBase( double strength ) {
            super( new GenericStrongBaseMolecule(), new GenericStrongBaseProductMolecule(), strength );
        }
    }

    public static class TestStrongBase extends GenericStrongBase {

        public TestStrongBase() {
            super( 2 /* strength */);
        }
    }

    public static class CustomStrongBase extends GenericStrongBase implements ICustomSolute {

        public CustomStrongBase( double strength ) {
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

    public static abstract class WeakBase extends Base {

        public WeakBase( Molecule molecule, Molecule conjugate, double strength ) {
            super( molecule, conjugate, strength );
        }

        protected boolean isValidStrength( double strength ) {
            return ( ABSConstants.WEAK_STRENGTH_RANGE.contains( strength ) );
        }
    }

    public static class GenericWeakBase extends WeakBase {

        public GenericWeakBase( double strength ) {
            super( new GenericWeakBaseMolecule(), new GenericWeakBaseProductMolecule(), strength );
        }
    }

    public static class TestWeakBase extends GenericWeakBase {

        public TestWeakBase() {
            super( 1E-4 /* strength */);
        }
    }

    public static class CustomWeakBase extends GenericWeakBase implements ICustomSolute {

        public CustomWeakBase( double strength ) {
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
