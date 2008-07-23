/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.glaciers.view.ModelViewTransform;


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
 * (see MountainsAndValleyNode) was drawn for a specific Valley profile.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Valley {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double HEADWALL_X = 0; // x coordinate where the headwall starts (meters) CHANGING THIS IS UNTESTED!!
    
    // These constants affect the headwall, the steepest part of the valley floor.
    private static final double HEADWALL_STEEPNESS = 5000;
    private static final double HEADWALL_LENGTH = 800;
    
    private static final double X_EQUALITY_THREHOLD = 1; // meters
    
    // 2D elevation change between foreground and background boundaries of the valley, used to draw pseudo-3D perspective
    private static final double PERSPECTIVE_HEIGHT = 250; // meters
    
    /*
     * WORKAROUND:
     * We don't have a model that matches the background image for x<0.
     * This set of points was chosen manually to roughly match the background image.
     * For x<0, we'll calculate elevation as a linear interpolation between these points.
     * These points are ordered by decreasing x value.
     */
    private static final Point2D[] NEGATIVE_X_SAMPLE_POINTS = {
        new Point2D.Double( -41, 4561 ),
        new Point2D.Double( -642, 4676 ),
        new Point2D.Double( -948, 4701 ),
        new Point2D.Double( -1094, 4716 ),
        new Point2D.Double( -1868, 4876 ),
        new Point2D.Double( -2077, 4746 ),
        new Point2D.Double( -2271, 4666 ),
        new Point2D.Double( -2787, 4566 ),
        new Point2D.Double( -3110, 4606 ),
        new Point2D.Double( -3255, 4566 ),
        new Point2D.Double( -3461, 4561 ),
        new Point2D.Double( -3513, 4561 ),
        new Point2D.Double( -3690, 4571 ),
        new Point2D.Double( -3803, 4546 ),
        new Point2D.Double( -3932, 4501 ),
        new Point2D.Double( -4029, 4441 ),
        new Point2D.Double( -4255, 4396 ),
        new Point2D.Double( -4416, 4346 )
    };
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Point2D _headwallPosition;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Valley() {
        _headwallPosition = new Point2D.Double( HEADWALL_X, getElevation( HEADWALL_X ) );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * Gets the height of the pseudo-3D perspective, in meters.
     * @return
     */
    public static double getPerspectiveHeight() {
        return PERSPECTIVE_HEIGHT;
    }
    
    /**
     * Gets a reference to the position where the headwall starts (the top of the headwall).
     * 
     * @return Point2D
     */
    public Point2D getHeadwallPositionReference() {
        return _headwallPosition;
    }
    
    /**
     * Gets the elevation at a position along the valley floor.
     * Slope starts off steep, then levels out as we go downvalley.
     * 
     * @param x position (meters)
     * @return elevation (meters)
     */
    public double getElevation( final double x ) {
        double elevation = 0;
        if ( x >= HEADWALL_X ) {
            elevation = 4e3 - ( x / 30. ) + Math.exp( -( x - HEADWALL_STEEPNESS ) / HEADWALL_LENGTH );
            if ( elevation < 0 ) {
                elevation = 0;
            }
        }
        else {
            elevation = getElevationToLeftOfHeadwall( x ); //WORKAROUND
        }
        return elevation;
    }
    
    /**
     * Convenience method for getting the maximum elevation.
     * This is the elevation at the top of the headwall.
     * 
     * @return max elevation (meters)
     */
    public double getMaxElevation() {
        return getElevation( _headwallPosition.getX() );
    }
    
    /**
     * Gets the x value for a specified elevation.
     * This uses a divide-and-conquer algorithm and is expensive.
     * 
     * @param elevation
     * @return
     */
    public double getX( final double elevation ) {
        double x = HEADWALL_X;
        double dx = 4000;
        boolean found = false;
        while ( !found ) {
            double e = getElevation( x );
            if ( Math.abs( e - elevation ) < X_EQUALITY_THREHOLD ) {
                found = true;
            }
            else {
                if ( ( e > elevation && dx < 0 ) || ( e < elevation && dx > 0 ) ) {
                    // reduce dx and change directions
                    dx = -dx / 2;
                }
                x += dx;
            }
        }
        return x;
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
     * Zero degrees point horizontally to the right.
     * 
     * @param x1 meters
     * @param x2 meters
     * @return radians
     */
    public double getDirection( final double x1, final double x2 ) {
        final double m = getSlope( x1, x2 );
        return Math.atan( m );
    }
    
    /**
     * Gets the headwall length.
     * 
     * @return
     */
    public static final double getHeadwallLength() {
        return HEADWALL_LENGTH;
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
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    /**
     * Creates a path (in view coordinates) that approximates the valley floor, from left to right.
     * 
     * @param mvt
     * @param minX
     * @param maxX
     * @param dx
     * @return GeneralPath
     */
    public GeneralPath createValleyFloorPath( ModelViewTransform mvt, double minX, double maxX, double dx ) {
        GeneralPath path = new GeneralPath();
        double elevation = 0;
        Point2D pModel = new Point2D.Double();
        Point2D pView = new Point2D.Double();
        double x = minX;
        while ( x <= maxX ) {
            elevation = getElevation( x );
            pModel.setLocation( x, elevation );
            pView = mvt.modelToView( pModel, pView );
            if ( x == minX ) {
                path.moveTo( (float) pView.getX(), (float) pView.getY() );
            }
            else {
                path.lineTo( (float) pView.getX(), (float) pView.getY() );
            }
            x += dx;
        }
        return path;
    }
    
    /*
     * WORKAROUND: see documentation of NEGATIVE_X_SAMPLE_POINTS above
     */
    private double getElevationToLeftOfHeadwall( double x ) {
        assert ( x < HEADWALL_X );
        double elevation = -1;
        Point2D leftMostSample = NEGATIVE_X_SAMPLE_POINTS[NEGATIVE_X_SAMPLE_POINTS.length - 1];
        if ( x < leftMostSample.getX() ) {
            elevation = leftMostSample.getY();
        }
        else {
            Point2D pLeft = null;
            Point2D pRight = null;
            for ( int i = 1; i < NEGATIVE_X_SAMPLE_POINTS.length; i++ ) {
                
                pLeft = NEGATIVE_X_SAMPLE_POINTS[i];
                if ( i == 0 ) {
                    pRight = new Point2D.Double( HEADWALL_X, getElevation( HEADWALL_X ) );
                }
                else {
                    pRight = NEGATIVE_X_SAMPLE_POINTS[i - 1]; 
                }
                
                if (  x >= pLeft.getX() && x <= pRight.getX() ) {
                    elevation = linearInterpolateElevation( x, pLeft, pRight );
                    break;
                }
            }
            assert ( elevation != -1 );
        }
        return elevation;
    }
    
    private static double linearInterpolateElevation( double x, Point2D pLeft, Point2D pRight ) {
        // how far is x along the line from pLeft.x to pRight.x?
        final double scale = Math.abs( ( x - pLeft.getX() ) / ( pLeft.getX() - pRight.getX() ) );
        // linear interpolation to find the elevation
        final double elevation = pLeft.getY() + scale * ( pRight.getY() - pLeft.getY() );
        return elevation;
    }
}
