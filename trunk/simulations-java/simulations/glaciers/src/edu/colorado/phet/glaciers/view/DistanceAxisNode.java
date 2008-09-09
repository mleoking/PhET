/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.Valley;
import edu.colorado.phet.glaciers.util.UnitsConverter;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * DistanceAxisNode draws the horizontal (distance) axis.
 * Calling this an axis is a bit of a misnomer; it's actually a line that
 * follows the valley floor contour, with x position tick marks.
 * <p>
 * Because the range of the valley floor is so large, and because the birds-eye
 * viewport can change size as the main window is resized,
 * this axis is intended to be rebuilt when the bounds of the zoomed viewport change.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DistanceAxisNode extends PComposite {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double DX = 100; // meters
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
    private final Valley _valley;
    private final ModelViewTransform _mvt;
    private final double _majorTickSpacing;
    private double _minX, _maxX; // x range
    private boolean _isDirty; // does the axis need to be rebuilt when it becomes visible?
    private final boolean _englishUnits;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param valley
     * @param mvt
     * @param majorTickSpacing meters
     * @param englishUnits true for English units, false for metric units
     */
    public DistanceAxisNode( Valley valley, ModelViewTransform mvt, double majorTickSpacing, boolean englishUnits ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        _valley = valley;
        _mvt = mvt;
        _majorTickSpacing = majorTickSpacing;
        _englishUnits = englishUnits;
        
        _parentNode = new PComposite();
        addChild( _parentNode );
        
        _minX = _maxX = 0;
        _isDirty = true;
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * If the node is made visible and is dirty, update it.
     * 
     * @param visible
     */
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if ( _isDirty && getVisible() ) {
            setRange( _minX, _maxX );
        }
    }
    
    /**
     * Sets the x range for the axis.
     * If visible, the axis is rebuilt; if not, it will be rebuilt when it becomes visible.
     * 
     * @param minX meters
     * @param maxX meters
     */
    public void setRange( double minX, double maxX ) {
        
        assert( minX < maxX );
        
        _minX = minX;
        _maxX = maxX;
        
        if ( getVisible() == false ) {
            _isDirty = true;
        }
        else {
            _parentNode.removeAllChildren();

            // axis
            PNode axisNode = createAxis( _valley, _mvt, minX, maxX, DX );
            _parentNode.addChild( axisNode );

            // ticks & labels
            double x = minX;
            while ( x <= maxX ) {
                double elevation = _valley.getElevation( x );
                PNode tickNode = createLabeledTickNode( x, elevation, _englishUnits, _mvt );
                _parentNode.addChild( tickNode );
                x += _majorTickSpacing;
            }
            
            _isDirty = false;
        }
    }
    
    //----------------------------------------------------------------------------
    // Node creation
    //----------------------------------------------------------------------------
    
    /*
     * Create a line that follows the valley contour.
     */
    private static PNode createAxis( Valley valley, ModelViewTransform mvt, double minX, double maxX, double dx ) {

        // path
        GeneralPath axisPath = valley.createValleyFloorPath( mvt, minX, maxX, dx );
        
        // node
        PPath axisNode = new PPath( axisPath );
        axisNode.setPaint( null );
        axisNode.setStroke( AXIS_STROKE );
        axisNode.setStrokePaint( AXIS_COLOR );

        return axisNode;
    }
    
    /*
     * Creates a labeled tick, a vertical line with a label below the line.
     */
    private static PNode createLabeledTickNode( double x, double elevation, boolean englishUnits, ModelViewTransform mvt ) {
        
        PComposite parentNode = new PComposite();
        
        // tick
        PNode tickNode = createTickNode( x, elevation, mvt );
        parentNode.addChild( tickNode );
        
        // label
        PNode labelNode = createTickLabelNode( x, englishUnits );
        parentNode.addChild( labelNode );
        
        // position label below tick, horizontally align centers
        tickNode.setOffset( 0, 0 );
        PBounds tb = tickNode.getFullBoundsReference();
        PBounds lb = labelNode.getFullBoundsReference();
        labelNode.setOffset( tb.getX() + ( tb.getWidth() / 2 ) - ( lb.getWidth() / 2 ), tb.getMaxY() + TICK_LABEL_SPACING );

        return parentNode;
    }
    
    /*
     * Creates a tick, a vertical line.
     */
    private static PNode createTickNode( double x, double elevation, ModelViewTransform mvt ) {
        
        // top of tick
        Point2D pLeftModel = new Point2D.Double( x, elevation );
        Point2D pLeftView = mvt.modelToView( pLeftModel );
        
        // bottom of tick
        Point2D pRightModel = new Point2D.Double( x, elevation - TICK_LENGTH );
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
    private static PNode createTickLabelNode( double x, boolean englishUnits ) {
        double value = ( englishUnits ? UnitsConverter.metersToFeet( x ) : x );
        String units = ( englishUnits ? GlaciersStrings.UNITS_FEET_SYMBOL : GlaciersStrings.UNITS_METERS );
        String s = TICK_LABEL_FORMAT.format( value ) + units;
        PText textNode = new PText( s );
        textNode.setFont( TICK_LABEL_FONT );
        textNode.setTextPaint( TICK_LABEL_COLOR );
        return textNode;
    }

}
