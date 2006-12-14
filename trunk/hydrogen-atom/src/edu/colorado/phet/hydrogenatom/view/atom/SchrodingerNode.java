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

import java.awt.*;
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
import edu.colorado.phet.hydrogenatom.view.particle.ProtonNode;
import edu.colorado.phet.piccolo.nodes.ArrowNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * SchrodingerNode is the visual representation of the Schrodinger model of the hydrogen atom.
 * <p>
 * Axes orientation: x horizontal, z vertical, y depth
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
    private static final String HORIZONTAL_AXIS_LABEL = "x";
    private static final String VERTICAL_AXIS_LABEL = "z";
    
    // margin between the state display and animation box
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
    // electron state display
    private StateNode _stateNode;
    // atom node
    private AtomNode _atomNode;
    
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
        
        // Atom representation
        _atomNode = new AtomNode();
        
        // Proton
        ProtonNode protonNode = new ProtonNode();
        protonNode.setOffset( BOX_WIDTH / 2, BOX_HEIGHT / 2 );
        
        // Axes, positioned at lower left
        Axes2DNode axesNode = new Axes2DNode( HORIZONTAL_AXIS_LABEL, VERTICAL_AXIS_LABEL );
        double xOffset = AXES_MARGIN;
        double yOffset = BOX_HEIGHT - axesNode.getHeight() - AXES_MARGIN;
        axesNode.setOffset( xOffset, yOffset );

        // Electron state display, positioned at upper right
        if ( DEBUG_SHOW_STATE ) {
            _stateNode = new StateNode();
            _stateNode.setOffset( STATE_MARGIN, STATE_MARGIN );
        }
        
        // Layering
        addChild( _atomNode );
        addChild( protonNode );
        addChild( axesNode );
        if ( _stateNode != null ) {
            addChild( _stateNode );
        }
        
        // Storage allocation
        _probabilityDensitySums = new double[ NUMBER_OF_VERTICAL_CELLS ][ NUMBER_OF_HORIZONTAL_CELLS ];
        _brightness = new double[ NUMBER_OF_VERTICAL_CELLS ][ NUMBER_OF_HORIZONTAL_CELLS ];
        
        /* 
         * This view ignores the atom's position, and centers 
         * the atom (and the grid) in the animation box.
         */
        setOffset( 0, 0 );
        
        update( _atom, AbstractHydrogenAtom.PROPERTY_ELECTRON_STATE );
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
    // Rendering
    //----------------------------------------------------------------------------
    
    /*
     * Handles a change in the state of the atom's electron.
     * Computes the probability density of each cell in the grid,
     * and maps the third dimension (y, depth) to color brightness.  
     */
    public void handleStateChange() {
        
        // Update the state display
        if ( _stateNode != null ) {
            _stateNode.update( _atom );
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
                double x = ( column * CELL_WIDTH ) + ( CELL_WIDTH / 2 );
                assert( x > 0 );
                double sum = 0;
                for ( int depth = 0; depth < NUMBER_OF_DEPTH_CELLS; depth++ ) {
                    double y = ( depth * CELL_DEPTH ) + ( CELL_DEPTH / 2 );
                    assert( y > 0 );
                    double pd = _atom.getProbabilityDensity( x, y, z );
                    sum += pd;
                }
                _probabilityDensitySums[ row ][ column ] = sum;
                if ( sum > maxSum ) {
                    maxSum = sum;
                }
            }
        }
        
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
        
        // Update the atom.
        _atomNode.setBrightness( _brightness );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
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
            setPickable( false );
            setChildrenPickable( false );
            
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
    
    /*
     * StateNode displays the (n,l,m) state of an atom.
     */
    private static class StateNode extends PText {
        
        private static final Font STATE_FONT = new Font( HAConstants.DEFAULT_FONT_NAME, Font.BOLD, 16 );
        private static final Color STATE_COLOR = Color.WHITE;
        private static final String STATE_FORMAT = "(n,l,m)=({0},{1},{2})";
        
        /**
         * Constructor.
         * @param atom
         */
        public StateNode() {
            super();
            setPickable( false );
            setChildrenPickable( false );
            
            setFont( STATE_FONT );
            setTextPaint( STATE_COLOR );
        }
        
        /**
         * Updates the view to match the specified model.
         * @param atom
         */
        public void update( SchrodingerModel atom ) {
            int n = atom.getElectronState();
            int l = atom.getSecondaryElectronState();
            int m = atom.getTertiaryElectronState();
            Object[] args = { new Integer( n ), new Integer( l ), new Integer( m ) };
            String s = MessageFormat.format( STATE_FORMAT, args );
            setText( s );
        }
    }
    
    /*
     * GridNode draws the grid that covers one quadrant of the 2D animation box.
     * The grid is composed of rectangular cells, and each cell has its own color.
     */
    private static class GridNode extends PNode {
        
        private static final double CELL_OVERLAP = 0.1; // 1.0 = 100%
        
        private double[][] _brightness; // brightness values, [row][column]
        private double _cellWidth, _cellHeight;
        private Rectangle2D _rectangle; // reusable rectangle for drawing cells
        
        /**
         * Constructor.
         * @param width
         * @param height
         */
        public GridNode( double width, double height ) {
            super();
            setPickable( false );
            setChildrenPickable( false );
            setBounds( 0, 0, width, height );
            _rectangle = new Rectangle2D.Double();
        }
        
        /**
         * Sets the brightness values that are applied to the cells in the grid.
         * The dimensions of the brightness array determine the number of cells.
         * 
         * @param brightness
         */
        public void setBrightness( double[][] brightness ) {
            _brightness = brightness;
            _cellHeight = getHeight() / brightness.length;
            _cellWidth = getWidth() / brightness[0].length;
            repaint();
        }
        
        /*
         * Draws the grid, which is composed of rectangular cells.
         * Each cell is assigned a color based on its brightness value.
         * Cells overlap a bit so that we don't see the seams between them.
         */
        protected void paint( PPaintContext paintContext ) {

            if ( _brightness == null ) {
                return;
            }

            Graphics2D g2 = paintContext.getGraphics();
            
            // Save graphics state
            Color saveColor = g2.getColor();
            
            if ( DEBUG_GRID_BOUNDS ) {
                Stroke saveStroke = g2.getStroke();
                g2.setStroke( new BasicStroke( 2f ) );
                g2.setColor( Color.GREEN );
                _rectangle.setRect( getX(), getY(), getWidth(), getHeight() );
                g2.draw( _rectangle );
                g2.setStroke( saveStroke );
            }

            Color color;
            double x, z;
            final double w = _cellWidth + ( CELL_OVERLAP * _cellWidth );
            final double h = _cellHeight + ( CELL_OVERLAP * _cellHeight );

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
    
    /* 
     * AtomNode draws the 2D representation of the atom.
     * Since the wavefunction is symmetric about the orgin, each of
     * the box's quadrants is drawn using an identical image,
     * reflected about one or both axes.
     * <p>
     * Note: It is important to use a PImage for every quadrant,
     * or the images will not line up properly.
     */
    private static class AtomNode extends PNode {
        
        // overlap between quadrants to remove "seams" between PImage nodes
        private static final double QUADRANT_OVERLAP = 0.5; // pixels
        
        // Node used to create the Image that is copies to each quadrant
        private GridNode _gridNode;
        // the 4 quadrants of the box
        private PImage _upperLeftNode, _upperRightNode, _lowerLeftNode, _lowerRightNode;

        /**
         * Constructor.
         */
        public AtomNode() {
            super();
            setPickable( false );
            setChildrenPickable( false );
            
            _gridNode = new GridNode( BOX_WIDTH / 2, BOX_HEIGHT / 2 );

            _upperLeftNode = new PImage();
            AffineTransform upperLeftTransform = new AffineTransform();
            upperLeftTransform.translate( ( BOX_WIDTH / 2 ) + QUADRANT_OVERLAP, ( BOX_HEIGHT / 2 ) + QUADRANT_OVERLAP );
            upperLeftTransform.scale( -1, -1 ); // reflection about both axis
            _upperLeftNode.setTransform( upperLeftTransform );
            addChild( _upperLeftNode );

            _upperRightNode = new PImage();
            AffineTransform upperRightTransform = new AffineTransform();
            upperRightTransform.translate( ( BOX_WIDTH / 2 ) - QUADRANT_OVERLAP, ( BOX_HEIGHT / 2 ) + QUADRANT_OVERLAP );
            upperRightTransform.scale( 1, -1 ); // reflection about the horizontal axis
            _upperRightNode.setTransform( upperRightTransform );
            addChild( _upperRightNode );

            _lowerRightNode = new PImage();
            AffineTransform lowerRightTransform = new AffineTransform();
            lowerRightTransform.translate( ( BOX_WIDTH / 2 ) - QUADRANT_OVERLAP, ( BOX_HEIGHT / 2 ) - QUADRANT_OVERLAP );
            lowerRightTransform.scale( 1, 1 ); // no reflection
            _lowerRightNode.setTransform( lowerRightTransform );
            addChild( _lowerRightNode );

            _lowerLeftNode = new PImage();
            AffineTransform lowerLeftTransform = new AffineTransform();
            lowerLeftTransform.translate( ( BOX_WIDTH / 2 ) + QUADRANT_OVERLAP, ( BOX_HEIGHT / 2 ) - QUADRANT_OVERLAP);
            lowerLeftTransform.scale( -1, 1 ); // reflection about the vertical axis
            _lowerLeftNode.setTransform( lowerLeftTransform );
            addChild( _lowerLeftNode );
        }
        
        /**
         * Sets the brightness values that are applied to the cells in the grid.
         * The dimensions of the brightness array determine the number of cells.
         * 
         * @param brightness
         */
        public void setBrightness( double[][] brightness ) {
            _gridNode.setBrightness( brightness );
            Image image = _gridNode.toImage();
            _upperLeftNode.setImage( image );
            _upperRightNode.setImage( image );
            _lowerLeftNode.setImage( image );
            _lowerRightNode.setImage( image );
        }
    }
}
