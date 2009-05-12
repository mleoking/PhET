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

    public static class CustomStrongBase extends StrongBase {

        private static final double DEFAULT_STRENGTH = ABSConstants.STRONG_STRENGTH_RANGE.getMin();

        public CustomStrongBase() {
            super( ABSStrings.CUSTOM_STRONG_BASE, ABSSymbols.MOH, DEFAULT_STRENGTH, ABSSymbols.M_PLUS );
        }

        // public setter for custom
        public void setStrength( double strength ) {
            super.setStrength( strength );
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
    
    public static class CustomWeakBase extends WeakBase {

        private static final double DEFAULT_STRENGTH = ABSConstants.WEAK_STRENGTH_RANGE.getMin();

        public CustomWeakBase() {
            super( ABSStrings.CUSTOM_WEAK_BASE, ABSSymbols.B, DEFAULT_STRENGTH, ABSSymbols.BH_PLUS );
        }

        // public setter for custom
        public void setStrength( double strength ) {
            super.setStrength( strength );
        }
    }
    
    //----------------------------------------------------------------------------
    // Intermediate bases
    //----------------------------------------------------------------------------

    public abstract static class IntermediateBase extends Base {

        private final String conjugateSymbol;

        private IntermediateBase( String name, String symbol, String conjugateSymbol, double strength ) {
            super( name, symbol, strength );
            this.conjugateSymbol = conjugateSymbol;
        }

        public String getConjugateSymbolSymbol() {
            return conjugateSymbol;
        }

        protected boolean isValidStrength( double strength ) {
            // exclusive of intermediate range bounds!
            return ( strength > ABSConstants.INTERMEDIATE_STRENGTH_RANGE.getMin() && strength < ABSConstants.INTERMEDIATE_STRENGTH_RANGE.getMax() );
        }
    }
    
    public static class CustomIntermediateBase extends IntermediateBase {
        
        private static final double DEFAULT_STRENGTH = ABSConstants.WEAK_STRENGTH_RANGE.getMax() + 1;

        public CustomIntermediateBase() {
            super( ABSStrings.CUSTOM_INTERMEDIATE_BASE, ABSSymbols.B, ABSSymbols.BH_PLUS, DEFAULT_STRENGTH );
        }

        // public setter for custom
        public void setStrength( double strength ) {
            super.setStrength( strength );
        }
    }
}
