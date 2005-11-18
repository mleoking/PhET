/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.model;

import java.awt.geom.Point2D;


/**
 * PotentialRegion is an immutable class for describing a region of potential energy.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PotentialRegion {

    private double _start;
    private double _end;
    private double _energy;
    
    public PotentialRegion( double start, double end, double energy ) {
        if ( start >= end ) {
            throw new IllegalArgumentException( "start >= end, region must have a positive width" );
        }
        _start = start;
        _end = end;
        _energy = energy;
    }
    
    public double getStart() {
        return _start;
    }
    
    public double getEnd() {
        return _end;
    }
    
    public double getEnergy() {
        return _energy;
    }
    
    public double getWidth() {
        double width = 0;
        if ( _start == Double.NEGATIVE_INFINITY || _end == Double.POSITIVE_INFINITY ) {
            width = Double.POSITIVE_INFINITY;
        }
        else {
            width = _end - _start;
        }
        return width;
    }
}
