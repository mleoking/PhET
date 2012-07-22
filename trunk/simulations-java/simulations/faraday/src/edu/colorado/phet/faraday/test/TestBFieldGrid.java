// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.faraday.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.faraday.FaradayConstants;
import edu.colorado.phet.faraday.FaradayResources;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Unfuddle #2236.
 * Test the B-field grid files that Mike Dubson provided, by reading the files and displaying as vectors.
 * The red box is the outline of the bar magnet.
 * Use PCanvas zoom/pan controls to examine the vectors more closely.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestBFieldGrid extends JFrame {

    private static final Dimension CANVAS_SIZE = new Dimension( 1024, 768 ); // similar to PhET sims
    private static final Dimension MAGNET_SIZE = FaradayConstants.BAR_MAGNET_SIZE; // value used when generating data files in MathCAD
    private static final Dimension GRID_SPACING = new Dimension( 4, 4 ); // value used when generating data files in MathCAD
    private static final Dimension GRID_SIZE = new Dimension( 125, 50 ); // value used when generating data files in MathCAD
    private static final String BX_RESOURCE_NAME = "bfield/Bx.csv"; // B-field vector x components, one quadrant
    private static final String BY_RESOURCE_NAME = "bfield/By.csv"; // B-field vector y components, one quadrant
    private static final double BFIELD_DISPLAY_SCALING_FACTOR = 3.0; // larger values make the B-field appear to drop off less quickly

    public static class Grid {

        private final double[][] bx, by; // column-major order, [column][row]

        public Grid() {
            bx = readGridComponent( BX_RESOURCE_NAME, GRID_SIZE );
            by = readGridComponent( BY_RESOURCE_NAME, GRID_SIZE );
        }

        public double[][] getBx() {
            return bx;
        }

        public double[][] getBy() {
            return by;
        }

        /*
        * Reads one B-field component from the specified resource file.
        * The file is in CSV (Comma Separated Value) format, with values in column-major order.
        * (This means that the x coordinate changes more slowly than the y coordinate.)
        */
        private double[][] readGridComponent( String resourceName, Dimension size ) {
            double array[][] = new double[size.width][size.height]; // [column][row], column-major order
            int count = 0;
            try {
                InputStream is = FaradayResources.getResourceLoader().getResourceAsStream( resourceName );
                BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
                String line = br.readLine();
                int row = 0;
                int column = 0;
                while ( line != null ) {
                    StringTokenizer stringTokenizer = new StringTokenizer( line, "," );
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
                //TODO if this becomes production code, do something sensible here
                e.printStackTrace();
            }
            int expectedCount = size.width * size.height;
            if ( count < expectedCount ) {
                throw new IllegalStateException( "fewer values than expected in " + resourceName + " count=" + count + "expectedCount=" + expectedCount );
            }
            return array;
        }
    }

    /**
     * Visual representation of a 2D grid of 2D vectors.
     */
    private static class GridNode extends PComposite {

        public GridNode( Grid grid ) {
            double[][] bx = grid.getBx();
            double[][] by = grid.getBy();
            int x = 0;
            int y = 0;
            for ( int row = 0; row < GRID_SIZE.width; row++ ) {
                for ( int column = 0; column < GRID_SIZE.height; column++ ) {
                    MutableVector2D bfield = new MutableVector2D( bx[row][column], by[row][column] );
                    PNode vectorNode = new ScaledVector2DNode( bfield );
                    addChild( vectorNode );
                    vectorNode.setOffset( x, y );
                    y += GRID_SPACING.getHeight();
                }
                x += GRID_SPACING.getWidth();
                y = 0;
            }
        }
    }

    /**
     * Visual representation of a 2D vector, scaled for display using Dubson's algorithm.
     * The length, head size and tail width are all proportional to the magnitude.
     */
    public static class ScaledVector2DNode extends PComposite {

        public ScaledVector2DNode( MutableVector2D vector ) {

            MutableVector2D scaledVector = scaleVector( vector );

            Point2D tailLocation = new Point2D.Double( 0, 0 );
            Point2D tipLocation = new Point2D.Double( scaledVector.getX(), scaledVector.getY() );
            double headHeight = 0.25 * scaledVector.magnitude();
            double headWidth = headHeight;
            double tailWidth = headWidth / 2;
            ArrowNode arrowNode = new ArrowNode( tailLocation, tipLocation, headHeight, headWidth, tailWidth );
            arrowNode.setStroke( null );
            arrowNode.setPaint( Color.BLACK );
            addChild( arrowNode );
        }

        /*
        * Dubson's vector scaling algorithm.
        */
        public MutableVector2D scaleVector( MutableVector2D vector ) {
            double multiplier = Math.pow( vector.magnitude(), 1.0 / BFIELD_DISPLAY_SCALING_FACTOR );
            double bxScaled = multiplier * ( vector.getX() / vector.magnitude() );
            double byScaled = multiplier * ( vector.getY() / vector.magnitude() );
            return new MutableVector2D( bxScaled, byScaled );
        }
    }

    /**
     * Visual representation of the magnet.
     * Origin is at the center of the bounding box.
     */
    public static class MagnetNode extends PPath {

        private static final Color FILL_COLOR = new Color( 0, 0, 0, 0 ); // transparent
        private static final Color STROKE_COLOR = new Color( 255, 0, 0, 180 ); // translucent red
        private static final Stroke STROKE = new BasicStroke( 1f );

        public MagnetNode( Dimension size ) {
            setPathTo( new Rectangle2D.Double( 0, 0, size.getWidth(), size.getHeight() ) );
            setPaint( FILL_COLOR );
            setStrokePaint( STROKE_COLOR );
            setStroke( STROKE );
        }
    }

    public static class TestCanvas extends PCanvas {

        public TestCanvas() {
            setPreferredSize( CANVAS_SIZE );

            PComposite parentNode = new PComposite();
            parentNode.scale( 5 ); // so we can see something by default
            getLayer().addChild( parentNode );

            MagnetNode magnetNode = new MagnetNode( MAGNET_SIZE );
            parentNode.addChild( magnetNode );
            magnetNode.setOffset( -magnetNode.getFullBoundsReference().getWidth() / 2, -magnetNode.getFullBoundsReference().getHeight() / 2 );

            GridNode gridNode = new GridNode( new Grid() );
            parentNode.addChild( gridNode );
        }
    }

    public TestBFieldGrid() {
        super( "TestBFieldGrid" );
        setContentPane( new TestCanvas() );
        pack();
    }

    public static void main( String[] args ) {
        JFrame frame = new TestBFieldGrid();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
