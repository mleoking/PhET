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



/**
 * PotentialRegion is an immutable class for describing a region of potential energy.
 * IT IS ESSENTIAL THAT THIS CLASS REMAIN IMMUTABLE!
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PotentialRegion {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _start;
    private double _end;
    private double _energy;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Creates a region of potential energy.
     * The energy level is contant within the region.
     * 
     * @param start
     * @param end
     * @param energy
     * @throws IllegalArgumentException if the region's width is not > 0
     */
    public PotentialRegion( final double start, final double end, final double energy ) {
        if ( start >= end ) {
            throw new IllegalArgumentException( "start >= end, region must have a positive width" );
        }
        _start = start;
        _end = end;
        _energy = energy;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the start position.
     * 
     * @return start position
     */
    public double getStart() {
        return _start;
    }
    
    /**
     * Gets the end position.
     * 
     * @return end position
     */
    public double getEnd() {
        return _end;
    }
    
    /**
     * Gets the middle position of the region.
     * 
     * @return
     */
    public double getMiddle() {
        return _start + ( getWidth() / 2 );
    }
    
    /**
     * Gets the potential energy level in the region.
     * 
     * @return the energy
     */
    public double getEnergy() {
        return _energy;
    }
    
    /**
     * Gets the width of the region.
     * If either endpoint is at infinity, then the width is infinity.
     * 
     * @return width
     */
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
