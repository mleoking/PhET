package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;


public class StrongAcid {
    
    // specific acids
    public static final StrongAcid HYDROCHLORIC_ACID = new StrongAcid( ABSStrings.HYDORCHLORIC_ACID, ABSSymbols.HCl, ABSSymbols.Cl_MINUS, 10E7 );

    private final String name;
    private final String symbol;
    private final String conjugateBaseSymbol;
    private double strength;
    
    private StrongAcid( String name, String symbol, String conjugateBaseSymbol, double strength ) {
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
        if ( !( ABSConstants.STRONG_STRENGTH_RANGE.contains( strength ) ) ) {
            throw new IllegalArgumentException( "strength out of range: " + strength );
        }
        if ( strength != this.strength ) {
            this.strength = strength;
            //XXX notify
        }
    }
    
    public static class CustomStrongAcid extends StrongAcid {
        
        private static final double DEFAULT_STRENGTH = ABSConstants.STRONG_STRENGTH_RANGE.getMin();
        
        public CustomStrongAcid() {
            super( ABSStrings.CUSTOM_STRONG_ACID, ABSSymbols.HA, ABSSymbols.A_MINUS, DEFAULT_STRENGTH );
        }
        
        public void setStrength( double strength ) {
            super.setStrength( strength );
        }
    }
}
