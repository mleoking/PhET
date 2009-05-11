package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;


public class WeakAcid {
    
    // specific acids
    public static final WeakAcid HYPOCHLORUS_ACID = new WeakAcid( ABSStrings.HYPOCHLOROUS_ACID, ABSSymbols.HClO, ABSSymbols.ClO_MINUS, 2.9E-8 );

    private final String name;
    private final String symbol;
    private final String conjugateBaseSymbol;
    private double strength;
    
    private WeakAcid( String name, String symbol, String conjugateBaseSymbol, double strength ) {
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
        if ( !( ABSConstants.WEAK_STRENGTH_RANGE.contains( strength ) ) ) {
            throw new IllegalArgumentException( "strength out of range: " + strength );
        }
        if ( strength != this.strength ) {
            this.strength = strength;
            //XXX notify
        }
    }
    
    public static class CustomAcid extends WeakAcid {
        
        private static final double DEFAULT_STRENGTH = ABSConstants.WEAK_STRENGTH_RANGE.getMin();
        
        public CustomAcid() {
            super( ABSStrings.CUSTOM_WEAK_ACID, ABSSymbols.HA, ABSSymbols.A_MINUS, DEFAULT_STRENGTH );
        }
        
        public void setStrength( double strength ) {
            super.setStrength( strength );
        }
    }
}
