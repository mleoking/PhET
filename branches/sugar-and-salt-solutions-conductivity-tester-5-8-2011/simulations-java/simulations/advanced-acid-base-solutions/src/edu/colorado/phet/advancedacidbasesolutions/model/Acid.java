// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.advancedacidbasesolutions.model;

import edu.colorado.phet.advancedacidbasesolutions.*;

/**
 * Base class for all acids.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Acid extends Solute {
    
    protected Acid( String name, String symbol, String conjugateSymbol, double strength  ) {
        super( name, 
               symbol, AABSImages.HA_MOLECULE, AABSImages.HA_STRUCTURE, AABSColors.HA, 
               conjugateSymbol, AABSImages.A_MINUS_MOLECULE, AABSImages.A_MINUS_STRUCTURE, AABSColors.A_MINUS, 
               strength );
    }
    
    public String getStrengthSymbol() {
        return AABSSymbols.Ka;
    }
    
    //----------------------------------------------------------------------------
    // Strong acids
    //----------------------------------------------------------------------------
    
    public abstract static class StrongAcid extends Acid {
        
        // all strong bases have the same strength, in the middle of the range
        private static final double STRENGTH = AABSConstants.STRONG_STRENGTH_RANGE.getMin() +  ( AABSConstants.STRONG_STRENGTH_RANGE.getLength() / 2 );
        
        private StrongAcid( String name, String symbol, String conjugateSymbol ) {
            super( name, symbol, conjugateSymbol, STRENGTH );
        }
        
        protected boolean isValidStrength( double strength ) {
            return AABSConstants.STRONG_STRENGTH_RANGE.contains( strength );
        }
    }
    
    public static class HydrochloricAcid extends StrongAcid {
        public HydrochloricAcid() {
            super( AABSStrings.HYDROCHLORIC_ACID, AABSSymbols.HCl, AABSSymbols.Cl_MINUS );
        }
    }
    
    public static class PerchloricAcid extends StrongAcid {
        public PerchloricAcid() {
            super( AABSStrings.PERCHLORIC_ACID, AABSSymbols.HClO4, AABSSymbols.ClO4_MINUS );
        }
    }

    //----------------------------------------------------------------------------
    // Weak acids
    //----------------------------------------------------------------------------
    
    public abstract static class WeakAcid extends Acid {
        
        private WeakAcid( String name, String symbol, String conjugateSymbol, double strength ) {
            super( name, symbol, conjugateSymbol, strength );
        }
        
        protected boolean isValidStrength( double strength ) {
            return AABSConstants.WEAK_STRENGTH_RANGE.contains( strength );
        }
    }
    
    public static class ChlorousAcid extends WeakAcid {
        public ChlorousAcid() {
            super( AABSStrings.CHLOROUS_ACID, AABSSymbols.HClO2, AABSSymbols.ClO2_MINUS, 1E-2 );
        }
    }
    
    public static class HypochlorusAcid extends WeakAcid {
        public HypochlorusAcid() {
            super( AABSStrings.HYPOCHLOROUS_ACID, AABSSymbols.HClO, AABSSymbols.ClO_MINUS, 2.9E-8 );
        }
    }
    
    public static class HydrofluoricAcid extends WeakAcid {
        public HydrofluoricAcid() {
            super( AABSStrings.HYDROFLUORIC_ACID, AABSSymbols.HF, AABSSymbols.F_MINUS, 6.8E-4 );
        }
    }
    
    public static class AceticAcid extends WeakAcid {
        public AceticAcid() {
            super( AABSStrings.ACETIC_ACID, AABSSymbols.CH3COOH, AABSSymbols.CH3COO_MINUS, 1.8E-5 );
        }
    }
    
    //----------------------------------------------------------------------------
    // Custom acid (strong, weak, or intermediate)
    //----------------------------------------------------------------------------
    
    public static class CustomAcid extends Acid implements ICustomSolute {
        
        private static final double DEFAULT_STRENGTH = AABSConstants.WEAK_STRENGTH_RANGE.getMin();
        
        public CustomAcid() {
            this( DEFAULT_STRENGTH );
        }
        
        public CustomAcid( double strength ) {
            super( AABSStrings.CUSTOM_ACID, AABSSymbols.HA, AABSSymbols.A_MINUS, strength );
        }
        
        protected boolean isValidStrength( double strength ) {
            return AABSConstants.CUSTOM_STRENGTH_RANGE.contains( strength );
        }

        // public, so that custom acid strength is mutable
        public void setStrength( double strength ) {
            super.setStrength( strength );
        }
    }

}
