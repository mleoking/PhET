/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.util.PDimension;

/**
 * pH Meter model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PHMeter extends ABSModelElement {
    
    private final PDimension shaftSize;
    private final PDimension tipSize;
    
    public PHMeter( Point2D location, boolean visible, PDimension shaftSize, PDimension tipSize ) {
        super( location, visible );
        this.shaftSize = shaftSize;
        this.tipSize = tipSize;
    }

    /**
     * The shaft connects the value display to the tip that does the pH sensing.
     * @return
     */
    public PDimension getShaftSizeReference() {
        return shaftSize;
    }
    
    /**
     * The tip is the pointed part that contains the pH sensor.
     * The meter's origin is at the pointy end of the tip.
     * @return
     */
    public PDimension getTipSizeRefernence() {
        return tipSize;
    }
    
    /**
     * The probe consists of the shaft and the tip.
     * It's useful to know how long the probe is so that we can submerge the entire
     * probe without submerging the digital display portion of the meter.
     * @return
     */
    public double getProbeHeight() {
        return shaftSize.getHeight() + tipSize.getHeight();
    }
}
