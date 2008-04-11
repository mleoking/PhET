/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.model;


/**
 * Valley is the model of the valley where the glacier forms.
 * For the purposes of this simulation, the valley is a static entity 
 * that does not change over time.
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
 * The headwall is the highest cliff, where the glacier begins to form.
 * <p>
 * WARNING WARNING WARNING WARNING WARNING !!!
 * If you change anything in this class, you will likely break this simulation.
 * The Glacier model is a Hollywood model that was created to work for a single
 * Valley profile. And the image used to draw the valley floor and mountains 
 * was drawn for a specific Valley profile.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Valley {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // These constants affect the headwall, the steepest part of the valley floor.
    private static final double HEADWALL_STEEPNESS = 5000;
    private static final double HEADWALL_LENGTH = 800;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Valley() {}
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * Gets the headwall length.
     * 
     * @return
     */
    public static final double getHeadwallLength() {
        return HEADWALL_LENGTH;
    }
    
    /**
     * Gets the elevation at a position along the valley floor.
     * Slope starts off steep, then levels out as we go downvalley.
     * 
     * @param x position (meters)
     * @return elevation (meters)
     */
    public double getElevation( final double x ) {
        double elevation = 4e3 - ( x / 30. ) + Math.exp( -( x - HEADWALL_STEEPNESS ) / HEADWALL_LENGTH );
        if ( elevation < 0 ) {
            elevation = 0;
        }
        return elevation;
    }
    
    /**
     * Gets the width at a position along the valley floor.
     * Width is largest upvalley, then tapers down to constant further downvalley.
     * 
     * @param x position (meters)
     * @return width (meters)
     */
    public double getWidth( final double x ) {
        final double term = ( x - 5e3 ) / 2e3;
        return 1e3 + ( 5e3 * Math.exp( -( term * term ) ) );
    }
    
    /**
     * Gets the direction (angle) between two points on the valley floor.
     * 
     * @param x1 meters
     * @param x2 meters
     * @return radians
     */
    public double getDirection( final double x1, final double x2 ) {
        final double m = getSlope( x1, x2 );
        return Math.atan( m );
    }
    
    /*
     * Gets the slope between 2 points on the valley floor.
     * 
     * @param x1 meters
     * @param x2 meters
     * @return slope
     */
    private double getSlope( final double x1, final double x2 ) {
        if ( x1 == x2 ) {
            throw new IllegalArgumentException( "x1 and x2 must be different values" );
        }
        final double elevation1 = getElevation( x1 );
        final double elevation2 = getElevation( x2 );
        return ( elevation1 - elevation2 ) / ( x1 - x2 );
    }
}
