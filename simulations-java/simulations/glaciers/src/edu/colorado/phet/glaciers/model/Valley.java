/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.model;


/**
 * Valley is the model of the valley where the glacier forms.
 * For the purposes of this simulation, the valley is a static entity that does not change over time.
 * <p>
 * The coordinate system is:
 * <ul>
 * <li>x : position downvalley (meters)
 * <li>y : width across the valley floor (meters)
 * <li>z : elevation above sea level (meters)
 * </ul>
 * <p>
 * Downvalley indicates increasing x and decreasing z.
 * Upvalley indicates decreasing x and increasing z.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Valley {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Valley() {}
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * Gets the elevation at a position along the valley floor.
     * Slope starts off steep, then levels out as we go downvalley.
     * 
     * @param x position (meters)
     * @return elevation (meters)
     */
    public double getElevation( double x ) {
        return 4e3 - ( x / 30. ) + Math.exp( -( x - 5e3 ) / 1e3 );
    }
    
    /**
     * Gets the width at a position along the valley floor.
     * Width is largest upvalley, then tapers down to constant further downvalley.
     * 
     * @param x position (meters)
     * @return width (meters)
     */
    public double getWidth( double x ) {
        final double term = ( x - 5e3 ) / 2e3;
        return 1e3 + ( 5e3 * Math.exp( -( term * term ) ) );
    }
}
