package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.*;

/**
 * Base class for all acids.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Acid extends Solute {
    
    protected Acid( String name, String symbol, String conjugateSymbol, double strength  ) {
        super( name, 
               symbol, ABSImages.HA_MOLECULE, ABSImages.HA_STRUCTURE, ABSColors.HA, 
               conjugateSymbol, ABSImages.A_MINUS_MOLECULE, ABSImages.A_MINUS_STRUCTURE, ABSColors.A_MINUS, 
               strength );
    }
    
    //----------------------------------------------------------------------------
    // Strong acids
    //----------------------------------------------------------------------------
    
    public abstract static class StrongAcid extends Acid {
        
        private StrongAcid( String name, String symbol, String conjugateSymbol, double strength ) {
            super( name, symbol, conjugateSymbol, strength );
        }
        
        protected boolean isValidStrength( double strength ) {
            return ABSConstants.STRONG_STRENGTH_RANGE.contains( strength );
        }
    }
    
    public static class HydrochloricAcid extends StrongAcid {
        public HydrochloricAcid() {
            super( ABSStrings.HYDROCHLORIC_ACID, ABSSymbols.HCl, ABSSymbols.Cl_MINUS, 25 );
        }
    }
    
    public static class PerchloricAcid extends StrongAcid {
        public PerchloricAcid() {
            super( ABSStrings.PERCHLORIC_ACID, ABSSymbols.HClO4, ABSSymbols.ClO4_MINUS, 25 );
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
            return ABSConstants.WEAK_STRENGTH_RANGE.contains( strength );
        }
    }
    
    public static class ChlorousAcid extends WeakAcid {
        public ChlorousAcid() {
            super( ABSStrings.CHLOROUS_ACID, ABSSymbols.HClO2, ABSSymbols.ClO2_MINUS, 1E-2 );
        }
    }
    
    public static class HypochlorusAcid extends WeakAcid {
        public HypochlorusAcid() {
            super( ABSStrings.HYPOCHLOROUS_ACID, ABSSymbols.HClO, ABSSymbols.ClO_MINUS, 2.9E-8 );
        }
    }
    
    public static class HydrofluoricAcid extends WeakAcid {
        public HydrofluoricAcid() {
            super( ABSStrings.HYDROFLUORIC_ACID, ABSSymbols.HF, ABSSymbols.F_MINUS, 6.8E-4 );
        }
    }
    
    public static class AceticAcid extends WeakAcid {
        public AceticAcid() {
            super( ABSStrings.ACETIC_ACID, ABSSymbols.CH3COOH, ABSSymbols.CH3COO_MINUS, 1.8E-5 );
        }
    }
    
    //----------------------------------------------------------------------------
    // Custom acid (strong, weak, or intermediate)
    //----------------------------------------------------------------------------
    
    public static class CustomAcid extends Acid implements ICustomSolute {
        
        private static final double DEFAULT_STRENGTH = ABSConstants.WEAK_STRENGTH_RANGE.getMin();
        
        public CustomAcid() {
            super( ABSStrings.CUSTOM_ACID, ABSSymbols.HA, ABSSymbols.A_MINUS, DEFAULT_STRENGTH );
        }
        
        protected boolean isValidStrength( double strength ) {
            return ABSConstants.CUSTOM_STRENGTH_RANGE.contains( strength );
        }

        // public, so that custom acid strength is mutable
        public void setStrength( double strength ) {
            super.setStrength( strength );
        }
    }

}
