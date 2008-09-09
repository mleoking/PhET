/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * ElevationAxisNode draws the elevation (vertical) axis.
 * The elevation axis is intended to be built once, then repositioned (using setOffset)
 * as the bounds of the zoomed viewport change.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ElevationAxisNode extends PComposite {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color AXIS_COLOR = Color.BLACK;
    private static final Stroke AXIS_STROKE = new BasicStroke( 1f );
    private static final Color TICK_COLOR = AXIS_COLOR;
    private static final Stroke TICK_STROKE = AXIS_STROKE;
    private static final double TICK_LENGTH = 100; // meters
    private static final Color TICK_LABEL_COLOR = TICK_COLOR;
    private static final Font TICK_LABEL_FONT = new PhetFont( 12 );
    private static final double TICK_LABEL_SPACING = 2; // pixels
    private static final DecimalFormat TICK_LABEL_FORMAT = new DecimalFormat( "0" );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final PComposite _parentNode;
    private final ModelViewTransform _mvt;
    private final double _minElevation, _maxElevation, _majorTickSpacing;
    private final boolean _tickLabelOnLeft;
    private boolean _englishUnits;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param valley
     * @param mvt
     * @param minElevation meters
     * @param maxElevation meters
     * @param majorTickSpacing meters
     * @param tickLabelOnLeft  true for label to left of tick, false for label to right of tick
     * @param englishUnits true for English units, false for metric units
     */
    public ElevationAxisNode( ModelViewTransform mvt, DoubleRange elevationRange, double majorTickSpacing, boolean tickLabelOnLeft, boolean englishUnits ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        _mvt = mvt;
        _minElevation = elevationRange.getMin();
        _maxElevation = elevationRange.getMax();
        _majorTickSpacing = majorTickSpacing;
        _tickLabelOnLeft = tickLabelOnLeft;
        _englishUnits = englishUnits;
        
        _parentNode = new PComposite();
        addChild( _parentNode );
        
        createNodes( 0 /* x location */ );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setEnglishUnits( boolean englishUnits ) {
        _englishUnits = englishUnits;
        update();
    }
    
    //----------------------------------------------------------------------------
    // Node creation
    //----------------------------------------------------------------------------
    
    private void update() {
        //XXX
    }
    
    /*
     * Creates all nodes at some downvalley (horizontal) location.
     */
    private void createNodes( double x ) {
        
        _parentNode.removeAllChildren();
        
        // axis
        PNode axisNode = createAxis( x, _minElevation, _maxElevation, _mvt );
        _parentNode.addChild( axisNode );
        
        // ticks & labels
        double elevation = _minElevation;
        while ( elevation <= _maxElevation ) {
            PNode tickNode = createLabeledTickNode( x, elevation, _tickLabelOnLeft, _englishUnits, _mvt );
            _parentNode.addChild( tickNode );
            elevation += _majorTickSpacing;
        }
    }
    
    /*
     * Create a vertical axis.
     */
    private static PNode createAxis( double x, double minElevation, double maxElevation, ModelViewTransform mvt ) {

        // bottom of axis
        Point2D pBottomModel = new Point2D.Double( x, minElevation );
        Point2D pBottomView = mvt.modelToView( pBottomModel );
        
        // top of axis 
        Point2D pTopModel = new Point2D.Double( x, maxElevation );
        Point2D pTopView = mvt.modelToView( pTopModel );
        
        // path
        GeneralPath axisPath = new GeneralPath();
        axisPath.moveTo( (float)pBottomView.getX(), (float)pBottomView.getY() );
        axisPath.lineTo( (float)pTopView.getX(), (float)pTopView.getY()  );
        
        // node
        PPath axisNode = new PPath( axisPath );
        axisNode.setPaint( null );
        axisNode.setStroke( AXIS_STROKE );
        axisNode.setStrokePaint( AXIS_COLOR );

        return axisNode;
    }
    
    /*
     * Creates a labeled tick.
     */
    private static PNode createLabeledTickNode( double x, double elevation, boolean tickLabelOnLeft, boolean englishUnits, ModelViewTransform mvt ) {
        
        PComposite parentNode = new PComposite();
        
        // tick
        PNode tickNode = createTickNode( x, elevation, tickLabelOnLeft, mvt );
        parentNode.addChild( tickNode );
        
        // label
        PNode labelNode = createTickLabelNode( elevation, englishUnits );
        parentNode.addChild( labelNode );
        
        tickNode.setOffset( 0, 0 );
        PBounds tb = tickNode.getFullBoundsReference();
        PBounds lb = labelNode.getFullBoundsReference();
        if ( tickLabelOnLeft ) {
            // position label to left of tick, vertically align centers
            labelNode.setOffset( tb.getX() - lb.getWidth() - TICK_LABEL_SPACING, tb.getY() + ( tb.getHeight() / 2 ) - ( lb.getHeight() / 2 ) );
        }
        else {
            // position label to right of tick, vertically align centers
            labelNode.setOffset( tb.getMaxX() + TICK_LABEL_SPACING, tb.getY() + ( tb.getHeight() / 2 ) - ( lb.getHeight() / 2 ) );
        }

        return parentNode;
    }
    
    /*
     * Creates a tick, a horizontal line.
     */
    private static PNode createTickNode( double x, double elevation, boolean tickLabelOnLeft, ModelViewTransform mvt ) {
        
        // right edge of tick
        Point2D pLeftModel = new Point2D.Double( ( tickLabelOnLeft ? x : x + TICK_LENGTH ), elevation );
        Point2D pLeftView = mvt.modelToView( pLeftModel );
        
        // left edge of tick
        Point2D pRightModel = new Point2D.Double( ( tickLabelOnLeft ? x - TICK_LENGTH : x ), elevation );
        Point2D pRightView = mvt.modelToView( pRightModel );
        
        // path
        GeneralPath path = new GeneralPath();
        path.moveTo( (float)pLeftView.getX(), (float)pLeftView.getY() );
        path.lineTo( (float)pRightView.getX(), (float)pRightView.getY() );
        
        // node
        PPath pathNode = new PPath( path );
        pathNode.setPaint( null );
        pathNode.setStroke( TICK_STROKE );
        pathNode.setStrokePaint( TICK_COLOR );
        
        return pathNode;
    }
    
    /*
     * Creates a tick label.
     */
    private static PNode createTickLabelNode( double elevation, boolean englishUnits ) {
        String units = ( englishUnits ? GlaciersStrings.UNITS_FEET_SYMBOL : GlaciersStrings.UNITS_METERS );
        String s = TICK_LABEL_FORMAT.format( elevation ) + units;
        PText textNode = new PText( s );
        textNode.setFont( TICK_LABEL_FONT );
        textNode.setTextPaint( TICK_LABEL_COLOR );
        return textNode;
    }

    /**
     * Creates an icon to represent a set of x-y axes.
     * @return Icon
     */
    public static Icon createIcon() {
        
        PNode parentNode = new PNode();
        
        // constants
        final double xStart = -10;
        final double xEnd = 20;
        final double yStart = -10;
        final double yEnd = 10;
        final double tickSpacing = 5;
        final double tickLength = 2;
        final Stroke stroke = new BasicStroke( 1f );
        
        // x axis with tick marks
        PPath xAxisNode = new PPath( new Line2D.Double( xStart, 0, xEnd, 0 ) );
        xAxisNode.setStroke( stroke );
        xAxisNode.setStrokePaint( AXIS_COLOR );
        parentNode.addChild( xAxisNode );
        for ( double x = ( xStart + tickSpacing ); x < xEnd; x += tickSpacing ) {
            PPath tickNode = new PPath( new Line2D.Double( x, -(tickLength/2), x, (tickLength/2) ) );
            tickNode.setStroke( stroke );
            tickNode.setStrokePaint( AXIS_COLOR );
            parentNode.addChild( tickNode );
        }
        
        // y axis with tick marks
        PPath yAxisNode = new PPath( new Line2D.Double( 0, yStart, 0, yEnd ) );
        yAxisNode.setStroke( stroke );
        yAxisNode.setStrokePaint( AXIS_COLOR );
        parentNode.addChild( yAxisNode );
        for ( double y = ( yStart + tickSpacing ); y < yEnd; y += tickSpacing ) {
            PPath tickNode = new PPath( new Line2D.Double( -(tickLength/2), y, (tickLength/2), y ) );
            tickNode.setStroke( stroke );
            tickNode.setStrokePaint( AXIS_COLOR );
            parentNode.addChild( tickNode );
        }
        
        Image image = parentNode.toImage();
        return new ImageIcon( image );
    }
}
