package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;


public abstract class Acid extends Solute {
    
    private final String conjugateSymbol;
    
    protected Acid( String name, String symbol, double strength, String conjugateSymbol ) {
        super( name, symbol, strength );
        this.conjugateSymbol = conjugateSymbol;
    }
    
    public String getConjugateSymbol() {
        return conjugateSymbol;
    }
    
    //----------------------------------------------------------------------------
    // Strong acids
    //----------------------------------------------------------------------------
    
    public abstract static class StrongAcid extends Acid {
        
        private StrongAcid( String name, String symbol, double strength, String conjugateSymbol ) {
            super( name, symbol, strength, conjugateSymbol );
        }
        
        protected boolean isValidStrength( double strength ) {
            return ABSConstants.STRONG_STRENGTH_RANGE.contains( strength );
        }
    }
    
    public static class HydrochloricAcid extends StrongAcid {
        public HydrochloricAcid() {
            super( ABSStrings.HYDROCHLORIC_ACID, ABSSymbols.HCl, 1E7, ABSSymbols.Cl_MINUS );
        }
    }
    
    public static class PerchloricAcid extends StrongAcid {
        public PerchloricAcid() {
            super( ABSStrings.PERCHLORIC_ACID, ABSSymbols.HClO4, 40, ABSSymbols.ClO4_MINUS );
        }
    }

    //----------------------------------------------------------------------------
    // Weak acids
    //----------------------------------------------------------------------------
    
    public abstract static class WeakAcid extends Acid {
        
        private WeakAcid( String name, String symbol, double strength, String conjugateSymbol ) {
            super( name, symbol, strength, conjugateSymbol );
        }
        
        protected boolean isValidStrength( double strength ) {
            return ABSConstants.WEAK_STRENGTH_RANGE.contains( strength );
        }
    }
    
    public static class ChlorousAcid extends WeakAcid {
        public ChlorousAcid() {
            super( ABSStrings.CHLOROUS_ACID, ABSSymbols.HClO2, 1E-2, ABSSymbols.ClO2_MINUS );
        }
    }
    
    public static class HypochlorusAcid extends WeakAcid {
        public HypochlorusAcid() {
            super( ABSStrings.HYPOCHLOROUS_ACID, ABSSymbols.HClO, 2.9E-8, ABSSymbols.ClO_MINUS );
        }
    }
    
    public static class HydrofluoricAcid extends WeakAcid {
        public HydrofluoricAcid() {
            super( ABSStrings.HYDROFLUORIC_ACID, ABSSymbols.HF, 6.8E-4, ABSSymbols.F_MINUS );
        }
    }
    
    public static class AceticAcid extends WeakAcid {
        public AceticAcid() {
            super( ABSStrings.ACETIC_ACID, ABSSymbols.CH3COOH, 1.8E-5, ABSSymbols.CH3COO_MINUS );
        }
    }
    
    //----------------------------------------------------------------------------
    // Custom acid (strong, weak, or intermediate)
    //----------------------------------------------------------------------------
    
    public static class CustomAcid extends Acid {
        
        private static final double DEFAULT_STRENGTH = ABSConstants.WEAK_STRENGTH_RANGE.getMin();
        
        public CustomAcid() {
            super( ABSStrings.CUSTOM_ACID, ABSSymbols.HA, DEFAULT_STRENGTH, ABSSymbols.A_MINUS );
        }

        // public, so that custom acid strength is mutable
        public void setStrength( double strength ) {
            super.setStrength( strength );
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
            return ABSConstants.WEAK_STRENGTH_RANGE.containsExclusive( getStrength() );
        }
    }

}
