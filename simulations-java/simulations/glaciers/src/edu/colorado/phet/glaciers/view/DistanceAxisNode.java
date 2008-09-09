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
import edu.colorado.phet.glaciers.GlaciersConstants;
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
    
    private static final int TICK_SPACING_METRIC = GlaciersConstants.DISTANCE_AXIS_TICK_SPACING_METRIC;
    private static final int TICK_SPACING_ENGLISH = GlaciersConstants.DISTANCE_AXIS_TICK_SPACING_ENGLISH;
    
    private static final double DX = 100; // meters
    private static final Color AXIS_COLOR = Color.BLACK;
    private static final Stroke AXIS_STROKE = new BasicStroke( 1f );
    private static final Color TICK_COLOR = AXIS_COLOR;
    private static final Stroke TICK_STROKE = AXIS_STROKE;
    private static final double TICK_LENGTH = 10; // pixels
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
    private double _minX, _maxX; // x range, in meters
    private boolean _isDirty; // does the axis need to be rebuilt when it becomes visible?
    private boolean _englishUnits;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param valley
     * @param mvt
     * @param englishUnits true for English units, false for metric units
     */
    public DistanceAxisNode( Valley valley, ModelViewTransform mvt, boolean englishUnits ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        _valley = valley;
        _mvt = mvt;
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
            update();
        }
    }
    
    /**
     * Sets the x range for the axis.
     * 
     * @param minX meters
     * @param maxX meters
     */
    public void setRange( double minX, double maxX ) {
        assert( minX < maxX );
        _minX = minX;
        _maxX = maxX;
        update();
    }
    
    /**
     * Sets units to English or metric.
     * 
     * @param englishUnits true for English, false for metric
     */
    public void setEnglishUnits( boolean englishUnits ) {
        _englishUnits = englishUnits;
        update();
    }
    
    //----------------------------------------------------------------------------
    // Node creation
    //----------------------------------------------------------------------------
    
    /*
     * Rebuilds the axis.
     * If the node is not visible, it will be rebuilt when it becomes visible.
     */
    private void update() {
        
        if ( getVisible() == false ) {
            _isDirty = true;
        }
        else {
            _parentNode.removeAllChildren();

            // axis
            PNode axisNode = createAxis( _valley, _mvt, _minX, _maxX, DX );
            _parentNode.addChild( axisNode );

            // ticks
            final double minX = ( _englishUnits ? UnitsConverter.metersToFeet( _minX ) : _minX );
            final double maxX = ( _englishUnits ? UnitsConverter.metersToFeet( _maxX ) : _maxX );
            final int tickSpacing = ( _englishUnits ? TICK_SPACING_ENGLISH : TICK_SPACING_METRIC );
            final String units = ( _englishUnits ? GlaciersStrings.UNITS_FEET_SYMBOL : GlaciersStrings.UNITS_METERS );
            int x = (int)( minX / tickSpacing ) * tickSpacing; // feet or meters, depending on value of _englishUnits
            while ( x <= maxX ) {
                
                // create the tick node
                PNode tickNode = createTick( x, units );
                _parentNode.addChild( tickNode );
                
                // position the node
                double meters = ( _englishUnits ? UnitsConverter.feetToMeters( x ) : x );
                double elevation = _valley.getElevation( meters );
                Point2D pModel = new Point2D.Double( meters, elevation );
                Point2D pView = _mvt.modelToView( pModel );
                tickNode.setOffset( pView );
                
                x += tickSpacing;
            }
            
            _isDirty = false;
        }
    }
    
    /*
     * Create a line that follows the valley contour.
     * The line is automatically positioned in the view coordinate system.
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
     * Creates a tick, a vertical line with a label beneath it.
     * Origin is at the top center of the vertical line.
     */
    private static PNode createTick( int value, String units ) {
        
        PComposite tickNode = new PComposite();
        
        // vertical line
        GeneralPath path = new GeneralPath();
        path.moveTo( 0f, 0f );
        path.lineTo( 0f, (float) TICK_LENGTH );
        PPath lineNode = new PPath( path );
        lineNode.setPaint( null );
        lineNode.setStroke( TICK_STROKE );
        lineNode.setStrokePaint( TICK_COLOR );
        tickNode.addChild( lineNode );
        
        // label
        String s = TICK_LABEL_FORMAT.format( value ) + units;
        PText labelNode = new PText( s );
        labelNode.setFont( TICK_LABEL_FONT );
        labelNode.setTextPaint( TICK_LABEL_COLOR );
        tickNode.addChild( labelNode );
        
        // position label below tick, horizontally align centers
        lineNode.setOffset( 0, 0 );
        PBounds b1 = lineNode.getFullBoundsReference();
        PBounds b2 = labelNode.getFullBoundsReference();
        labelNode.setOffset( b1.getX() + ( b1.getWidth() / 2 ) - ( b2.getWidth() / 2 ), b1.getMaxY() + TICK_LABEL_SPACING );
        
        return tickNode;
    }

}
