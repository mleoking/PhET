/* Copyright 2010, University of Colorado */

package edu.colorado.phet.advancedacidbasesolutions.model;

import java.awt.Color;
import java.awt.Image;

/**
 * Base class for molecules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Molecule {

    private String name;
    private String symbol;
    private Image icon;
    private Image structure;
    private Color color;
    
    protected Molecule( String name, String symbol, Image icon, Image structure, Color color ) {
        this.name = name;
        this.symbol = symbol;
        this.icon = icon;
        this.structure = structure;
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
    
    protected void setStructure( Image structure ) {
        this.structure = structure;
    }
    
    public Image getStructure() {
        return structure;
    }
    
    protected void setColor( Color color ) {
        this.color = color;
    }
    
    public Color getColor() {
        return color;
    }
}
