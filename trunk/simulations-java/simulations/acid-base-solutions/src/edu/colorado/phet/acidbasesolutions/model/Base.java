package edu.colorado.phet.acidbasesolutions.model;

import java.awt.Color;
import java.awt.Image;

import edu.colorado.phet.acidbasesolutions.*;

/**
 * Base class for all bases.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Base extends Solute {
    
    private Base( String name, 
                  String symbol, Image icon, Image structure, Color color, 
                  String conjugateSymbol, Image conjugateIcon, Image conjugateStructure, Color conjugateColor, 
                  double strength ) {
        super( name, 
               symbol, icon, structure, color, 
               conjugateSymbol, conjugateIcon, conjugateStructure, conjugateColor, 
               strength );
    }
    
    //----------------------------------------------------------------------------
    // Strong bases
    //----------------------------------------------------------------------------
    
    public abstract static class StrongBase extends Base {
        
        // all strong bases have the same strength, in the middle of the range
        private static final double STRENGTH = ABSConstants.STRONG_STRENGTH_RANGE.getMin() +  ( ABSConstants.STRONG_STRENGTH_RANGE.getLength() / 2 );
        
        private StrongBase( String name, String symbol, String conjugateSymbol  ) {
            super( name, 
                   symbol, ABSImages.MOH_MOLECULE, ABSImages.MOH_STRUCTURE, ABSColors.MOH, 
                   conjugateSymbol, ABSImages.M_PLUS_MOLECULE, ABSImages.M_PLUS_STRUCTURE, ABSColors.M_PLUS, 
                   STRENGTH );
        }
        
        protected boolean isValidStrength( double strength ) {
            return ABSConstants.STRONG_STRENGTH_RANGE.contains( strength );
        }
    }
    
    public static class SodiumHydroxide extends StrongBase {
        public SodiumHydroxide() {
            super( ABSStrings.SODIUM_HYDROXIDE, ABSSymbols.NaOH, ABSSymbols.Na_PLUS );
        }
    }

    //----------------------------------------------------------------------------
    // Weak bases
    //----------------------------------------------------------------------------

    public abstract static class WeakBase extends Base {
        
        private WeakBase( String name, String symbol, String conjugateSymbol, double strength ) {
            super( name, 
                   symbol, ABSImages.B_MOLECULE, ABSImages.B_STRUCTURE, ABSColors.B, 
                   conjugateSymbol, ABSImages.BH_PLUS_MOLECULE, ABSImages.BH_PLUS_STRUCTURE, ABSColors.BH_PLUS,
                   strength );
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
            this( DEFAULT_STRENGTH );
        }
        
        public CustomBase( double strength ) {
            super( ABSStrings.CUSTOM_BASE, 
                   "symbol?", ABSImages.B_MOLECULE, ABSImages.B_STRUCTURE, ABSColors.B, 
                   "symbol?", ABSImages.BH_PLUS_MOLECULE, ABSImages.BH_PLUS_STRUCTURE, ABSColors.BH_PLUS, 
                   strength );
            updateSymbol( getStrength() );
        }
        
        protected boolean isValidStrength( double strength ) {
            return ABSConstants.CUSTOM_STRENGTH_RANGE.contains( strength );
        }
        
        // public, so that custom base strength is mutable
        public void setStrength( double strength ) {
            updateSymbol( strength );
            super.setStrength( strength );
        }
        
        //XXX redo this
        private void updateSymbol( double strength ) {
            if ( ABSConstants.STRONG_STRENGTH_RANGE.contains( strength ) ) {
                setSymbol( ABSSymbols.MOH );
                setIcon( ABSImages.MOH_MOLECULE );
                setStructure( ABSImages.MOH_STRUCTURE );
                setColor( ABSColors.MOH );
                setConjugateSymbol( ABSSymbols.M_PLUS );
                setConjugateIcon( ABSImages.M_PLUS_MOLECULE );
                setConjugateStructure( ABSImages.M_PLUS_STRUCTURE );
                setConjugateColor( ABSColors.M_PLUS );
            }
            else {
                setSymbol( ABSSymbols.B );
                setIcon( ABSImages.B_MOLECULE );
                setStructure( ABSImages.B_STRUCTURE );
                setColor( ABSColors.B );
                setConjugateSymbol( ABSSymbols.BH_PLUS );
                setConjugateIcon( ABSImages.BH_PLUS_MOLECULE );
                setConjugateStructure( ABSImages.BH_PLUS_STRUCTURE );
                setConjugateColor( ABSColors.BH_PLUS );
            }
        }
    }
}
