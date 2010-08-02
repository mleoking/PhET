/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.util.PDimension;

/**
 * pH Meter model.
 * Location is at the tip of the meter's probe.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PHMeter extends SolutionRepresentation {
    
    private final PDimension shaftSize;
    private final PDimension tipSize;
    private final Beaker beaker;
    
    public PHMeter( AqueousSolution solution, Point2D location, boolean visible, PDimension shaftSize, PDimension tipSize, Beaker beaker ) {
        super( solution, location, visible );
        this.shaftSize = shaftSize;
        this.tipSize = tipSize;
        this.beaker = beaker;
    }

    @Override
    public void setLocation( double x, double y ) {
        super.setLocation( constrainX( x ), constrainY( y ) );
    }
    
    /*
     * Constrains an x coordinate to be between the walls of the beaker.
     */
    private double constrainX( double requestedX ) {
        double min = beaker.getLocationReference().getX() - ( beaker.getWidth() / 2 ) + ( tipSize.width / 2 );
        double max = beaker.getLocationReference().getX() + ( beaker.getWidth() / 2 ) - ( tipSize.width / 2 );
        double x = requestedX;
        if ( x < min ) {
            x = min;
        }
        else if ( x > max ) {
            x = max;
        }
        return x;
    }
    
    /*
     * Constraints a y coordinate to be in or slightly above the solution,
     * but keeps the pH meter's display from being submerged in the solution.
     */
    private double constrainY( double requestedY ) {
        double min = beaker.getLocationReference().getY() - beaker.getHeight() - 20;
        double max = beaker.getLocationReference().getY() - beaker.getHeight() + getProbeHeight();
        double y = requestedY;
        if ( y < min ) {
            y = min;
        }
        else if ( y > max ) {
            y = max;
        }
        return y;
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
    
    /**
     * Gets the pH value that is to be displayed by the meter.
     * If the meter is not in the solution, this returns null.
     * @return
     */
    public Double getValue() {
        Double value = null;
        if ( beaker.inSolution( getLocationReference() ) ) {
            value = new Double( getSolution().getPH() );
        }
        return value;
    }
}
