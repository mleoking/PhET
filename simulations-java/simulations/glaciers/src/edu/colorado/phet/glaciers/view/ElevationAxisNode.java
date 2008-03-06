package edu.colorado.phet.glaciers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;


public class ElevationAxisNode extends PComposite {
    
    private static final Color AXIS_COLOR = Color.BLACK;
    private static final Stroke AXIS_STROKE = new BasicStroke( 2f );
    private static final Color TICK_COLOR = AXIS_COLOR;
    private static final Stroke TICK_STROKE = AXIS_STROKE;
    private static final double TICK_LENGTH = 100; // meters
    private static final Color TICK_LABEL_COLOR = TICK_COLOR;
    private static final Font TICK_LABEL_FONT = new PhetDefaultFont( 12 );
    private static final DecimalFormat TICK_LABEL_FORMAT = new DecimalFormat( "0" );
    private static final boolean TICK_ON_LEFT = true; // true=label to left of tick, false=label to right of tick
    
    private final PComposite _parentNode;
    private final ModelViewTransform _mvt;
    private final double _minElevation, _maxElevation, _majorTickSpacing;
    
    /**
     * Constructor.
     * 
     * @param valley
     * @param mvt
     * @param minElevation meters
     * @param maxElevation meters
     * @param majorTickSpacing meters
     */
    public ElevationAxisNode( ModelViewTransform mvt, DoubleRange elevationRange, double majorTickSpacing ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        _mvt = mvt;
        _minElevation = elevationRange.getMin();
        _maxElevation = elevationRange.getMax();
        _majorTickSpacing = majorTickSpacing;
        _parentNode = new PComposite();
        addChild( _parentNode );
        
        setDownvalleyLocation( 0 );
    }
    
    public void setDownvalleyLocation( double x ) {
        
        _parentNode.removeAllChildren();
        
        // axis
        PNode axisNode = createAxis( x, _minElevation, _maxElevation, _mvt );
        _parentNode.addChild( axisNode );
        
        // ticks & labels
        double elevation = _minElevation;
        while ( elevation <= _maxElevation ) {
            PNode tickNode = createLabeledTickNode( x, elevation, _mvt );
            elevation += _majorTickSpacing;
            _parentNode.addChild( tickNode );
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
     * Creates a labeled tick, a horizontal line with a label to the right of the line.
     */
    private static PNode createLabeledTickNode( double x, double elevation, ModelViewTransform mvt ) {
        
        PComposite parentNode = new PComposite();
        
        // tick
        PNode tickNode = createTickNode( x, elevation, mvt );
        parentNode.addChild( tickNode );
        
        // label
        PNode labelNode = createTickLabelNode( elevation );
        parentNode.addChild( labelNode );
        
        // position label to left of tick, vertically align centers
        tickNode.setOffset( 0, 0 );
        PBounds tb = tickNode.getFullBoundsReference();
        PBounds lb = labelNode.getFullBoundsReference();
        if ( TICK_ON_LEFT ) {
            labelNode.setOffset( tb.getX() - lb.getWidth() - 2, tb.getY() + ( tb.getHeight() / 2 ) - ( lb.getHeight() / 2 ) );
        }
        else {
            labelNode.setOffset( tb.getMaxX() + 2, tb.getY() + ( tb.getHeight() / 2 ) - ( lb.getHeight() / 2 ) );
        }

        return parentNode;
    }
    
    /*
     * Creates a tick, a horizontal line.
     */
    private static PNode createTickNode( double x, double elevation, ModelViewTransform mvt ) {
        
        // right edge of tick
        Point2D pLeftModel = new Point2D.Double( ( TICK_ON_LEFT ? x : x + TICK_LENGTH ), elevation );
        Point2D pLeftView = mvt.modelToView( pLeftModel );
        
        // left edge of tick
        Point2D pRightModel = new Point2D.Double( ( TICK_ON_LEFT ? x - TICK_LENGTH : x ), elevation );
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
    private static PNode createTickLabelNode( double elevation ) {
        String s = TICK_LABEL_FORMAT.format( elevation );
        PText textNode = new PText( s );
        textNode.setFont( TICK_LABEL_FONT );
        textNode.setTextPaint( TICK_LABEL_COLOR );
        return textNode;
    }

}
