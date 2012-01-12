// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.advancedacidbasesolutions.model;

import java.awt.Color;
import java.awt.Image;

import edu.colorado.phet.advancedacidbasesolutions.*;

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
    
    public String getStrengthSymbol() {
        return AABSSymbols.Kb;
    }
    
    //----------------------------------------------------------------------------
    // Strong bases
    //----------------------------------------------------------------------------
    
    public abstract static class StrongBase extends Base {
        
        // all strong bases have the same strength, in the middle of the range
        private static final double STRENGTH = AABSConstants.STRONG_STRENGTH_RANGE.getMin() +  ( AABSConstants.STRONG_STRENGTH_RANGE.getLength() / 2 );
        
        private StrongBase( String name, String symbol, String conjugateSymbol  ) {
            super( name, 
                   symbol, AABSImages.MOH_MOLECULE, AABSImages.MOH_STRUCTURE, AABSColors.MOH, 
                   conjugateSymbol, AABSImages.M_PLUS_MOLECULE, AABSImages.M_PLUS_STRUCTURE, AABSColors.M_PLUS, 
                   STRENGTH );
        }
        
        protected boolean isValidStrength( double strength ) {
            return AABSConstants.STRONG_STRENGTH_RANGE.contains( strength );
        }
    }
    
    public static class SodiumHydroxide extends StrongBase {
        public SodiumHydroxide() {
            super( AABSStrings.SODIUM_HYDROXIDE, AABSSymbols.NaOH, AABSSymbols.Na_PLUS );
        }
    }

    //----------------------------------------------------------------------------
    // Weak bases
    //----------------------------------------------------------------------------

    public abstract static class WeakBase extends Base {
        
        private WeakBase( String name, String symbol, String conjugateSymbol, double strength ) {
            super( name, 
                   symbol, AABSImages.B_MOLECULE, AABSImages.B_STRUCTURE, AABSColors.B, 
                   conjugateSymbol, AABSImages.BH_PLUS_MOLECULE, AABSImages.BH_PLUS_STRUCTURE, AABSColors.BH_PLUS,
                   strength );
        }
        
        protected boolean isValidStrength( double strength ) {
            return AABSConstants.WEAK_STRENGTH_RANGE.contains( strength );
        }
    }

    public static class Ammonia extends WeakBase {
        public Ammonia() {
            super( AABSStrings.AMMONIA, AABSSymbols.NH3, AABSSymbols.NH4_PLUS, 1.8E-5 );
        }
    }
    
    public static class Pyridine extends WeakBase {
        public Pyridine() {
            super( AABSStrings.PYRIDINE, AABSSymbols.C5H5N, AABSSymbols.C5H5NH_PLUS, 1.7E-9 );
        }
    }
    
    //----------------------------------------------------------------------------
    // Custom base (strong, weak, or intermediate)
    //----------------------------------------------------------------------------

    public static class CustomBase extends Base implements ICustomSolute {
        
        private static final double DEFAULT_STRENGTH = AABSConstants.WEAK_STRENGTH_RANGE.getMin();
        
        public CustomBase() {
            this( DEFAULT_STRENGTH );
        }
        
        public CustomBase( double strength ) {
            super( AABSStrings.CUSTOM_BASE, 
                   "symbol?", AABSImages.B_MOLECULE, AABSImages.B_STRUCTURE, AABSColors.B, 
                   "symbol?", AABSImages.BH_PLUS_MOLECULE, AABSImages.BH_PLUS_STRUCTURE, AABSColors.BH_PLUS, 
                   strength );
            updateSymbol( getStrength() );
        }
        
        protected boolean isValidStrength( double strength ) {
            return AABSConstants.CUSTOM_STRENGTH_RANGE.contains( strength );
        }
        
        // public, so that custom base strength is mutable
        public void setStrength( double strength ) {
            updateSymbol( strength );
            super.setStrength( strength );
        }
        
        //XXX redo this
        private void updateSymbol( double strength ) {
            if ( AABSConstants.STRONG_STRENGTH_RANGE.contains( strength ) ) {
                setSymbol( AABSSymbols.MOH );
                setIcon( AABSImages.MOH_MOLECULE );
                setStructure( AABSImages.MOH_STRUCTURE );
                setColor( AABSColors.MOH );
                setConjugateSymbol( AABSSymbols.M_PLUS );
                setConjugateIcon( AABSImages.M_PLUS_MOLECULE );
                setConjugateStructure( AABSImages.M_PLUS_STRUCTURE );
                setConjugateColor( AABSColors.M_PLUS );
            }
            else {
                setSymbol( AABSSymbols.B );
                setIcon( AABSImages.B_MOLECULE );
                setStructure( AABSImages.B_STRUCTURE );
                setColor( AABSColors.B );
                setConjugateSymbol( AABSSymbols.BH_PLUS );
                setConjugateIcon( AABSImages.BH_PLUS_MOLECULE );
                setConjugateStructure( AABSImages.BH_PLUS_STRUCTURE );
                setConjugateColor( AABSColors.BH_PLUS );
            }
        }
    }
}
