/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.util.UnitsConverter;
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
    
    private static final int TICK_SPACING_METRIC = GlaciersConstants.ELEVATION_AXIS_TICK_SPACING_METRIC;
    private static final int TICK_SPACING_ENGLISH = GlaciersConstants.ELEVATION_AXIS_TICK_SPACING_ENGLISH;
    
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
    private final ModelViewTransform _mvt;
    private final double _minElevation, _maxElevation;
    private final boolean _tickLabelOnLeft;
    private boolean _englishUnits;
    private boolean _isDirty; // does the axis need to be rebuilt when it becomes visible?
    
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
     * @param tickLabelOnLeft  true for label to left of tick, false for label to right of tick
     * @param englishUnits true for English units, false for metric units
     */
    public ElevationAxisNode( ModelViewTransform mvt, DoubleRange elevationRange, boolean tickLabelOnLeft, boolean englishUnits ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        _mvt = mvt;
        _minElevation = elevationRange.getMin();
        _maxElevation = elevationRange.getMax();
        _tickLabelOnLeft = tickLabelOnLeft;
        _englishUnits = englishUnits;
        _isDirty = true;
        
        _parentNode = new PComposite();
        addChild( _parentNode );
        
        update();
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
     * Sets units to English or metric.
     * 
     * @param englishUnits true for English, false for metric
     */
    public void setEnglishUnits( boolean englishUnits ) {
        if ( _englishUnits != englishUnits ) {
            _englishUnits = englishUnits;
            update();
        }
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
            PNode axisNode = createAxis( 0, _minElevation, _maxElevation, _mvt );
            _parentNode.addChild( axisNode );
            
            // ticks
            final double minElevation = ( _englishUnits ? UnitsConverter.metersToFeet( _minElevation ) : _minElevation );
            final double maxElevation = ( _englishUnits ? UnitsConverter.metersToFeet( _maxElevation ) : _maxElevation );
            final int tickSpacing = ( _englishUnits ? TICK_SPACING_ENGLISH : TICK_SPACING_METRIC );
            final String units = ( _englishUnits ? GlaciersStrings.UNITS_FEET_SYMBOL : GlaciersStrings.UNITS_METERS );
            int elevation = (int)( minElevation / tickSpacing ) * tickSpacing; // feet or meters, depending on value of _englishUnits
            while ( elevation <= maxElevation ) {
                
                // create the tick node
                PNode tickNode = createTick( elevation, units, _tickLabelOnLeft );
                _parentNode.addChild( tickNode );
                
                // position the node
                double meters = ( _englishUnits ? UnitsConverter.feetToMeters( elevation ) : elevation );
                Point2D pModel = new Point2D.Double( 0, meters );
                Point2D pView = _mvt.modelToView( pModel );
                tickNode.setOffset( pView );
                
                elevation += tickSpacing;
            }
        }
    }
    
    /*
     * Create a vertical axis.
     * The axis is automatically positioned in the view coordinate system.
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
     * Creates a tick, a horizontal line with a label next to it (on left or right).
     * Origin is at the end of the line opposite the label.
     */
    private static PNode createTick( double value, String units, boolean labelOnLeft ) {
        
        PComposite tickNode = new PComposite();
        
        // horizontal line
        GeneralPath path = new GeneralPath();
        path.moveTo( 0f, 0f );
        if ( labelOnLeft ) {
            path.lineTo( (float) -TICK_LENGTH, 0f ); 
        }
        else {
            path.lineTo( (float) TICK_LENGTH, 0f );
        }
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
        
        // position label on left or right of tick, vertically align centers
        lineNode.setOffset( 0, 0 );
        PBounds bLine = lineNode.getFullBoundsReference();
        PBounds bLabel = labelNode.getFullBoundsReference();
        if ( labelOnLeft ) {
            labelNode.setOffset( bLine.getMinX() - bLabel.getWidth() - TICK_LABEL_SPACING, bLine.getCenterY() - bLabel.getHeight() / 2 );
        }
        else {
            labelNode.setOffset( bLine.getMaxX() + TICK_LABEL_SPACING, bLine.getCenterY() - bLabel.getHeight() / 2 );
        }
        
        return tickNode;
    }
}
