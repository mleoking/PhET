package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;


public abstract class Base extends Solute {
    
    private Base( String name, String symbol, double strength ) {
        super( name, symbol, strength );
    }
    
    //----------------------------------------------------------------------------
    // Strong bases
    //----------------------------------------------------------------------------
    
    public abstract static class StrongBase extends Base {
        
        private final String metalSymbol;
        
        private StrongBase( String name, String symbol, double strength, String metalSymbol ) {
            super( name, symbol, strength );
            this.metalSymbol = metalSymbol;
        }
        
        public String getMetalSymbol() {
            return metalSymbol;
        }
        
        protected boolean isValidStrength( double strength ) {
            return ABSConstants.STRONG_STRENGTH_RANGE.contains( strength );
        }
    }
    
    public static class SodiumHydroxide extends StrongBase {
        public SodiumHydroxide() {
            super( ABSStrings.SODIUM_HYDROXIDE, ABSSymbols.NaOH, 1E7, ABSSymbols.Na_PLUS );
        }
    }

    //----------------------------------------------------------------------------
    // Weak bases
    //----------------------------------------------------------------------------

    public abstract static class WeakBase extends Base {
        
        private final String conjugateSymbol;
        
        private WeakBase( String name, String symbol, double strength, String conjugateSymbol ) {
            super( name, symbol, strength );
            this.conjugateSymbol = conjugateSymbol;
        }
        
        public String getConjugateSymbol() {
            return conjugateSymbol;
        }
        
        protected boolean isValidStrength( double strength ) {
            return ABSConstants.WEAK_STRENGTH_RANGE.contains( strength );
        }
    }

    public static class Ammonia extends WeakBase {
        public Ammonia() {
            super( ABSStrings.AMMONIA, ABSSymbols.NH3, 1.8E-5, ABSSymbols.NH4_PLUS );
        }
    }
    
    public static class Pyridine extends WeakBase {
        public Pyridine() {
            super( ABSStrings.PYRIDINE, ABSSymbols.C5H5N, 1.7E-9, ABSSymbols.C5H5NH_PLUS );
        }
    }
    
    //----------------------------------------------------------------------------
    // Custom base (strong, weak, or intermediate)
    //----------------------------------------------------------------------------

    public static class CustomBase extends Base {
        
        private static final double DEFAULT_STRENGTH = ABSConstants.WEAK_STRENGTH_RANGE.getMin();
        
        public CustomBase() {
            super( ABSStrings.CUSTOM_BASE, "" /* symbol depends on strength */, DEFAULT_STRENGTH );
            updateSymbol( getStrength() );
        }
        
        public String getConjugateSymbol() {
            return ABSSymbols.BH_PLUS;
        }
        
        public String getMetalSymbol() {
            return ABSSymbols.M_PLUS;
        }
        
        // public, so that custom base strength is mutable
        public void setStrength( double strength ) {
            updateSymbol( strength );
            super.setStrength( strength );
        }
        
        private void updateSymbol( double strength ) {
            if ( ABSConstants.STRONG_STRENGTH_RANGE.contains( strength ) ) {
                setSymbol( ABSSymbols.MOH );
            }
            else {
                setSymbol( ABSSymbols.B );
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
            return ABSConstants.WEAK_STRENGTH_RANGE.containsExclusive( getStrength() );
        }
    }
}
