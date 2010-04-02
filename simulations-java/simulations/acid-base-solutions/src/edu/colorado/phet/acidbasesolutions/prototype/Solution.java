/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.Color;

/**
 * Base class for solution models.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
abstract class Solution extends Changeable {
    
    private double concentration, strength;
    private Color color;

    public Solution( double concentration, double strength, Color color ) {
        this.concentration = concentration;
        this.strength = strength;
        this.color = color;
    }
    
    public void setConcentration( double concentration ) {
        if ( concentration != this.concentration ) {
            this.concentration = concentration;
            fireStateChanged();
        }
    }
    
    public double getConcentration() {
        return concentration;
    }
    
    public void setStrength( double strength ) {
        if ( strength != this.strength ) {
            this.strength = strength;
            fireStateChanged();
        }
    }
    
    public double getStrength() {
        return strength;
    }
    
    public void setColor( Color color ) {
        if ( !color.equals( this.color ) ) {
            this.color = color;
            fireStateChanged();
        }
    }
    
    public Color getColor() {
        return color;
    }
}
