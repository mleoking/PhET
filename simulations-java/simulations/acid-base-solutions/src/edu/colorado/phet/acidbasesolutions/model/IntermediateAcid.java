package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;


public class IntermediateAcid {
    
    private final String name;
    private final String symbol;
    private final String conjugateBaseSymbol;
    private double strength;
    
    private IntermediateAcid( String name, String symbol, String conjugateBaseSymbol, double strength ) {
        this.name = name;
        this.symbol = symbol;
        this.conjugateBaseSymbol = conjugateBaseSymbol;
        this.strength = strength;
    }
    
    public String getName() {
        return name;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public String getConjugateBaseSymbol() {
        return conjugateBaseSymbol;
    }
    
    public double getStrength() {
        return strength;
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
    
    public static class CustomIntermediateAcid extends IntermediateAcid {
        
        private static final double DEFAULT_STRENGTH = ABSConstants.WEAK_STRENGTH_RANGE.getMax() + 1;
        
        public CustomIntermediateAcid() {
            super( ABSStrings.CUSTOM_INTERMEDIATE_ACID, ABSSymbols.HA, ABSSymbols.A_MINUS, DEFAULT_STRENGTH );
        }
        
        public void setStrength( double strength ) {
            super.setStrength( strength );
        }
    }
}
