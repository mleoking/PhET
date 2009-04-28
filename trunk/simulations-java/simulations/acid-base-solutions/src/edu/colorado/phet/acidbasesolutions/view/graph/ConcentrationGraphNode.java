/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view.graph;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.TimesTenNumberFormat;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.FormattedNumberNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * The Concentration graph, y-axis is log moles/L.
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
    private static final PDimension DEFAULT_OUTLINE_SIZE = new PDimension( 350, 550 );
    
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
    
    // molecule icons and labels
    private static final Font MOLECULE_LABEL_FONT = new PhetFont( 18 );
    
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
    
    // horizontal gridlines
    private static final Stroke GRIDLINE_STROKE = new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3,3}, 0 ); // dashed
    private static final Color GRIDLINE_COLOR = new Color(192, 192, 192, 100 ); // translucent gray
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final PDimension _outlineSize;
    private final ConcentrationBarNode _barLHS, _barRHS, _barH3O, _barOH, _barH2O;
    private final ValueNode _valueLHS, _valueRHS, _valueH3O, _valueOH, _valueH2O;
    private final IconNode _iconLHS, _iconRHS;
    private final LabelNode _labelLHS, _labelRHS;
    private final ConcentrationYAxisNode _yAxisNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ConcentrationGraphNode() {
        this( DEFAULT_OUTLINE_SIZE );
    }
    
    public ConcentrationGraphNode( PDimension outlineSize ) {
        
        _outlineSize = new PDimension( outlineSize );
        
        // graphOutlineNode is not instance data because we do NOT want to use its bounds for calculations.
        // It's stroke width will cause calculation errors.  Use _graphOutlineSize instead.
        Rectangle2D r = new Rectangle2D.Double( 0, 0, outlineSize.getWidth(), outlineSize.getHeight() );
        PPath graphOutlineNode = new PPath( r );
        graphOutlineNode.setStroke( OUTLINE_STROKE );
        graphOutlineNode.setStrokePaint( OUTLINE_STROKE_COLOR );
        graphOutlineNode.setPaint( OUTLINE_FILL_COLOR );
        graphOutlineNode.setPickable( false );
        graphOutlineNode.setChildrenPickable( false );
        addChild( graphOutlineNode );
        
        // y axis
        _yAxisNode = new ConcentrationYAxisNode( _outlineSize, NUMBER_OF_TICKS, TICKS_TOP_MARGIN, 
                BIGGEST_TICK_EXPONENT,  TICK_EXPONENT_SPACING, TICK_LENGTH,
                TICK_STROKE, TICK_COLOR, TICK_LABEL_FONT, TICK_LABEL_COLOR, GRIDLINE_STROKE, GRIDLINE_COLOR );
        addChild( _yAxisNode );
        
        // bars
        final double outlineHeight = _outlineSize.getHeight();
        _barLHS = new ConcentrationBarNode( BAR_WIDTH, LHS_BAR_COLOR, outlineHeight );
        _barRHS = new ConcentrationBarNode( BAR_WIDTH, RHS_BAR_COLOR, outlineHeight );
        _barH3O = new ConcentrationBarNode( BAR_WIDTH, H3O_BAR_COLOR, outlineHeight );
        _barOH = new ConcentrationBarNode( BAR_WIDTH, OH_BAR_COLOR, outlineHeight );
        _barH2O = new ConcentrationBarNode( BAR_WIDTH, H2O_BAR_COLOR, outlineHeight );
        PNode[] bars = { _barLHS, _barRHS, _barH3O, _barOH, _barH2O }; // left-to-right layout order
        for ( int i = 0; i < bars.length; i++ ) {
            addChild( bars[i] );
        }
        
        // line along the bottom of the graph, where bars overlap the outline
        PPath bottomLineNode = new PPath( new Line2D.Double( 0, _outlineSize.getHeight(), _outlineSize.getWidth(), _outlineSize.getHeight() ) );
        bottomLineNode.setStroke( OUTLINE_STROKE );
        bottomLineNode.setStrokePaint( OUTLINE_STROKE_COLOR );
        bottomLineNode.setPickable( false );
        addChild( bottomLineNode );
        
        // values
        _valueLHS = new ValueNode( LHS_FORMAT );
        _valueRHS = new ValueNode( RHS_FORMAT );
        _valueH3O = new ValueNode( H3O_FORMAT );
        _valueOH = new ValueNode( OH_FORMAT );
        _valueH2O = new ValueNode( H2O_FORMAT );
        PNode[] values = { _valueLHS, _valueRHS, _valueH3O, _valueOH, _valueH2O }; // left-to-right layout order
        for ( int i = 0; i < values.length; i++ ) {
            addChild( values[i] );
        }
        
        // icons
        _iconLHS = new IconNode( ABSImages.HA_MOLECULE );
        _iconRHS = new IconNode( ABSImages.A_MINUS_MOLECULE );
        IconNode iconH3O = new IconNode( ABSImages.H3O_PLUS_MOLECULE );
        IconNode iconOH = new IconNode( ABSImages.OH_MINUS_MOLECULE );
        IconNode iconH2O = new IconNode( ABSImages.H2O_MOLECULE );
        PNode[] icons = { _iconLHS, _iconRHS, iconH3O, iconOH, iconH2O };
        for ( int i = 0; i < icons.length; i++ ) {
            addChild( icons[i] );
        }
        
        // labels
        _labelLHS = new LabelNode( ABSSymbols.HA );
        _labelRHS = new LabelNode( ABSSymbols.A_MINUS );
        LabelNode labelH3O = new LabelNode( ABSSymbols.H3O_PLUS );
        LabelNode labelOH = new LabelNode( ABSSymbols.OH_MINUS );
        LabelNode labelH2O = new LabelNode( ABSSymbols.H2O );
        PNode[] labels = { _labelLHS, _labelRHS, labelH3O, labelOH, labelH2O };
        for ( int i = 0; i < labels.length; i++ ) {
            addChild( labels[i] );
        }
        
        // layout
        graphOutlineNode.setOffset( 0, 0 );
        PBounds gob = graphOutlineNode.getFullBoundsReference();
        // y axis, to left of graph
        _yAxisNode.setOffset( graphOutlineNode.getOffset() );
        // bars, evenly distributed across graph width
        final double xSpacing = ( gob.getWidth() - ( bars.length * BAR_WIDTH ) ) / ( bars.length + 1 );
        assert( xSpacing > 0 );
        for ( int i = 0; i < bars.length; i++ ) {
            double xOffset = graphOutlineNode.getXOffset() + xSpacing + ( i * ( xSpacing + BAR_WIDTH ) ) + ( BAR_WIDTH / 2. );
            double yOffset = _outlineSize.getHeight();
            bars[i].setOffset( xOffset, yOffset );
        }
        // values, horizontally centered in bars
        assert( values.length == bars.length );
        for ( int i = 0; i < values.length; i++ ) {
            double xOffset = bars[i].getXOffset() + ( ( bars[i].getFullBoundsReference().getWidth() - values[i].getFullBoundsReference().getWidth() ) / 2 ) - ( BAR_WIDTH / 2 );
            double yOffset = gob.getMaxY() - values[i].getFullBoundsReference().getHeight() - VALUE_Y_MARGIN;
            values[i].setOffset( xOffset, yOffset );
        }
        // icons, centered below bars
        assert( icons.length == bars.length );
        double maxIconHeight = 0;
        for ( int i = 0; i < icons.length; i++ ) {
            maxIconHeight = Math.max( maxIconHeight, icons[i].getFullBoundsReference().getHeight() );
        }
        for ( int i = 0; i < icons.length; i++ ) {
            double xOffset = bars[i].getXOffset() - ( icons[i].getFullBoundsReference().getWidth() / 2 );
            double yOffset = gob.getMaxY() + ( ( maxIconHeight - icons[i].getFullBoundsReference().getHeight() ) / 2 ) + 10;
            icons[i].setOffset( xOffset, yOffset );
        }
        // labels, centered below icons
        assert( labels.length == icons.length );
        for ( int i = 0; i < labels.length; i++ ) {
            double xOffset = icons[i].getXOffset() + ( icons[i].getFullBoundsReference().getWidth() - labels[i].getFullBoundsReference().getWidth() ) / 2;
            double yOffset = icons[i].getFullBoundsReference().getMaxY() + 5;
            labels[i].setOffset( xOffset, yOffset );
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
        //TODO adjust offsets of icon and label
        _iconLHS.setImage( image );
        _labelLHS.setText( text );
        _barLHS.setPaint( barColor );
    }
    
    public void setMoleculeRHS( Image image, String text, Color barColor ) {
        //TODO adjust offsets of icon and label
        _iconRHS.setImage( image );
        _labelRHS.setText( text );
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
        final double maxTickHeight = _outlineSize.getHeight() - TICKS_TOP_MARGIN;
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
    
    /*
     * Labels for the molecules.
     */
    private static class LabelNode extends PComposite {
        
        private HTMLNode htmlNode;
        
        public LabelNode( String text ) {
            super();
            htmlNode = new HTMLNode( HTMLUtils.toHTMLString( text ) );
            htmlNode.setFont( MOLECULE_LABEL_FONT );
            addChild( htmlNode );
        }
        
        public void setText( String text ) {
            htmlNode.setHTML( HTMLUtils.toHTMLString( text ) );
        }
    }
    
    /*
     * Icons for the molecules.
     */
    private static class IconNode extends PComposite {
        
        private PImage imageNode;
        
        public IconNode( Image image ) {
            super();
            imageNode = new PImage( image );
            addChild( imageNode );
            scale( 0.25 );//TODO scale image files
        }
        
        public void setImage( Image image ) {
            imageNode.setImage( image );
        }
    }
}
