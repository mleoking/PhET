// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.energydiagrams;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;
import java.util.Observer;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.HAResources;
import edu.colorado.phet.hydrogenatom.control.CloseButtonNode;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PClip;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * AbstractEnergyDiagram is the base class for all energy diagrams.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractEnergyDiagram extends PhetPNode implements Observer {

    //----------------------------------------------------------------------------
    // Debug
    //----------------------------------------------------------------------------
    
    private static final boolean DEBUG_DRAWING_AREA_BOUNDS = false;
    
    //----------------------------------------------------------------------------
    // Public class data
    //----------------------------------------------------------------------------
    
    // Squiggle
    public static double SQUIGGLE_LIFETIME = 1500; // milliseconds
    
    //----------------------------------------------------------------------------
    // Protected class data
    //----------------------------------------------------------------------------
    
    // State line properties
    protected static final double LINE_LENGTH = 10;
    protected static final Stroke LINE_STROKE = new BasicStroke( 2f );
    protected static final Color LINE_COLOR = Color.BLACK;
    
    // State label properties
    protected static final String LABEL_FORMAT = "n={0}";
    protected static final Font LABEL_FONT = new PhetFont( Font.BOLD, 14 );
    protected static final Color LABEL_COLOR = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Private class data
    //----------------------------------------------------------------------------
    
    // Energy of the ground state
    private static final double E1 = -13.6; // eV
    
    // How much to distory the energy level values. For no distortion, set this to zero.
    private static final double DISTORTION_FACTOR = 0.2; // 1.0 = 100% increase in spacing
    
    // Size of the "window" for the energy diagram
    private static final Dimension WINDOW_SIZE = new Dimension( 250, 505 );
    // Width of the window's border
    private static final double WINDOW_BORDER_WIDTH = 3;
    
    // Diagram background properties (content area of the window)
    private static final Color BACKGROUND_COLOR = new Color( 240, 240, 240 );
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final Color BACKGROUND_STROKE_COLOR = Color.BLACK;
    
    // Vertical axis properties
    private static final double AXIS_X_MARGIN = 10;
    private static final double AXIS_Y_MARGIN = 10;
    private static final double AXIS_LABEL_SPACING = 3;
    private static final Color AXIS_COLOR = Color.BLACK;
    private static final Color AXIS_LABEL_COLOR = Color.BLACK;
    private static final float AXIS_STROKE_WIDTH = 2;
    private static final Dimension AXIS_ARROW_SIZE = new Dimension( 13, 13 );
    private static final String AXIS_FONT_SIZE_RESOURCE = "energyDiagram.font.size";
    
    // Title bar properties
    private static final Color TITLE_BAR_COLOR = Color.GRAY;
    private static final Color TITLE_COLOR = Color.WHITE;
    private static final double TITLE_BAR_X_MARGIN = 6;
    private static final double TITLE_BAR_Y_MARGIN = 3;
    private static final String TITLE_FONT_SIZE_RESOURCE = "energyDiagram.title.font.size";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double[] _energies; // energy values for each of the electron's states
    
    private ElectronNode _electronNode; // move this node to indicate state
    private PLayer _stateLayer; // add state indicators to this layer
    private PLayer _squiggleLayer; // add squiggles to this layer
    private CloseButtonNode _closeButton;
    
    // area that subclasses can draw in without overlapping the vertical axis
    private Rectangle2D _drawingArea;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param numberOfStates
     * @param canvas
     */
    public AbstractEnergyDiagram( int numberOfStates ) {
        super();
        
        setPickable( false );
        
        // Fonts
        int axisFontSize = HAResources.getInt( AXIS_FONT_SIZE_RESOURCE, HAConstants.DEFAULT_FONT_SIZE );
        Font axisFont = new PhetFont( HAConstants.DEFAULT_FONT_STYLE, axisFontSize );
        
        int titleFontSize = HAResources.getInt( TITLE_FONT_SIZE_RESOURCE, HAConstants.DEFAULT_FONT_SIZE );
        Font titleFont = new PhetFont( HAConstants.DEFAULT_FONT_STYLE, titleFontSize );
        
        // Title bar, with title & close button
        PText titleNode = new PText( HAResources.getString( "title.energyDiagram" ) );
        _closeButton = new CloseButtonNode();
        PPath titleBarNode = new PPath();
        titleBarNode.addChild( titleNode );
        titleBarNode.addChild( _closeButton );
        double titleBarHeight = Math.max( titleNode.getHeight(), _closeButton.getFullBounds().getHeight() ) + ( 2 * TITLE_BAR_Y_MARGIN );
        titleBarNode.setPathTo( new Rectangle2D.Double( 0, 0, WINDOW_SIZE.width, WINDOW_SIZE.height ) );
        titleBarNode.setPaint( TITLE_BAR_COLOR );
        titleBarNode.setStroke( null );
        titleNode.setOffset( TITLE_BAR_X_MARGIN, TITLE_BAR_Y_MARGIN );
        titleNode.setFont( titleFont );
        titleNode.setTextPaint( TITLE_COLOR );
        _closeButton.setOffset( WINDOW_SIZE.width - _closeButton.getFullBounds().getWidth() - TITLE_BAR_X_MARGIN, TITLE_BAR_Y_MARGIN );
        addChild( titleBarNode );
        
        // Everything else, clipped to the bounds of the diagram
        final double diagramWidth = WINDOW_SIZE.getWidth() - ( 2 * WINDOW_BORDER_WIDTH );
        final double diagramHeight = WINDOW_SIZE.getHeight() - titleBarHeight - WINDOW_BORDER_WIDTH;
        PClip clipNode = new PClip();
        // don't call clipNode.setPickable(false), we want to swallow mouse events
        clipNode.setChildrenPickable( false );
        clipNode.setPathTo( new Rectangle2D.Double( 0, 0, diagramWidth, diagramHeight ) );
        clipNode.setPaint( BACKGROUND_COLOR );
        clipNode.setStroke( BACKGROUND_STROKE );
        clipNode.setStrokePaint( BACKGROUND_STROKE_COLOR );
        clipNode.setOffset( WINDOW_BORDER_WIDTH, titleBarHeight );
        addChild( clipNode );
        
        // Energy axis (vertical)
        PNode axisNode = createAxisNode( diagramHeight - ( 2 * AXIS_Y_MARGIN ), axisFont );
        
        // Layer for all state indicators
        _stateLayer = new PLayer();
        
        // Layer for all squiggles
        _squiggleLayer = new PLayer();
        
        // Electron
        _electronNode = new ElectronNode();
        _electronNode.setVisible( false );

        // Layering
        clipNode.addChild( axisNode );
        clipNode.addChild( _stateLayer );
        clipNode.addChild( _squiggleLayer );
        clipNode.addChild( _electronNode ); // on top!
        
        // Positions
        axisNode.setOffset( AXIS_X_MARGIN, AXIS_Y_MARGIN );
        
        // Determine the bounds of the "safe" drawing area,
        // everything to the left of the vertical energy axis.
        double x = AXIS_X_MARGIN + axisNode.getFullBounds().getWidth();
        double y = 0;
        double w = clipNode.getWidth() - x;
        double h = clipNode.getHeight();
        _drawingArea = new Rectangle2D.Double( x, y, w, h );
        if ( DEBUG_DRAWING_AREA_BOUNDS ) {
            PPath p = new PPath( _drawingArea );
            p.setStroke( new BasicStroke( 1f ) );
            p.setStrokePaint( Color.RED );
            clipNode.addChild( p );
        }
        
        // Calculate the energy values for all states
        _energies = calculateEnergies( numberOfStates );
    }
    
    //----------------------------------------------------------------------------
    // Public mutators and accessors
    //----------------------------------------------------------------------------
    
    /**
     * Adds a listener to the close button.
     * 
     * @param listener
     */
    public void addCloseListener( ActionListener listener ) {
        _closeButton.addActionListener( listener );
    }
    
    /**
     * Removes a listener from the close button.
     * 
     * @param listener
     */
    public void removeCloseListener( ActionListener listener ) {
        _closeButton.removeActionListener( listener );
    }
    
    //----------------------------------------------------------------------------
    // Protected mutators and accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the Piccolo node that represents the electron.
     * 
     * @return ElectronNode
     */
    protected ElectronNode getElectronNode() {
        return _electronNode;
    }
    
    /**
     * Get the bounds of the area that subclasses can draw stuff in.
     * 
     * @return Rectangle2D
     */
    protected Rectangle2D getDrawingArea() {
        return _drawingArea;
    }
    
    /**
     * Gets the layer that contains all state indicators.
     * 
     * @param node
     */
    protected PLayer getStateLayer() {
        return _stateLayer;
    }
    
    /**
     * Gets the layer that contains all squiggles.
     * 
     * @param node
     */
    protected PLayer getSquiggleLayer() {
        return _squiggleLayer;
    }
    
    //----------------------------------------------------------------------------
    // Energies
    //----------------------------------------------------------------------------
    
    /**
     * Gets the energy value for a specified state.
     * @param state
     * @return double
     */
    protected double getEnergy( int state ) {
        return _energies[ state - 1 ];
    }
    
    /*
     * Calculates the energy values for a specified number of states.
     * Optional distortion is applied to increase the space between
     * energy values so that they don't overlap when displayed.
     * 
     * @param numberOfStates
     * @param distortionFactor
     */
    private static double[] calculateEnergies( int numberOfStates ) {
        
        // Calculate energies
        double E[] = new double[numberOfStates];
        for ( int i = 0; i < E.length; i++ ) {
            int n = i + 1;
            E[i] = E1 / ( n * n );
        }

        // Apply distortion
        if ( DISTORTION_FACTOR > 0 ) {
            for ( int i = 1; i < E.length - 1; i++ ) {
                E[i] = E[i] * ( 1 + DISTORTION_FACTOR );
            }
        }

        return E;
    }
    
    //----------------------------------------------------------------------------
    // Node creation
    //----------------------------------------------------------------------------
    
    /*
     * Creates a vertical line that represents a state.
     */
    protected static PNode createStateLineNode() {
        PPath lineNode = new PPath( new Line2D.Double( 0, 0, LINE_LENGTH, 0 ) );
        lineNode.setStroke( LINE_STROKE );
        lineNode.setStrokePaint( LINE_COLOR );
        return lineNode;
    }
    
    /*
     * Creates a label for a specified primary state (n).
     */
    protected static PNode createStateLabelNode( int state ) {
        Object[] args = new Object[] { new Integer( state ) };
        String label = MessageFormat.format( LABEL_FORMAT, args );
        PText labelNode = new PText( label );
        labelNode.setFont( LABEL_FONT );
        labelNode.setTextPaint( LABEL_COLOR );
        return labelNode;
    }
    
    /*
     * Creates the vertical energy axis.
     */
    private static PNode createAxisNode( double height, Font font ) {
        
        // Vertical line -- origin at upper left
        PPath lineNode = new PPath();
        lineNode.setPathTo( new Line2D.Double( 0, AXIS_ARROW_SIZE.height - 1, 0, height ) );
        lineNode.setStroke( new BasicStroke( AXIS_STROKE_WIDTH ) );
        lineNode.setStrokePaint( AXIS_COLOR );

        // Arrow head -- origin at tip
        GeneralPath arrowPath = new GeneralPath();
        arrowPath.moveTo( 0, 0 );
        arrowPath.lineTo( (float)( AXIS_ARROW_SIZE.width / 2 ), (float)AXIS_ARROW_SIZE.height );
        arrowPath.lineTo( (float)( -AXIS_ARROW_SIZE.width / 2 ), (float)AXIS_ARROW_SIZE.height );
        arrowPath.closePath();
        PPath arrowNode = new PPath( arrowPath );
        arrowNode.setPaint( AXIS_COLOR );
        arrowNode.setStroke( null );
        
        // Label -- origin at lower-left after rotating
        PText labelNode = new PText( HAResources.getString( "label.energyDiagram.yAxis" ) );
        labelNode.setFont( font );
        labelNode.setTextPaint( AXIS_LABEL_COLOR );
        labelNode.rotate( Math.toRadians( -90 ) ); 
        
        // Layering
        PComposite parentNode = new PComposite();
        parentNode.addChild( lineNode );
        parentNode.addChild( arrowNode );
        parentNode.addChild( labelNode );
        
        // Positions
        double x = 0;
        double y = height;
        labelNode.setOffset( x, y );
        x = labelNode.getFullBounds().getWidth() + AXIS_LABEL_SPACING;
        y = 0;
        lineNode.setOffset( x, y );
        arrowNode.setOffset( x, y );
        
        return parentNode;
    }
}
