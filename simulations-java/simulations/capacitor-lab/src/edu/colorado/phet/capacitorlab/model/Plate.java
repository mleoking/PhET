/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;


/**
 * Model of a capacitor plate.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Plate extends Box {
    
    public Plate( double width, double height, double depth ) {
        super( width, height, depth );
    }
    
    public double getArea() {
        return getWidth() * getDepth();
    }
}
