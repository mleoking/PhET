// Copyright 2002-2011, University of Colorado

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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.HAResources;
import edu.colorado.phet.hydrogenatom.hacks.MetastableHandler.MetastableListener;
import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom;
import edu.colorado.phet.hydrogenatom.model.SchrodingerModel;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;
import edu.colorado.phet.hydrogenatom.view.particle.ProtonNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * SchrodingerNode is the visual representation of the Schrodinger model of the hydrogen atom.
 * <p>
 * The axes are orientated with x horizontal, z vertical, y depth.
 * <p>
 * Probability density is computed in 3D. The atom's 3D space is treated as
 * a cube containing NxNxN discrete cells. The probability density is computed
 * at the center of each cell.
 * <p>
 * The NxNxN 3D cube is projected onto an NxN 2D grid that covers the animation box.
 * Depth information is mapped to color brightness. The sum of probability densities
 * for the depth dimension (y axis) are normalized to a brightness value that has
 * a range 0...1 inclusive.  Each cell in the NxN grid has a brightness value that
 * is used to generate the cell's color.
 * <p>
 * Computing the probability density for an NxNxN cube is fairly expensive,
 * so the resulting NxN array of brightness values is cache for reuse.
 * The cache can optionally be fully populated when it is created (quite 
 * time consuming) or populated on demand.
 * <p>
 * NOTE: This implementation assumes a 1:1 transform between model and view 
 * coordinates. If this were to change in the future, this class would need
 * to map from view to model coordinates before computing probability density.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SchrodingerNode extends AbstractHydrogenAtomNode implements Observer, MetastableListener {
    
    //----------------------------------------------------------------------------
    // Debug
    //----------------------------------------------------------------------------
    
    // Strokes the bounds of the grid
    private static final boolean DEBUG_GRID_BOUNDS = false;
    
    // Prints debugging output related to the brightness cache
    private static final boolean DEBUG_CACHE = false;
    
    //----------------------------------------------------------------------------
    // Private class data
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
    
    // colors used to represent probability density -- MUST BE OPAQUE!
    private static final Color MAX_COLOR = ElectronNode.getColor();
    private static final Color MIN_COLOR = Color.BLACK;
        
    // margin between axes and animation box
    private static final double AXES_MARGIN = 20;
    private static final String HORIZONTAL_AXIS_LABEL = "x";
    private static final String VERTICAL_AXIS_LABEL = "z";
    
    // margin between the state display and animation box
    private static final double STATE_MARGIN = 15;
    
    // Cache of brightness values for all possible states
    private static BrightnessCache BRIGHTNESS_CACHE;
    
    // Should the brightness cache be fully populated the first time we visit Schrodinger?
    // DANGER! Fully populating the cache can take ~15 seconds!
    private static final boolean POPULATE_CACHE = false;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // the atom that this node is representing
    private SchrodingerModel _atom;
    // electron state display
    private StateDisplayNode _stateDisplayNode;
    // field node
    private AtomNode _fieldNode;
    // proton node
    private ProtonNode _protonNode;
    private TextButtonNode _exciteButton;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param atom
     */
    public SchrodingerNode( SchrodingerModel atom ) {
        super();
        setChildrenPickable( true ); // for Excite button
        
        // many assumptions herein that the smallest value of n is 1
        assert( SchrodingerModel.getGroundState() == 1 );
        
        if ( BRIGHTNESS_CACHE == null ) {
            BRIGHTNESS_CACHE = new BrightnessCache( POPULATE_CACHE );
        }
        
        _atom = atom;
        _atom.addObserver( this );
        _atom.addMetastableListener( this );
        
        // Atom representation
        _fieldNode = new AtomNode();
        
        // Proton
        _protonNode = new ProtonNode();
        _protonNode.setOffset( BOX_WIDTH / 2, BOX_HEIGHT / 2 );

        // Axes, positioned at lower left
        Axes2DNode axesNode = new Axes2DNode( HORIZONTAL_AXIS_LABEL, VERTICAL_AXIS_LABEL );
        double xOffset = AXES_MARGIN;
        double yOffset = BOX_HEIGHT - axesNode.getHeight() - AXES_MARGIN;
        axesNode.setOffset( xOffset, yOffset );

        // Electron state display, positioned at lower right
        _stateDisplayNode = new StateDisplayNode();
        _stateDisplayNode.setVisible( HAConstants.SHOW_STATE_DISPLAY );
        _stateDisplayNode.setState( 6, 5, -5 ); // widest value
        xOffset = BOX_WIDTH - _stateDisplayNode.getFullBounds().getWidth() - STATE_MARGIN;
        yOffset = BOX_HEIGHT - _stateDisplayNode.getFullBounds().getHeight() - STATE_MARGIN;
        _stateDisplayNode.setOffset( xOffset, yOffset );

        // Excite button, above state display.
        _exciteButton = new TextButtonNode( HAResources.getString( "button.excite" ), new PhetFont( 18 ), Color.YELLOW );
        _exciteButton.setVisible( false );
        xOffset =  _stateDisplayNode.getFullBoundsReference().getMaxX() - _exciteButton.getFullBoundsReference().getWidth() - 5;
        yOffset =  _stateDisplayNode.getFullBoundsReference().getMinY() - _exciteButton.getFullBoundsReference().getHeight() - 10;
        _exciteButton.setOffset( xOffset, yOffset );
        _exciteButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                _atom.fireOneAbsorbablePhoton();
            }
        } );
        
        // Layering
        addChild( _fieldNode );
        addChild( _protonNode );
        addChild( axesNode );
        addChild( _exciteButton );
        if ( _stateDisplayNode != null ) {
            addChild( _stateDisplayNode );
        }
        
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
    // MetastableListener implementation
    //----------------------------------------------------------------------------

    // #2803, If the gun is firing monochromatic light when we enter the metastable state, make the Excite button visible.
    public void enteredMetastableState() {
        if ( _atom.isMonochromaticLightType() ) {
            _exciteButton.setVisible( true );
        }
    }

    public void exitedMetastableState() {
        _exciteButton.setVisible( false );
    }
    
    //----------------------------------------------------------------------------
    // Property change handlers
    //----------------------------------------------------------------------------
    
    /*
     * Handles a change in the state of the atom's electron.
     * Computes the probability density of each cell in the grid,
     * and maps the third dimension (y, depth) to color brightness.  
     */
    public void handleStateChange() {
        
        int n = _atom.getElectronState();
        int l = _atom.getSecondaryElectronState();
        int m = Math.abs( _atom.getTertiaryElectronState() );
        
        // Update the state display
        if ( _stateDisplayNode != null ) {
            _stateDisplayNode.setState( n, l, m );
        }
        
        // Look up the set of brightness values for the atom's state
        float[][] brightness = BRIGHTNESS_CACHE.getBrightness( n, l, m );
        
        // Update the atom.
        _fieldNode.setBrightness( brightness );
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
        
        private static final Font LABEL_FONT = new PhetFont( Font.PLAIN, 14 );
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
     * GridNode draws the grid that covers one quadrant of the 2D animation box.
     * The grid is composed of rectangular cells, and each cell has its own color.
     */
    private static class GridNode extends PNode {
        
        private static final double CELL_OVERLAP = 0.1; // 1.0 = 100%
        
        private float[][] _brightness; // brightness values, [row][column]
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
        public void setBrightness( float[][] brightness ) {
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
        // reusable buffered image
        private BufferedImage _bufferedImage;

        /**
         * Constructor.
         */
        public AtomNode() {
            super();
            setPickable( false );
            setChildrenPickable( false );
            
            _gridNode = new GridNode( BOX_WIDTH / 2, BOX_HEIGHT / 2 );
            
            _bufferedImage = new BufferedImage( (int)_gridNode.getWidth(), (int)_gridNode.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE );

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
        public void setBrightness( float[][] brightness ) {
            _gridNode.setBrightness( brightness );
            Image image = _gridNode.toImage( _bufferedImage, null );
            _upperLeftNode.setImage( image );
            _upperRightNode.setImage( image );
            _lowerLeftNode.setImage( image );
            _lowerRightNode.setImage( image );
        }
    }
    
    /*
     * BrightnessCache is a cache containing brightness information for states.
     * The cache is set up for a fixed number of states, and a fixed size grid.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    private static final class BrightnessCache {
        
        private float[][][][][] _cache; // [n][l][m][z][x]
        float[][] _sums; // reusable array
        
        public BrightnessCache( boolean populate ) {

            _sums = new float[NUMBER_OF_VERTICAL_CELLS][NUMBER_OF_HORIZONTAL_CELLS];
            
            int statesCount = 0;
            int nSize = SchrodingerModel.getNumberOfStates();
            _cache = new float[nSize][][][][];
            for ( int n = 1; n <= nSize; n++ ) {
                int lSize = n;
                _cache[n-1] = new float[lSize][][][];
                for ( int l = 0; l < lSize; l++ ) {
                    int mSize = l + 1;
                    _cache[n-1][l] = new float[mSize][][];
                    for ( int m = 0; m < mSize; m++ ) {
                        statesCount++;
                        if ( populate ) {
                            getBrightness( n, l, m );
                        }
                    }
                }
            }
            if ( DEBUG_CACHE ) {
                System.out.println( "BrightnessCache has room for " + statesCount + " states" );
            }
        }
        
        /**
         * Gets a cache entry. 
         * If there is no entry, the entry is created.
         * 
         * @param n
         * @param l
         * @param m
         * @return
         */
        public float[][] getBrightness( int n, int l, int m ) {
            float[][] brightness = _cache[n-1][l][m];
            if ( brightness == null ) {
                if ( DEBUG_CACHE ) {
                    System.out.println( "BrightnessCache adding entry for " + SchrodingerModel.stateToString( n, l, m ) );
                }
                brightness = computeBrightness( n, l, m, _sums );
                _cache[n-1][l][m] = brightness;
            }
            return brightness;
        }
        
        /*
         * Computes the brightness values for a specific state.
         * @param n
         * @param l
         * @param m
         * @param sums resuable array of probability density sums
         * @return
         */
        private static final float[][] computeBrightness( int n, int l, int m, float[][] sums ) {
            
            float[][] brightness = new float[NUMBER_OF_VERTICAL_CELLS][NUMBER_OF_HORIZONTAL_CELLS];
            
            float maxSum = 0;
            
            for ( int row = 0; row < NUMBER_OF_VERTICAL_CELLS; row++ ) {
                double z = ( row * CELL_HEIGHT ) + ( CELL_HEIGHT / 2 );
                assert ( z > 0 );
                for ( int column = 0; column < NUMBER_OF_HORIZONTAL_CELLS; column++ ) {
                    double x = ( column * CELL_WIDTH ) + ( CELL_WIDTH / 2 );
                    assert ( x > 0 );
                    float sum = 0;
                    for ( int depth = 0; depth < NUMBER_OF_DEPTH_CELLS; depth++ ) {
                        double y = ( depth * CELL_DEPTH ) + ( CELL_DEPTH / 2 );
                        assert ( y > 0 );
                        double pd = SchrodingerModel.getProbabilityDensity( n, l, m, x, y, z );
                        sum += pd;
                    }
                    sums[row][column] = sum;
                    if ( sum > maxSum ) {
                        maxSum = sum;
                    }
                }
            }

            for ( int row = 0; row < NUMBER_OF_VERTICAL_CELLS; row++ ) {
                for ( int column = 0; column < NUMBER_OF_HORIZONTAL_CELLS; column++ ) {
                    float b = 0;
                    if ( maxSum > 0 ) {
                        b = sums[row][column] / maxSum;
                    }
                    brightness[row][column] = b;
                }
            }
            
            return brightness;
        }
    }
    
    //----------------------------------------------------------------------------
    // Unit test harness
    //----------------------------------------------------------------------------
    
    public static final void main( String args[] ) {
        
        System.out.println( "SchrodingerNode unit test begins..." );
        
        // Populate a brightness cache for all possible (n,l,m) states
        BrightnessCache cache = new BrightnessCache( false );
        int statesCount = 0;
        for ( int n = 1; n <= SchrodingerModel.getNumberOfStates(); n++ ) {
            for ( int l = 0; l <= n - 1; l++ ) {
                for ( int m = 0; m <= l; m++ ) {
                    statesCount++;
                    cache.getBrightness( n, l, m );
                    System.out.println( "populated brightness cache for " + SchrodingerModel.stateToString( n, l, m ) );
                }
            }
        }
        System.out.println( "There are "+ statesCount + " (n,l,m) states" );
        
        System.out.println( "SchrodingerNode unit test ends." );
    }
}
