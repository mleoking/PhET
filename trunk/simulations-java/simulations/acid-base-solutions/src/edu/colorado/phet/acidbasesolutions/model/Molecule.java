package edu.colorado.phet.acidbasesolutions.model;


public abstract class Molecule {

    private final String name;
    private final String symbol;
    
    protected Molecule( String name, String symbol ) {
        this.name = name;
        this.symbol = symbol;
    }
    
    public String getName() {
        return name;
    }
    
    public String getSymbol() {
        return symbol;
    }
}
