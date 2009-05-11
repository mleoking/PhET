package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;


public class IntermediateBase {
    
    private final String name;
    private final String symbol;
    private final String conjugateAcidSymbol;
    private double strength;
    
    private IntermediateBase( String name, String symbol, String conjugateAcidSymbol, double strength ) {
        this.name = name;
        this.symbol = symbol;
        this.conjugateAcidSymbol = conjugateAcidSymbol;
        this.strength = strength;
    }
    
    public String getName() {
        return name;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public String getConjugateAcidSymbolSymbol() {
        return conjugateAcidSymbol;
    }
    
    protected void setStrength( double strength ) {
        if ( !( strength > ABSConstants.WEAK_STRENGTH_RANGE.getMax() && strength < ABSConstants.STRONG_STRENGTH_RANGE.getMin() ) ) {
            throw new IllegalArgumentException( "strength out of range: " + strength );
        }
        if ( strength != this.strength ) {
            this.strength = strength;
            //XXX notify
        }
    }
    
    public double getStrength() {
        return strength;
    }
    
    public static class CustomIntermediateBase extends IntermediateBase {
        
        private static final double DEFAULT_STRENGTH = ABSConstants.WEAK_STRENGTH_RANGE.getMax() + 1;
        
        public CustomIntermediateBase() {
            super( ABSStrings.CUSTOM_WEAK_BASE, ABSSymbols.B, ABSSymbols.BH_PLUS, DEFAULT_STRENGTH );
        }
        
        public void setStrength( double strength ) {
            super.setStrength( strength );
        }
    }
}
