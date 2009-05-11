package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;


public class StrongBase {
    
    // specific strong bases
    public static final StrongBase SODIUM_HYDROXIDE = new StrongBase( ABSStrings.SODIUM_HYDROXIDE, ABSSymbols.NaOH, ABSSymbols.Na_PLUS, 1.8E-5 );

    private final String name;
    private final String symbol;
    private final String metalSymbol;
    private double strength;
    
    private StrongBase( String name, String symbol, String metalSymbol, double strength ) {
        this.name = name;
        this.symbol = symbol;
        this.metalSymbol = metalSymbol;
        this.strength = strength;
    }
    
    public String getName() {
        return name;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public String getMetalSymbol() {
        return metalSymbol;
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
    
    public static class CustomStrongBase extends StrongBase {
        
        private static final double DEFAULT_STRENGTH = 20; //XXX
        
        public CustomStrongBase() {
            super( ABSStrings.CUSTOM_STRONG_BASE, ABSSymbols.MOH, ABSSymbols.M_PLUS, DEFAULT_STRENGTH );
        }
        
        public void setStrength( double strength ) {
            super.setStrength( strength );
        }
    }
}
