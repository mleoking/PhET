/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view.graph;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.TimesTenNumberFormat;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.FormattedNumberNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * GraphNode is the bar graph.
 * The y-axis is log, in units of moles/L.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationGraphNode extends PNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // graph outline
    private static final Stroke OUTLINE_STROKE = new BasicStroke( 1f );
    private static final Color OUTLINE_STROKE_COLOR = Color.BLACK;
    private static final Color OUTLINE_FILL_COLOR = Color.WHITE;
    private static final double DEFAULT_OUTLINE_WIDTH = 350;
    
    // bars
    private static final double BAR_WIDTH = 40;
    private static final Color LHS_BAR_COLOR = ABSConstants.HA_COLOR;
    private static final Color RHS_BAR_COLOR = ABSConstants.A_COLOR;
    private static final Color H3O_BAR_COLOR = ABSConstants.H3O_COLOR;
    private static final Color OH_BAR_COLOR = ABSConstants.OH_COLOR;
    private static final Color H2O_BAR_COLOR = ABSConstants.H2O_COLOR;   
    
    // numeric values
    private static final Font VALUE_FONT = new PhetFont( Font.BOLD, 18 );
    private static final Color VALUE_COLOR = Color.BLACK;
    private static final double VALUE_Y_MARGIN = 10;
    private static final TimesTenNumberFormat LHS_FORMAT = new TimesTenNumberFormat( "0.00" );
    private static final TimesTenNumberFormat RHS_FORMAT = new TimesTenNumberFormat( "0.00" );
    private static final TimesTenNumberFormat H3O_FORMAT = new TimesTenNumberFormat( "0.00" );
    private static final TimesTenNumberFormat OH_FORMAT = new TimesTenNumberFormat( "0.00" );
    private static final DecimalFormat H2O_FORMAT = new DefaultDecimalFormat( "#0" );
    
    // y ticks
    private static final double TICK_LENGTH = 6;
    private static final Stroke TICK_STROKE = new BasicStroke( 1f );
    private static final Color TICK_COLOR = Color.BLACK;
    private static final Font TICK_LABEL_FONT = new PhetFont( 14 );
    private static final Color TICK_LABEL_COLOR = Color.BLACK;
    private static final double TICKS_TOP_MARGIN = 10;
    private static final int NUMBER_OF_TICKS = 19;
    private static final int BIGGEST_TICK_EXPONENT = 2;
    private static final int TICK_EXPONENT_SPACING = 2;
    public static final double DEFAULT_TICKS_Y_SPACING = 24;
    
    // horizontal gridlines
    private static final Stroke GRIDLINE_STROKE = new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3,3}, 0 ); // dashed
    private static final Color GRIDLINE_COLOR = new Color(192, 192, 192, 100 ); // translucent gray
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final double _graphOutlineHeight;
    private final ConcentrationBarNode _barLHS, _barRHS, _barH3O, _barOH, _barH2O;
    private final ValueNode _valueLHS, _valueRHS, _valueH3O, _valueOH, _valueH2O;
    private final ConcentrationXAxisNode _xAxisNode;
    private final ConcentrationYAxisNode _yAxisNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ConcentrationGraphNode() {
        this( DEFAULT_OUTLINE_WIDTH, DEFAULT_TICKS_Y_SPACING );
    }
    
    public ConcentrationGraphNode( double graphOutlineWidth, double ticksYSpacing ) {
        
        _graphOutlineHeight = ( ( NUMBER_OF_TICKS - 1 ) * ticksYSpacing ) + TICKS_TOP_MARGIN;
        
        // graphOutlineNode is not instance data because we do NOT want to use its bounds for calculations.
        // It's stroke width will cause calculation errors.  Use _graphOutlineSize instead.
        Rectangle2D r = new Rectangle2D.Double( 0, 0, graphOutlineWidth, _graphOutlineHeight );
        PPath graphOutlineNode = new PPath( r );
        graphOutlineNode.setStroke( OUTLINE_STROKE );
        graphOutlineNode.setStrokePaint( OUTLINE_STROKE_COLOR );
        graphOutlineNode.setPaint( OUTLINE_FILL_COLOR );
        graphOutlineNode.setPickable( false );
        graphOutlineNode.setChildrenPickable( false );
        addChild( graphOutlineNode );
        
        // x axis
        _xAxisNode = new ConcentrationXAxisNode();
        addChild( _xAxisNode );
        
        // y axis
        PDimension graphOutlineSize = new PDimension( graphOutlineWidth, _graphOutlineHeight );
        _yAxisNode = new ConcentrationYAxisNode( graphOutlineSize, NUMBER_OF_TICKS, TICKS_TOP_MARGIN, 
                BIGGEST_TICK_EXPONENT,  TICK_EXPONENT_SPACING, TICK_LENGTH,
                TICK_STROKE, TICK_COLOR, TICK_LABEL_FONT, TICK_LABEL_COLOR, GRIDLINE_STROKE, GRIDLINE_COLOR );
        addChild( _yAxisNode );
        
        // bars
        _barLHS = new ConcentrationBarNode( BAR_WIDTH, LHS_BAR_COLOR, _graphOutlineHeight );
        addChild( _barLHS );
        _barRHS = new ConcentrationBarNode( BAR_WIDTH, RHS_BAR_COLOR, _graphOutlineHeight );
        addChild( _barRHS );
        _barH3O = new ConcentrationBarNode( BAR_WIDTH, H3O_BAR_COLOR, _graphOutlineHeight );
        addChild( _barH3O );
        _barOH = new ConcentrationBarNode( BAR_WIDTH, OH_BAR_COLOR, _graphOutlineHeight );
        addChild( _barOH );
        _barH2O = new ConcentrationBarNode( BAR_WIDTH, H2O_BAR_COLOR, _graphOutlineHeight );
        addChild( _barH2O );
        
        // line along the bottom of the graph, where bars overlap the outline
        PPath bottomLineNode = new PPath( new Line2D.Double( 0, _graphOutlineHeight, graphOutlineWidth, _graphOutlineHeight ) );
        bottomLineNode.setStroke( OUTLINE_STROKE );
        bottomLineNode.setStrokePaint( OUTLINE_STROKE_COLOR );
        bottomLineNode.setPickable( false );
        addChild( bottomLineNode );
        
        // numbers
        _valueLHS = new ValueNode( LHS_FORMAT );
        addChild( _valueLHS );
        _valueRHS = new ValueNode( RHS_FORMAT );
        addChild( _valueRHS );
        _valueH3O = new ValueNode( H3O_FORMAT );
        addChild( _valueH3O );
        _valueOH = new ValueNode( OH_FORMAT );
        addChild( _valueOH );
        _valueH2O = new ValueNode( H2O_FORMAT );
        addChild( _valueH2O );
        
        // layout
        graphOutlineNode.setOffset( 0, 0 );
        PBounds gob = graphOutlineNode.getFullBoundsReference();
        // x axis, centered below graph
        _xAxisNode.setOffset( gob.getX() + ( gob.getWidth() - _xAxisNode.getFullBoundsReference().getWidth() ) / 2, gob.getMaxY() + 10 );
        // y axis, to left of graph
        _yAxisNode.setOffset( graphOutlineNode.getOffset() );
        // bars, evenly distributed across graph width
        PNode[] bars = { _barLHS, _barRHS, _barH3O, _barOH, _barH2O }; // left-to-right layout order
        final double xSpacing = ( gob.getWidth() - ( bars.length * BAR_WIDTH ) ) / ( bars.length + 1 );
        assert( xSpacing > 0 );
        for ( int i = 0; i < bars.length; i++ ) {
            double xOffset = graphOutlineNode.getXOffset() + ( i * ( xSpacing + BAR_WIDTH ) ) + ( BAR_WIDTH / 2. );
            double yOffset = graphOutlineSize.getHeight();
            bars[i].setOffset( xOffset, yOffset );
        }
        // values, horizontally centered in bars
        PNode[] values = { _valueLHS, _valueRHS, _valueH3O, _valueOH, _valueH2O }; // left-to-right layout order
        assert( values.length == bars.length );
        for ( int i = 0; i < values.length; i++ ) {
            double xOffset = bars[i].getXOffset() + ( bars[i].getFullBoundsReference().getWidth() - values[i].getFullBoundsReference().getWidth() ) / 2;
            double yOffset = gob.getMaxY() - values[i].getFullBoundsReference().getHeight() - VALUE_Y_MARGIN;
            values[i].setOffset( xOffset, yOffset );
        }

        //XXX remove this
        setConcentrationLHS( 1E1 );
        setConcentrationRHS( 1E0 );
        setConcentrationH3O( 1E-1 );
        setConcentrationOH( 1E-2 );
        setConcentrationH2O( 1E-3 );
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setMoleculeLHS( Image image, String text, Color barColor ) {
        _xAxisNode.setMoleculeLHS( image, text );
        _barLHS.setPaint( barColor );
    }
    
    public void setMoleculeRHS( Image image, String text, Color barColor ) {
        _xAxisNode.setMoleculeRHS( image, text );
        _barLHS.setPaint( barColor );
    }
    
    public void setConcentrationLHS( double value ) {
        _valueLHS.setValue( value );
        _barLHS.setBarHeight( calculateBarHeight( value ) );
    }
    
    public void setConcentrationRHS( double value ) {
        _valueRHS.setValue( value );
        _barRHS.setBarHeight( calculateBarHeight( value ) );
    }
    
    public void setConcentrationH3O( double value ) {
        _valueH3O.setValue( value );
        _barH3O.setBarHeight( calculateBarHeight( value ) );
    }
    
    public void setConcentrationH2O( double value ) {
        _valueH2O.setValue( value );
        _barH2O.setBarHeight( calculateBarHeight( value ) );
    }
    
    public void setConcentrationOH( double value ) {
        _valueOH.setValue( value );
        _barOH.setBarHeight( calculateBarHeight( value ) );
    }
    
    /*
     * Calculates a bar height in view coordinates, given a model value.
     */
    private double calculateBarHeight( final double modelValue ) {
        final double maxTickHeight = _graphOutlineHeight - TICKS_TOP_MARGIN;
        final double maxExponent = BIGGEST_TICK_EXPONENT;
        final double minExponent = BIGGEST_TICK_EXPONENT - NUMBER_OF_TICKS + 1;
        final double modelValueExponent = MathUtil.log10( modelValue );
        return maxTickHeight * ( modelValueExponent - minExponent ) / ( maxExponent - minExponent );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * Values displayed on the bars.
     */
    private static class ValueNode extends FormattedNumberNode {

        private static final double DEFAULT_VALUE = 0;

        public ValueNode( NumberFormat format ) {
            super( format, DEFAULT_VALUE, VALUE_FONT, VALUE_COLOR );
            rotate( -Math.PI / 2 );
            setPickable( false );
            setChildrenPickable( false );
        }
    }
}
