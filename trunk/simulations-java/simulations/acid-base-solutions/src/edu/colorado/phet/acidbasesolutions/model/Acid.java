package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;


public class Acid {
    
    // specific acids
    public static final Acid HYDROCHLORIC_ACID = new Acid( ABSStrings.HYDORCHLORIC_ACID, ABSSymbols.HCl, ABSSymbols.Cl_MINUS, 10E7 );
    public static final Acid HYPOCHLORUS_ACID = new Acid( ABSStrings.HYPOCHLOROUS_ACID, ABSSymbols.HClO, ABSSymbols.ClO_MINUS, 2.9E-8 );

    private final String name;
    private final String symbol;
    private final String conjugateBaseSymbol;
    private double strength;
    
    protected Acid( String name, String symbol, String conjugateBaseSymbol, double strength ) {
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
        if ( strength != this.strength ) {
            this.strength = strength;
            //XXX notify
        }
    }
    
    public boolean isStrong() {
        return strength >= 20; //XXX
    }
    
    public static class CustomAcid extends Acid {
        
        private static final double DEFAULT_STRENGTH = 10E-10; //XXX
        
        public CustomAcid() {
            super( ABSStrings.CUSTOM_ACID, ABSSymbols.HA, ABSSymbols.A_MINUS, DEFAULT_STRENGTH );
        }
        
        public void setStrength( double strength ) {
            super.setStrength( strength );
        }
    }
}
