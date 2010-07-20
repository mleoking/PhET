/* Copyright 2004-2010, University of Colorado */

package edu.colorado.phet.faraday.model;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.faraday.FaradayResources;
import edu.colorado.phet.faraday.FaradayStrings;
import edu.colorado.phet.faraday.util.Vector2D;

/**
 * Model of a bar magnet that uses a grid of precomputed B-field values.
 * This model was motivated by Unfuddle #2236.
 * <p>
 * It was not feasible to implement a numerical model directly in Java, as it relies on double integrals.
 * So the model was implemented in MathCAD, and MathCAD was used to create a grid of B-field vectors.
 * That grid is stored in 2 files (one for Bx, one for By).
 * This simulation reads those files, and computes the B-field at a specified point using 
 * the grid and an interpolation algorithm.
 * <p>
 * The grid assumes that the magnet's center is at the origin, and the grid includes only 
 * the quadrant where x and y are both positive (lower-right quadrant in our coordinate system).
 * <p>
 * Our coordinate system has +x to the left, and +y down, with quadrants numbered like this: 
 * <code>
 * Q3 | Q2
 * -------
 * Q4 | Q1 
 * </code>
 * The grid files contain the B-field components for Q1, with values in column-major order.
 * (This means that the x coordinate changes more slowly than the y coordinate.)
 * x and y coordinates both start at 0.
 * <p>
 * After locating a B-field vector in Q1, here's how to map it to one of the other quadrants: 
 * Q2: reflect about the x axis, so multiply By by -1 
 * Q3: reflect through the origin, so no change
 * Q4: reflect about the x axis and reflect through the origin, so so multiply By by -1
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BarMagnet extends AbstractMagnet {

    // values using in MathCAD
    private static final double GRID_MAGNET_STRENGTH = 1; // Gauss
    private static final double INTERNAL_GRID_SPACING = 5; // same in both dimensions
    private static final double EXTERNAL_NEAR_GRID_SPACING = 5; // same in both dimensions
    private static final double EXTERNAL_FAR_GRID_SPACING = 20; // same in both dimensions
    private static final Dimension INTERNAL_GRID_SIZE = new Dimension( 6, 26 ); // number of points in the grid
    private static final Dimension EXTERNAL_NEAR_GRID_SIZE = new Dimension( 101, 81 ); // number of points in the grid
    private static final Dimension EXTERNAL_FAR_GRID_SIZE = new Dimension( 126, 61 ); // number of points in the grid
    
    // grid file names
    private static final String BX_INTERNAL_RESOURCE_NAME = "bfield/BX_internal.csv"; 
    private static final String BY_INTERNAL_RESOURCE_NAME = "bfield/BY_internal.csv";
    private static final String BX_EXTERNAL_NEAR_RESOURCE_NAME = "bfield/BX_external_near.csv"; 
    private static final String BY_EXTERNAL_NEAR_RESOURCE_NAME = "bfield/BY_external_near.csv";
    private static final String BX_EXTERNAL_FAR_RESOURCE_NAME = "bfield/BX_external_far.csv"; 
    private static final String BY_EXTERNAL_FAR_RESOURCE_NAME = "bfield/BY_external_far.csv";
    
    private final Grid internalGrid, externalNearGrid, externalFarGrid;
    
    public BarMagnet() {
        super();
        internalGrid = new Grid( BX_INTERNAL_RESOURCE_NAME, BY_INTERNAL_RESOURCE_NAME, INTERNAL_GRID_SIZE, INTERNAL_GRID_SPACING );
        externalNearGrid = new Grid( BX_EXTERNAL_NEAR_RESOURCE_NAME, BY_EXTERNAL_NEAR_RESOURCE_NAME, EXTERNAL_NEAR_GRID_SIZE, EXTERNAL_NEAR_GRID_SPACING );
        externalFarGrid = new Grid( BX_EXTERNAL_FAR_RESOURCE_NAME, BY_EXTERNAL_FAR_RESOURCE_NAME, EXTERNAL_FAR_GRID_SIZE, EXTERNAL_FAR_GRID_SPACING );
    }

    /**
     * Gets the B-field vector at a point in the magnet's local 2D coordinate frame.
     * 
     * @param p
     * @param outputVector
     * @return outputVector
     */
    protected Vector2D getBFieldRelative( Point2D p, Vector2D outputVector ) {
        
        assert( p != null );
        assert( outputVector != null );
        
        outputVector.zero();

        // find B-field by interpolating grid points
        double x = getBx( p.getX(), p.getY() );
        double y = getBy( p.getX(), p.getY() );
        outputVector.setXY( x, y );

        // scale based on magnet strength
        outputVector.scale( getStrength() / GRID_MAGNET_STRENGTH ); 
        
        return outputVector;
    }
    
    /*
     * Get Bx component for a point relative to the magnet's origin.
     * This component is identical in all 4 quadrants.
     * See class javadoc.
     */
    private double getBx( final double x, final double y ) {
        Grid grid = chooseGrid( x, y );
        return interpolate( Math.abs( x ), Math.abs( y ), grid.getMaxX(), grid.getMaxY(), grid.getBxArray(), grid.getGridSpacing() );
    }
    
    private Grid chooseGrid( double x, double y ) {
        Grid grid = null;
        if ( internalGrid.contains( x, y ) ) {
            grid = internalGrid;
        }
        else if ( externalNearGrid.contains( x, y ) ) {
            grid = externalNearGrid;
        }
        else {
            grid = externalFarGrid;
        }
        return grid;
    }
    
    /*
     * Get By component for a point relative to the magnet's origin.
     * This component is the same in 2 quadrants, but must be reflected about the y axis for 2 quadrants.
     * See class javadoc.
     */
    private double getBy( final double x, final double y ) {
        Grid grid = chooseGrid( x, y );
        double by = interpolate( Math.abs( x ), Math.abs( y ), grid.getMaxX(), grid.getMaxY(), grid.getByArray(), grid.getGridSpacing() );
        if ( ( x > 0 && y < 0 ) || ( x < 0 && y > 0 ) ) {
            by *= -1; // reflect about the y axis
        }
        return by;
    }
    
    /*
     * Locates the 4 grid points that form a rectangle enclosing the specified point.
     * Then performs a linear interpolation of the B-field at those 4 points.
     */
    private double interpolate( final double x, final double y, double maxX, double maxY, double[][] componentValues, double gridSpacing ) {
        if ( !( x >= 0 && y >= 0 ) ) {
            throw new IllegalArgumentException( "x and y must be positive" ); // ...because our grid is for that quadrant
        }
        
        double value = 0; // B-field outside the grid is zero
        if ( x >=0 && x <= maxX && y >= 0 && y <= maxY ) {
            
            // compute array indicies
            int columnIndex = (int) ( x / gridSpacing );
            int rowIndex = (int) ( y / gridSpacing );
            
            /* 
             * If we're at one of the index maximums, then we're exactly on the outer edge of the grid.
             * Back up by 1 so that we'll have a bounding rectangle.
             */
            if ( columnIndex == componentValues.length - 1 ) {
                columnIndex -= 1;
            }
            if ( rowIndex == componentValues[0].length - 1 ) {
                rowIndex -= 1;
            }
            
            // xy coordinates that define the enclosing rectangle
            double x0 = columnIndex * gridSpacing;
            double x1 = x0 + gridSpacing;
            double y0 = rowIndex * gridSpacing;
            double y1 = y0 + gridSpacing;
            
            // values at the 4 corners of the enclosing rectangle
            double f00 = componentValues[columnIndex][rowIndex];
            double f10 = componentValues[columnIndex+1][rowIndex];
            double f01 = componentValues[columnIndex][rowIndex+1];
            double f11 = componentValues[columnIndex+1][rowIndex+1];
            
            // interpolate
            value = ( f00 * ( ( x1 - x ) / ( x1 - x0 ) ) * ( ( y1 - y ) / ( y1 - y0 ) ) ) +
                    ( f10 * ( ( x - x0 ) / ( x1 - x0 ) ) * ( ( y1 - y ) / ( y1 - y0 ) ) ) +
                    ( f01 * ( ( x1 - x) / ( x1 - x0 ) ) * ( ( y - y0 ) / ( y1 - y0 ) ) ) +
                    ( f11 * ( ( x - x0 ) / ( x1 - x0 ) ) * ( ( y - y0 ) / ( y1 - y0 ) ) );
        }
        return value;
    }
    
    /*
     * Reads the B-field grid files.
     * One file for each 2D vector component (Bx, By).
     * Files are in CSV (Comma Separated Value) format, with values in column-major order.
     * (This means that the x coordinate changes more slowly than the y coordinate.)
     */
    private static class Grid {
        
        private static final String TOKEN_DELIMITER = ","; // CSV format
        
        private final double[][] bxArray, byArray;  // double[columns][rows]
        private final Dimension gridSize;
        private final double gridSpacing;
        
        public Grid( String bxResourceName, String byResourceName, Dimension gridSize, double gridSpacing ) {
            this.gridSize = new Dimension( gridSize.width, gridSize.height );
            this.gridSpacing = gridSpacing;
            bxArray = readGridComponent( bxResourceName, gridSize );
            byArray = readGridComponent( byResourceName, gridSize );
        }
        
        public Dimension getGridSize() {
            return gridSize;
        }

        public double getGridSpacing() {
            return gridSpacing;
        }
        
        public double[][] getBxArray() {
            return bxArray;
        }
        
        public double[][] getByArray() {
            return byArray;
        }
        
        public boolean contains( double x, double y ) {
            return x >=0 && x <= getMaxX() && y >= 0 && y <= getMaxY();
        }
        
        private double getMaxX() {
            return gridSpacing * ( gridSize.width - 1 );
        }
        
        private double getMaxY() {
            return gridSpacing * ( gridSize.height - 1 );
        }
        
        /*
         * Reads one B-field component from the specified resource file.
         */
        private double[][] readGridComponent( String resourceName, Dimension size ) {
            double array[][] = new double[size.width][size.height];
            int count = 0;
            try {
                InputStream is = FaradayResources.getResourceLoader().getResourceAsStream( resourceName );
                BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
                String line = br.readLine();
                int row = 0;
                int column = 0;
                while ( line != null ) {
                    StringTokenizer stringTokenizer = new StringTokenizer( line, TOKEN_DELIMITER );
                    while ( stringTokenizer.hasMoreTokens() ) {
                        String token = stringTokenizer.nextToken();
                        array[column][row] = Double.parseDouble( token ); // column-major order
                        count++;
                        row++;
                        if ( row == size.height ) {
                            row = 0;
                            column++;
                        }
                    }
                    line = br.readLine();
                }
            }
            catch ( IOException e ) {
                e.printStackTrace();
                // If we can't read the grid files, this sim can't function, so bail.
                PhetOptionPane.showErrorDialog( PhetApplication.getInstance().getPhetFrame(), FaradayStrings.MESSAGE_CANNOT_FIND_BAR_MAGNET_FILES );
                System.exit( 1 );
            }
            int expectedCount = size.width * size.height;
            if ( count < expectedCount ) {
                throw new IllegalStateException( "fewer values than expected in " + resourceName + " count=" + count + "expectedCount=" + expectedCount );
            }
            return array;
        }
    }
}
