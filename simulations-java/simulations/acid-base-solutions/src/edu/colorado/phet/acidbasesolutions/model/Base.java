package edu.colorado.phet.acidbasesolutions.model;

import java.awt.Color;
import java.awt.Image;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;


public abstract class Base extends Solute {
    
    private Base( String name, String symbol, Image icon, Image structure, Color color, 
            String conjugateSymbol, Image conjugateIcon, Image conjugateStructure, Color conjugateColor, double strength ) {
        super( name, symbol, icon, structure, color, conjugateSymbol, conjugateIcon, conjugateStructure, conjugateColor, strength );
    }
    
    //----------------------------------------------------------------------------
    // Strong bases
    //----------------------------------------------------------------------------
    
    public abstract static class StrongBase extends Base {
        
        private StrongBase( String name, String symbol, String conjugateSymbol, double strength  ) {
            super( name, symbol, ABSImages.MOH_MOLECULE, ABSImages.MOH_STRUCTURE, ABSConstants.MOH_COLOR, 
                    conjugateSymbol, ABSImages.M_PLUS_MOLECULE, ABSImages.M_PLUS_STRUCTURE, ABSConstants.M_COLOR, strength );
        }
        
        public boolean isZeroNegligible() {
            return true;
        }
        
        protected boolean isValidStrength( double strength ) {
            return ABSConstants.STRONG_STRENGTH_RANGE.contains( strength );
        }
        
        public boolean isReactionBidirectional() {
            return false;
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
            super( name, symbol, ABSImages.B_MOLECULE, ABSImages.B_STRUCTURE, ABSConstants.B_COLOR, 
                    conjugateSymbol, ABSImages.BH_PLUS_MOLECULE, ABSImages.BH_PLUS_STRUCTURE, ABSConstants.BH_COLOR, strength );
        }
        
        public boolean isZeroNegligible() {
            return false;
        }
        
        protected boolean isValidStrength( double strength ) {
            return ABSConstants.WEAK_STRENGTH_RANGE.contains( strength );
        }
        
        public boolean isReactionBidirectional() {
            return true;
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
            super( ABSStrings.CUSTOM_BASE, "?", ABSImages.B_MOLECULE, ABSImages.B_STRUCTURE, ABSConstants.B_COLOR, 
                    "?", ABSImages.BH_PLUS_MOLECULE, ABSImages.BH_PLUS_STRUCTURE, ABSConstants.BH_COLOR, DEFAULT_STRENGTH );
            updateSymbol( getStrength() );
        }
        
        // public, so that custom base strength is mutable
        public void setStrength( double strength ) {
            updateSymbol( strength );
            super.setStrength( strength );
        }
        
        //XXX redo this
        private void updateSymbol( double strength ) {
            if ( isStrong() ) {
                setSymbol( ABSSymbols.MOH );
                setIcon( ABSImages.MOH_MOLECULE );
                setStructure( ABSImages.MOH_STRUCTURE );
                setColor( ABSConstants.MOH_COLOR );
                setConjugateSymbol( ABSSymbols.M_PLUS );
                setConjugateIcon( ABSImages.M_PLUS_MOLECULE );
                setConjugateStructure( ABSImages.M_PLUS_STRUCTURE );
                setConjugateColor( ABSConstants.M_COLOR );
            }
            else {
                setSymbol( ABSSymbols.B );
                setIcon( ABSImages.B_MOLECULE );
                setStructure( ABSImages.B_STRUCTURE );
                setColor( ABSConstants.B_COLOR );
                setConjugateSymbol( ABSSymbols.BH_PLUS );
                setConjugateIcon( ABSImages.BH_PLUS_MOLECULE );
                setConjugateStructure( ABSImages.BH_PLUS_STRUCTURE );
                setConjugateColor( ABSConstants.BH_COLOR );
            }
        }
        
        public boolean isZeroNegligible() {
            return isStrong();
        }
        
        protected boolean isValidStrength( double strength ) {
            return ABSConstants.CUSTOM_STRENGTH_RANGE.contains( strength );
        }
        
        public boolean isReactionBidirectional() {
            return !isStrong();
        }
    }
}
