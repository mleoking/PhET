/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

/**
 * Model of wire that connects the battery and capacitor.
 * This is an immutable container for some wire properties.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Wire {
    
    private final double thickness; // mm
    private final double extent; // how far the wire extends vertically from the capacitor's orgin (mm)
    
    public Wire( double thickness, double extent ) {
        this.thickness = thickness;
        this.extent = extent;
    }
    
    public double getThickness() {
        return thickness;
    }
    
    public double getExtent() {
        return extent;
    }

}
