package edu.colorado.phet.acidbasesolutions.model;



public abstract class Molecule {

    private String name;
    private String symbol;
    
    protected Molecule( String name, String symbol ) {
        this.name = name;
        this.symbol = symbol;
    }
    
    protected void setName( String name ) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    protected void setSymbol( String symbol ) {
        this.symbol = symbol;
    }
    
    public String getSymbol() {
        return symbol;
    }
}
