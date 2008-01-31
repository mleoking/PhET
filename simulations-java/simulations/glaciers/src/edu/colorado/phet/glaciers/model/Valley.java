/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;

/**
 * Valley is the model of the valley where the glacier forms.
 * For the purposes of this simulation, the valley is a static entity that does not change over time.
 * <p>
 * The coordinate system is:
 * <ul>
 * <li>x : position down the valley floor (meters)
 * <li>y : width across the valley floor (meters)
 * <li>z: elevation above sea level (meters)
 * </ul>
 * <p>
 * "Down the valley" indicates increasing x and decreasing z.
 * "Up the valley" indicates decreasing x and increasing z.
 * <p>
 * x=0 is at the point of highest elevation (maximum z), and
 * is the point where the glacier begins to form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Valley {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double MIN_X = 0; // meters, model is invalid for values less than this
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Valley() {}
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public double getMinX() {
        return MIN_X;
    }
    
    /**
     * Gets the elevation at a position along the valley floor.
     * Slope starts off steep, then levels out as we go down the valley.
     * 
     * @param x position (meters)
     * @return elevation (meters)
     */
    public double getElevation( double x ) {
        return 4e3 - ( x / 30. ) + Math.exp( -( x - 5e3 ) / 1e3 );
    }
    
    /**
     * Gets the width at a position along the valley floor.
     * Width is largest at the highest points of the valley, 
     * then tapers down to constant further down the valley.
     * 
     * @param x position (meters)
     * @return width (meters)
     */
    public double getWidth( double x ) {
        final double term = ( x - 5e3 ) / 2e3;
        return 1e3 + ( 5e3 * Math.exp( -( term * term ) ) );
    }
    
    /**
     * Gets the point of highest elevation in the valley.
     * 
     * @return highest point (x,elevation) in meters
     */
    public Point2D getHighestPoint() {
        return new Point2D.Double( 0, getElevation( 0 ) );
    }
}
