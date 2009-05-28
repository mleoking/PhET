package edu.colorado.phet.acidbasesolutions.model;

import java.awt.Image;



public abstract class Molecule {

    private String name;
    private String symbol;
    private Image icon;
    
    protected Molecule( String name, String symbol, Image icon ) {
        this.name = name;
        this.symbol = symbol;
        this.icon = icon;
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
    
    public void setIcon( Image icon ) {
        this.icon = icon;
    }
    
    public Image getIcon() {
        return icon;
    }
}
