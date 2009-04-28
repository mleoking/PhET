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
import edu.umd.cs.piccolo.nodes.PText;
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
    
    //TODO localize
    private static final String AXIS_LABEL_CONCENTRATION = "Equilibrium Concentration (mol/L)";
    
    // graph outline
    private static final Stroke OUTLINE_STROKE = new BasicStroke( 1f );
    private static final Color OUTLINE_STROKE_COLOR = Color.BLACK;
    private static final Color OUTLINE_FILL_COLOR = Color.WHITE;
    private static final double DEFAULT_OUTLINE_WIDTH = 225;
    
    // bars
    private static final double BAR_WIDTH = 50;
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
    
    // axis label
    private static final Font AXIS_LABEL_FONT = new PhetFont( 14 );
    private static final Color AXIS_LABEL_COLOR = Color.BLACK;
    private static final double AXIS_LABEL_X_MARGIN = 4;
    
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
    private final ConcentrationBarNode _lhsBarNode, _rhsBarNode, _h3oBarNode, _ohBarNode, _h2oBarNode;
    private final ValueNode _lhsNumberNode, _rhsNumberNode, _h3oNumberNode, _ohNumberNode, _h2oNumberNode;
    private final ConcentrationXAxisNode _xAxisNode;
    private final ConcentrationYAxisNode _yAxisNode;
    private final PText _yAxisLabel;//TODO move this into ConcentrationYAxisNode
    
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
        
        // log y axis
        PDimension graphOutlineSize = new PDimension( graphOutlineWidth, _graphOutlineHeight );
        _yAxisNode = new ConcentrationYAxisNode( graphOutlineSize, NUMBER_OF_TICKS, TICKS_TOP_MARGIN, 
                BIGGEST_TICK_EXPONENT,  TICK_EXPONENT_SPACING, TICK_LENGTH,
                TICK_STROKE, TICK_COLOR, TICK_LABEL_FONT, TICK_LABEL_COLOR, GRIDLINE_STROKE, GRIDLINE_COLOR );
        addChild( _yAxisNode );
        
        // y-axis label
        _yAxisLabel = new PText( AXIS_LABEL_CONCENTRATION );
        _yAxisLabel.rotate( -Math.PI / 2 );
        _yAxisLabel.setFont( AXIS_LABEL_FONT );
        _yAxisLabel.setTextPaint( AXIS_LABEL_COLOR );
        _yAxisLabel.setPickable( false );
        addChild( _yAxisLabel );
        
        // bars
        _lhsBarNode = new ConcentrationBarNode( BAR_WIDTH, LHS_BAR_COLOR, _graphOutlineHeight );
        addChild( _lhsBarNode );
        _rhsBarNode = new ConcentrationBarNode( BAR_WIDTH, RHS_BAR_COLOR, _graphOutlineHeight );
        addChild( _rhsBarNode );
        _h3oBarNode = new ConcentrationBarNode( BAR_WIDTH, H3O_BAR_COLOR, _graphOutlineHeight );
        addChild( _h3oBarNode );
        _ohBarNode = new ConcentrationBarNode( BAR_WIDTH, OH_BAR_COLOR, _graphOutlineHeight );
        addChild( _ohBarNode );
        _h2oBarNode = new ConcentrationBarNode( BAR_WIDTH, H2O_BAR_COLOR, _graphOutlineHeight );
        addChild( _h2oBarNode );
        
        // line along the bottom of the graph, where bars overlap the outline
        PPath bottomLineNode = new PPath( new Line2D.Double( 0, _graphOutlineHeight, graphOutlineWidth, _graphOutlineHeight ) );
        bottomLineNode.setStroke( OUTLINE_STROKE );
        bottomLineNode.setStrokePaint( OUTLINE_STROKE_COLOR );
        bottomLineNode.setPickable( false );
        addChild( bottomLineNode );
        
        // numbers
        _lhsNumberNode = new ValueNode( LHS_FORMAT );
        addChild( _lhsNumberNode );
        _rhsNumberNode = new ValueNode( RHS_FORMAT );
        addChild( _rhsNumberNode );
        _h3oNumberNode = new ValueNode( H3O_FORMAT );
        addChild( _h3oNumberNode );
        _ohNumberNode = new ValueNode( OH_FORMAT );
        addChild( _ohNumberNode );
        _h2oNumberNode = new ValueNode( H2O_FORMAT );
        addChild( _h2oNumberNode );
        
        // layout
        //TODO: fix this
        graphOutlineNode.setOffset( 0, 0 );
        PBounds gob = graphOutlineNode.getFullBoundsReference();
        _yAxisNode.setOffset( graphOutlineNode.getOffset() );
        // center the label on the y axis
        double xOffset = _yAxisNode.getFullBoundsReference().getX() - _yAxisLabel.getFullBoundsReference().getWidth() - AXIS_LABEL_X_MARGIN;
        double yOffset = _yAxisNode.getFullBoundsReference().getCenterY() + ( _yAxisLabel.getFullBoundsReference().getHeight() / 2 );
        _yAxisLabel.setOffset( xOffset, yOffset );
        final double xMargin = ( graphOutlineNode.getWidth() - ( 3 * BAR_WIDTH ) ) / 12;
        assert( xMargin > 0 );
        final double xH3O = ( 1./6.) * gob.getWidth() + xMargin;
        final double xOH = ( 3./6. ) * gob.getWidth();
        final double xH2O = ( 5./6.) * gob.getWidth() - xMargin;
        _h3oNumberNode.setOffset( xH3O - _h3oNumberNode.getFullBoundsReference().getWidth()/2, gob.getMaxY() - _h3oNumberNode.getFullBoundsReference().getHeight() - VALUE_Y_MARGIN );
        _ohNumberNode.setOffset( xOH - _ohNumberNode.getFullBoundsReference().getWidth()/2, gob.getMaxY() - _ohNumberNode.getFullBoundsReference().getHeight() - VALUE_Y_MARGIN );
        _h2oNumberNode.setOffset( xH2O - _h2oNumberNode.getFullBoundsReference().getWidth()/2, gob.getMaxY() - _h2oNumberNode.getFullBoundsReference().getHeight() - VALUE_Y_MARGIN );
        _h3oBarNode.setOffset( xH3O, graphOutlineSize.getHeight() );
        _ohBarNode.setOffset( xOH, graphOutlineSize.getHeight() );
        _h2oBarNode.setOffset( xH2O, graphOutlineSize.getHeight() );
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setMoleculeLHS( Image image, String text, Color barColor ) {
        _xAxisNode.setMoleculeLHS( image, text );
        _lhsBarNode.setPaint( barColor );
    }
    
    public void setMoleculeRHS( Image image, String text, Color barColor ) {
        _xAxisNode.setMoleculeRHS( image, text );
        _lhsBarNode.setPaint( barColor );
    }
    
    public void setConcentrationLHS( double value ) {
        _lhsNumberNode.setValue( value );
        _lhsBarNode.setBarHeight( calculateBarHeight( value ) );
    }
    
    public void setConcentrationRHS( double value ) {
        _rhsNumberNode.setValue( value );
        _rhsBarNode.setBarHeight( calculateBarHeight( value ) );
    }
    
    public void setConcentrationH3O( double value ) {
        _h3oNumberNode.setValue( value );
        _h3oBarNode.setBarHeight( calculateBarHeight( value ) );
    }
    
    public void setConcentrationH2O( double value ) {
        _h2oNumberNode.setValue( value );
        _h2oBarNode.setBarHeight( calculateBarHeight( value ) );
    }
    
    public void setConcentrationOH( double value ) {
        _ohNumberNode.setValue( value );
        _ohBarNode.setBarHeight( calculateBarHeight( value ) );
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
