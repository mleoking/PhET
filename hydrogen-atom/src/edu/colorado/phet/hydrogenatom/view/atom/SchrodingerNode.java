/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view.atom;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom;
import edu.colorado.phet.hydrogenatom.model.SchrodingerModel;
import edu.colorado.phet.hydrogenatom.util.ColorUtils;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;
import edu.colorado.phet.piccolo.nodes.ArrowNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * SchrodingerNode is the visual representation of the Schrodinger model of the hydrogen atom.
 * <p>
 * Coordinate system orientation: x horizontal, z vertical, y depth
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SchrodingerNode extends AbstractHydrogenAtomNode implements Observer {
    
    //----------------------------------------------------------------------------
    // Debug
    //----------------------------------------------------------------------------
    
    // Strokes the bounds of the grid
    private static final boolean DEBUG_GRID_BOUNDS = false;
    
    // Shows the (n,l,m) state in the upper left corner
    private static final boolean DEBUG_SHOW_STATE = true;
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Animation box dimensions, for convenience
    private static final double BOX_WIDTH = HAConstants.ANIMATION_BOX_SIZE.getWidth();
    private static final double BOX_HEIGHT = HAConstants.ANIMATION_BOX_SIZE.getHeight();
        
    // Resolution of the grid, which covers 1/8 of the 3D space
    private static final int NUMBER_OF_HORIZONTAL_CELLS = 40;
    private static final int NUMBER_OF_VERTICAL_CELLS = NUMBER_OF_HORIZONTAL_CELLS;
    private static final int NUMBER_OF_DEPTH_CELLS = NUMBER_OF_HORIZONTAL_CELLS;
    
    // 3D cell size
    private final static double CELL_WIDTH = ( HAConstants.ANIMATION_BOX_SIZE.getWidth() / NUMBER_OF_HORIZONTAL_CELLS ) / 2.0;
    private final static double CELL_HEIGHT = ( HAConstants.ANIMATION_BOX_SIZE.getHeight() / NUMBER_OF_VERTICAL_CELLS ) / 2.0;
    private final static double CELL_DEPTH = ( HAConstants.ANIMATION_BOX_SIZE.getHeight() / NUMBER_OF_DEPTH_CELLS ) / 2.0;
    
    // colors used to represent probability density
    private static final Color MAX_COLOR = ElectronNode.getColor();
    private static final Color MIN_COLOR = Color.BLACK;
        
    // margin between axes and animation box
    private static final double AXES_MARGIN = 20;
    
    private static final Font STATE_FONT = new Font( HAConstants.DEFAULT_FONT_NAME, Font.BOLD, 16 );
    private static final Color STATE_COLOR = Color.WHITE;
    private static final String STATE_FORMAT = "(n,l,m)=({0},{1},{2})";
    private static final double STATE_MARGIN = 15;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // the atom that this node is representing
    private SchrodingerModel _atom;
    // sum of the probability densities for each cell, [row,column]
    private double[][] _probabilityDensitySums;
    // normalized color brightness for each cell, [row][column]
    private double[][] _brightness;
    // the quadrant where all (x,z) values are positive
    private GridNode _lowerRightNode;
    // the other 3 quadrants, where x and/or z is negative
    private PImage _upperLeftNode, _upperRightNode, _lowerLeftNode;
    // electron state display
    private PText _stateNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param atom
     */
    public SchrodingerNode( SchrodingerModel atom ) {
        super();
        
        _atom = atom;
        _atom.addObserver( this );
        
        // axes, positioned at lower left
        Axes2DNode axesNode = new Axes2DNode( "x", "z" );
        axesNode.setPickable( false );
        axesNode.setChildrenPickable( false );
        double xOffset = AXES_MARGIN;
        double yOffset = BOX_HEIGHT - axesNode.getHeight() - AXES_MARGIN;
        axesNode.setOffset( xOffset, yOffset );

        // Electron state display
        if ( DEBUG_SHOW_STATE ) {
            _stateNode = new PText();
            _stateNode.setFont( STATE_FONT );
            _stateNode.setTextPaint( STATE_COLOR );
            _stateNode.setOffset( STATE_MARGIN, STATE_MARGIN );
        }

        /* 
         * Create the nodes that draw the atom.
         * The lower left quadrant will be draw using Shapes,
         * then converted to an image and copied to the other 3 quadrants.
         * Since the wavefunction is symmetric about the orgin, each of
         * the other 3 quadrants is a reflection about one or both axes.
         */
        PNode atomNode = new PNode();
        {
            // lower-right quadrant with +x +z values
            _lowerRightNode = new GridNode( BOX_WIDTH / 2, BOX_HEIGHT / 2 );
            _lowerRightNode.setOffset( BOX_WIDTH / 2, BOX_HEIGHT / 2 );
            atomNode.addChild( _lowerRightNode );
            
            // the other 3 quadrants...
            
            _upperRightNode = new PImage();
            AffineTransform upperRightTransform = new AffineTransform();
            upperRightTransform.translate( ( BOX_WIDTH / 2 ), ( BOX_HEIGHT / 2 ) );
            upperRightTransform.scale( 1, -1 ); // reflection about the horizontal axis
            _upperRightNode.setTransform( upperRightTransform );
            atomNode.addChild( _upperRightNode );
            
            _upperLeftNode = new PImage();
            AffineTransform upperLeftTransform = new AffineTransform();
            upperLeftTransform.translate( ( BOX_WIDTH / 2 ), ( BOX_HEIGHT / 2 ) );
            upperLeftTransform.scale( -1, -1 ); // reflection about both axis
            _upperLeftNode.setTransform( upperLeftTransform );
            atomNode.addChild( _upperLeftNode );
            
            _lowerLeftNode = new PImage();
            AffineTransform lowerLeftTransform = new AffineTransform();
            lowerLeftTransform.translate( ( BOX_WIDTH / 2 ), ( BOX_HEIGHT / 2 ) );
            lowerLeftTransform.scale( -1, 1 ); // reflection about the vertical axis
            _lowerLeftNode.setTransform( lowerLeftTransform );
            atomNode.addChild( _lowerLeftNode );
        }
        
        // Layering
        addChild( atomNode );
        addChild( axesNode );
        if ( _stateNode != null ) {
            addChild( _stateNode );
        }
        
        // Storage allocation
        _probabilityDensitySums = new double[ NUMBER_OF_VERTICAL_CELLS ][ NUMBER_OF_HORIZONTAL_CELLS ];
        _brightness = new double[ NUMBER_OF_VERTICAL_CELLS ][ NUMBER_OF_HORIZONTAL_CELLS ];
        
        /* 
         * This view ignores the atom's position
         * and centers atom (and the grid) in the animation box.
         */
        setOffset( 0, 0 );
        
        update( _atom, AbstractHydrogenAtom.PROPERTY_ELECTRON_STATE );
    }
    
    //----------------------------------------------------------------------------
    // Rendering
    //----------------------------------------------------------------------------
    
    /*
     * Handles a change in the state of the atom's electron.
     * Computes the probability density of each cell in the grid,
     * and maps the third dimension (y, depth) to color brightness.  
     */
    public void handleStateChange() {
        
//        System.out.println( "SchrodingerNode.handleStateChange" );//XXX
        
        // Update the state display
        if ( _stateNode != null ) {
            int n = _atom.getElectronState();
            int l = _atom.getSecondaryElectronState();
            int m = _atom.getTertiaryElectronState();
            Object[] args = { new Integer( n ), new Integer( l ), new Integer( m ) };
            String s = MessageFormat.format( STATE_FORMAT, args );
            _stateNode.setText( s );
        }
        
        /* 
         * The wave function is symmetric about the origin.
         * So compute the probability density for 1/8 of the grid (positive values of x,y,z).
         * Use the 3D position that is at the center of each cell.
         * Sum the wave function values over y (depth).
         */
        double maxSum = 0;
        for ( int row = 0; row < NUMBER_OF_VERTICAL_CELLS; row++ ) {
            double z = ( row * CELL_HEIGHT ) + ( CELL_HEIGHT / 2 );
            assert( z > 0 );
            for ( int column = 0; column < NUMBER_OF_HORIZONTAL_CELLS; column++ ) {
                double x = ( row * CELL_WIDTH ) + ( CELL_WIDTH / 2 );
                assert( x > 0 );
                double sum = 0;
                for ( int depth = 0; depth < NUMBER_OF_DEPTH_CELLS; depth++ ) {
                    double y = ( depth * CELL_DEPTH ) + ( CELL_DEPTH / 2 );
                    assert( y > 0 );
                    double pd = _atom.getProbabilityDensity( x, y, z );
//                    System.out.println( "w(" + x + "," + y + "," + z + ")=" + w );//XXX
                    sum += pd;
                }
                _probabilityDensitySums[ row ][ column ] = sum;
                if ( sum > maxSum ) {
                    maxSum = sum;
                }
            }
        }
//        System.out.println( "SchrodingerNode.maxSum=" + maxSum );//XXX
        
        /*
         * Normalize the probability density sum for each cell. 
         * Map the normalized value to a color brightness.
         */ 
        for ( int row = 0; row < NUMBER_OF_VERTICAL_CELLS; row++ ) {
            for ( int column = 0; column < NUMBER_OF_HORIZONTAL_CELLS; column++ ) {
                double brightness = 0;
                if ( maxSum > 0 ) {
                    brightness = _probabilityDensitySums[ row ][ column ] / maxSum;
                }
                _brightness[ row ][column] = brightness;
            }
        }
        
        /*
         * Update the nodes that draw the atom.
         */
        {
            _lowerRightNode.setBrightness( _brightness );
            
            //XXX other 3 quadrants
            Image image = _lowerRightNode.toImage();
            _upperLeftNode.setImage( image );
            _upperRightNode.setImage( image );
            _lowerLeftNode.setImage( image );
        }
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the view to match the model.
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        if ( o == _atom ) {
            if ( arg == AbstractHydrogenAtom.PROPERTY_ELECTRON_STATE ) {
                handleStateChange();
            }
            else if ( arg == AbstractHydrogenAtom.PROPERTY_ATOM_IONIZED ) {
                //XXX
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Axes node
    //----------------------------------------------------------------------------
    
    /**
     * Axes2DNode draws a pair of 2D axes.
     */
    private static class Axes2DNode extends PNode {
        
        private static final double AXIS_LENGTH = 100;
        private static final Color AXIS_COLOR = Color.WHITE;
        
        private static final double ARROW_HEAD_HEIGHT = 10;
        private static final double ARROW_HEAD_WIDTH = 10;
        private static final double ARROW_TAIL_WIDTH = 1;
        
        private static final Font LABEL_FONT = new Font( HAConstants.DEFAULT_FONT_NAME, Font.PLAIN, 14 );
        private static final Color LABEL_COLOR = Color.WHITE;
        private static final double LABEL_SPACING = 5;
        
        public Axes2DNode( String hLabel, String vLabel ) {
            super();
            
            Point2D origin = new Point2D.Double( 0, 0 );
            
            Point2D hAxisTipPoint = new Point2D.Double( AXIS_LENGTH, 0 );
            ArrowNode hAxis = new ArrowNode( origin, hAxisTipPoint, ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH );
            hAxis.setPaint( AXIS_COLOR );
            hAxis.setStroke( null );
            
            Point2D vAxisTipPoint = new Point2D.Double( 0, -AXIS_LENGTH );
            ArrowNode vAxis = new ArrowNode( origin, vAxisTipPoint, ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH);
            vAxis.setPaint( AXIS_COLOR );
            vAxis.setStroke( null );
            
            PText hText = new PText( hLabel );
            hText.setFont( LABEL_FONT );
            hText.setTextPaint( LABEL_COLOR );
            
            PText vText = new PText( vLabel );
            vText.setFont( LABEL_FONT );
            vText.setTextPaint( LABEL_COLOR );
            
            hText.setOffset( hAxis.getWidth() + LABEL_SPACING, -hText.getHeight() / 2 );
            vText.setOffset( -vText.getWidth() / 2, -( vAxis.getHeight() + LABEL_SPACING + vText.getHeight() ) );
            
            addChild( hAxis );
            addChild( vAxis );
            addChild( hText );
            addChild( vText );
        }
    }
    
    //----------------------------------------------------------------------------
    // Grid node
    //----------------------------------------------------------------------------
    
    /*
     * GridNode draws the grid that covers one quadrant of the 2D animation box.
     */
    private static class GridNode extends PNode {
        
        private double[][] _brightness;
        private double _cellWidth, _cellHeight;
        private Rectangle2D _rectangle; // reusable rectangle
        
        public GridNode( double width, double height ) {
            super();
            setBounds( 0, 0, width, height );
            _rectangle = new Rectangle2D.Double();
        }
        
        public void setBrightness( double[][] brightness ) {
            _brightness = brightness;
            _cellHeight = getHeight() / brightness.length;
            _cellWidth = getWidth() / brightness[0].length;
            repaint();
        }
        
        protected void paint( PPaintContext paintContext ) {

            if ( _brightness == null ) {
                return;
            }

            Graphics2D g2 = paintContext.getGraphics();
            
            // Save graphics state
            Color saveColor = g2.getColor();
            
            if ( DEBUG_GRID_BOUNDS ) {
                g2.setColor( Color.GREEN );
                _rectangle.setRect( getX(), getY(), getWidth(), getHeight() );
                g2.draw( _rectangle );
            }

            Color color;
            double x, z;
            final double w = _cellWidth + ( 0.1 * _cellWidth );
            final double h = _cellHeight + ( 0.1 * _cellHeight );

            for ( int row = 0; row < _brightness.length; row++ ) {
                for ( int column = 0; column < _brightness[row].length; column++ ) {
                    color = ColorUtils.interpolateRBGA( MIN_COLOR, MAX_COLOR, _brightness[row][column] );
                    g2.setColor( color );
                    x = ( column * _cellWidth );
                    z = ( row * _cellHeight ); 
                    _rectangle.setRect( x, z, w, h );
                    g2.fill( _rectangle );
                }
            }
            
            // Restore graphics state
            g2.setColor( saveColor );
        }
    }
}
