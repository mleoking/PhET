package edu.colorado.phet.acidbasesolutions.model;

import java.awt.Image;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;


public abstract class Base extends Solute {
    
    private Base( String name, String symbol, Image icon, String conjugateSymbol, Image conjugateIcon, double strength ) {
        super( name, symbol, icon, conjugateSymbol, conjugateIcon, strength );
    }
    
    public boolean isZeroNegligible() {
        return false;
    }
    
    //----------------------------------------------------------------------------
    // Strong bases
    //----------------------------------------------------------------------------
    
    public abstract static class StrongBase extends Base {
        
        private StrongBase( String name, String symbol, String conjugateSymbol, double strength  ) {
            super( name, symbol, ABSImages.MOH_MOLECULE, conjugateSymbol, ABSImages.M_PLUS_MOLECULE, strength );
        }
        
        protected boolean isValidStrength( double strength ) {
            return ABSConstants.STRONG_STRENGTH_RANGE.contains( strength );
        }
    }
    
    public static class SodiumHydroxide extends StrongBase {
        public SodiumHydroxide() {
            super( ABSStrings.SODIUM_HYDROXIDE, ABSSymbols.NaOH, ABSSymbols.Na_PLUS, 1E7 );
        }
    }

    //----------------------------------------------------------------------------
    // Weak bases
    //----------------------------------------------------------------------------

    public abstract static class WeakBase extends Base {
        
        private WeakBase( String name, String symbol, String conjugateSymbol, double strength ) {
            super( name, symbol, ABSImages.B_MOLECULE, conjugateSymbol, ABSImages.BH_PLUS_MOLECULE, strength );
        }
        
        protected boolean isValidStrength( double strength ) {
            return ABSConstants.WEAK_STRENGTH_RANGE.contains( strength );
        }
    }

    public static class Ammonia extends WeakBase {
        public Ammonia() {
            super( ABSStrings.AMMONIA, ABSSymbols.NH3, ABSSymbols.NH4_PLUS, 1.8E-5 );
        }
    }
    
    public static class Pyridine extends WeakBase {
        public Pyridine() {
            super( ABSStrings.PYRIDINE, ABSSymbols.C5H5N, ABSSymbols.C5H5NH_PLUS, 1.7E-9 );
        }
    }
    
    //----------------------------------------------------------------------------
    // Custom base (strong, weak, or intermediate)
    //----------------------------------------------------------------------------

    public static class CustomBase extends Base implements ICustomSolute {
        
        private static final double DEFAULT_STRENGTH = ABSConstants.WEAK_STRENGTH_RANGE.getMin();
        
        public CustomBase() {
            super( ABSStrings.CUSTOM_BASE, "", ABSImages.B_MOLECULE, "", ABSImages.BH_PLUS_MOLECULE, DEFAULT_STRENGTH );
            updateSymbol( getStrength() );
        }
        
        // public, so that custom base strength is mutable
        public void setStrength( double strength ) {
            updateSymbol( strength );
            super.setStrength( strength );
        }
        
        private void updateSymbol( double strength ) {
            if ( isStrong() ) {
                setSymbol( ABSSymbols.MOH );
                setIcon( ABSImages.MOH_MOLECULE );
                setConjugateSymbol( ABSSymbols.M_PLUS );
                setConjugateIcon( ABSImages.M_PLUS_MOLECULE );
            }
            else {
                setSymbol( ABSSymbols.B );
                setIcon( ABSImages.B_MOLECULE );
                setConjugateSymbol( ABSSymbols.BH_PLUS );
                setConjugateIcon( ABSImages.BH_PLUS_MOLECULE );
            }
        }
        
        protected boolean isValidStrength( double strength ) {
            return ABSConstants.CUSTOM_STRENGTH_RANGE.contains( strength );
        }
        
        public boolean isWeak() {
            return ABSConstants.WEAK_STRENGTH_RANGE.contains( getStrength() );
        }
        
        public boolean isStrong() {
            return ABSConstants.STRONG_STRENGTH_RANGE.contains( getStrength() );
        }
        
        public boolean isIntermediate() {
            return ABSConstants.INTERMEDIATE_STRENGTH_RANGE.containsExclusive( getStrength() );
        }
    }
}
