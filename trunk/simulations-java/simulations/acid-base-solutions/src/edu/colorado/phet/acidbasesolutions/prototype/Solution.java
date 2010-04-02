/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

/**
 * Base class for solution models.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
abstract class Solution extends Changeable {
    
    private double concentration, strength;

    public Solution( double concentration, double strength ) {
        this.concentration = concentration;
        this.strength = strength;
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
}
