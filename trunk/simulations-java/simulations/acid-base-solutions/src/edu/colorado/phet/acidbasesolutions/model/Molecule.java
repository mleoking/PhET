package edu.colorado.phet.acidbasesolutions.model;

import java.awt.Color;
import java.awt.Image;



public abstract class Molecule {

    private String name;
    private String symbol;
    private Image icon;
    private Color color;
    
    protected Molecule( String name, String symbol, Image icon, Color color ) {
        this.name = name;
        this.symbol = symbol;
        this.icon = icon;
        this.color = color;
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
    
    protected void setIcon( Image icon ) {
        this.icon = icon;
    }
    
    public Image getIcon() {
        return icon;
    }
    
    protected void setColor( Color color ) {
        this.color = color;
    }
    
    public Color getColor() {
        return color;
    }
}
