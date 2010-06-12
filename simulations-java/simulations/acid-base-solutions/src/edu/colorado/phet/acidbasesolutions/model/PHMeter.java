/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import java.awt.geom.Point2D;

/**
 * pH Meter model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PHMeter extends ABSModelElement {
    
    public final double shaftLength;
    
    public PHMeter( Point2D location, boolean visible, double shaftLength ) {
        super( location, visible );
        this.shaftLength = shaftLength;
    }

    public double getShaftLength() {
        return shaftLength;
    }
}
