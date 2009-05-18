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
 * Base class for all concentration graphs, y-axis is log moles/L.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractConcentrationGraphNode extends PNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int NUMBER_OF_BARS = 5;
    
    protected static final int H3O_INDEX = 2;
    protected static final int OH_INDEX = 3;
    protected static final int H2O_INDEX = 4;
    
    // graph outline
    private static final Stroke OUTLINE_STROKE = new BasicStroke( 1f );
    private static final Color OUTLINE_STROKE_COLOR = Color.BLACK;
    private static final Color OUTLINE_FILL_COLOR = Color.WHITE;
    private static final PDimension DEFAULT_OUTLINE_SIZE = new PDimension( 350, 550 );
    
    // bars
    private static final double BAR_WIDTH = 40;
    private static final Color DEFAULT_BAR_COLOR = Color.GRAY;
    
    // numeric values
    private static final Font VALUE_FONT = new PhetFont( Font.BOLD, 18 );
    private static final Color VALUE_COLOR = Color.BLACK;
    private static final double VALUE_Y_MARGIN = 10;
    private static final TimesTenNumberFormat DEFAULT_VALUE_FORMAT = new TimesTenNumberFormat( "0.00" );
    private static final DecimalFormat H2O_FORMAT = new DefaultDecimalFormat( "#0" );
    
    // molecule icons and labels
    private static final Font MOLECULE_LABEL_FONT = new PhetFont( 18 );
    private static final double MOLECULE_ICON_SCALE = 0.25; //TODO: scale image files so that this is 1.0
    
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
    
    private final PDimension outlineSize;
    private final ConcentrationBarNode[] barNodes;
    private final ValueNode[] valueNodes;
    private final IconNode[] iconNodes;
    private final LabelNode[] labelNodes;
    private final ConcentrationYAxisNode yAxisNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AbstractConcentrationGraphNode() {
        this( DEFAULT_OUTLINE_SIZE );
    }
    
    public AbstractConcentrationGraphNode( PDimension outlineSize ) {
        
        this.outlineSize = new PDimension( outlineSize );
        
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
        yAxisNode = new ConcentrationYAxisNode( outlineSize, NUMBER_OF_TICKS, TICKS_TOP_MARGIN, 
                BIGGEST_TICK_EXPONENT,  TICK_EXPONENT_SPACING, TICK_LENGTH,
                TICK_STROKE, TICK_COLOR, TICK_LABEL_FONT, TICK_LABEL_COLOR, GRIDLINE_STROKE, GRIDLINE_COLOR );
        addChild( yAxisNode );
        
        // bars
        final double outlineHeight = outlineSize.getHeight();
        barNodes = new ConcentrationBarNode[ NUMBER_OF_BARS ];
        for ( int i = 0; i < barNodes.length; i++ ) {
            barNodes[i] = new ConcentrationBarNode( BAR_WIDTH, DEFAULT_BAR_COLOR, outlineHeight );
            addChild( barNodes[i] );
        }
        
        // line along the bottom of the graph, where bars overlap the outline
        PPath bottomLineNode = new PPath( new Line2D.Double( 0, outlineSize.getHeight(), outlineSize.getWidth(), outlineSize.getHeight() ) );
        bottomLineNode.setStroke( OUTLINE_STROKE );
        bottomLineNode.setStrokePaint( OUTLINE_STROKE_COLOR );
        bottomLineNode.setPickable( false );
        addChild( bottomLineNode );
        
        // values
        valueNodes = new ValueNode[ NUMBER_OF_BARS ];
        for ( int i = 0; i < valueNodes.length; i++ ) {
            valueNodes[i] = new ValueNode( DEFAULT_VALUE_FORMAT );
            addChild( valueNodes[i] );
        }
        
        // icons
        iconNodes = new IconNode[ NUMBER_OF_BARS ];
        for ( int i = 0; i < iconNodes.length; i++ ) {
            iconNodes[i] = new IconNode();
            addChild( iconNodes[i] );
        }
        
        // labels
        labelNodes = new LabelNode[ NUMBER_OF_BARS ];
        for ( int i = 0; i < labelNodes.length; i++ ) {
            labelNodes[i] = new LabelNode();
            addChild( labelNodes[i] );
        }
        
        // layout
        graphOutlineNode.setOffset( 0, 0 );
        PBounds gob = graphOutlineNode.getFullBoundsReference();
        // y axis, to left of graph
        yAxisNode.setOffset( graphOutlineNode.getOffset() );
        // bars, evenly distributed across graph width
        final double xSpacing = ( gob.getWidth() - ( barNodes.length * BAR_WIDTH ) ) / ( barNodes.length + 1 );
        assert( xSpacing > 0 );
        for ( int i = 0; i < barNodes.length; i++ ) {
            double xOffset = graphOutlineNode.getXOffset() + xSpacing + ( i * ( xSpacing + BAR_WIDTH ) ) + ( BAR_WIDTH / 2. );
            double yOffset = outlineSize.getHeight();
            barNodes[i].setOffset( xOffset, yOffset );
        }
        // values, horizontally centered in bars
        assert( valueNodes.length == barNodes.length );
        for ( int i = 0; i < valueNodes.length; i++ ) {
            double xOffset = barNodes[i].getXOffset() + ( ( barNodes[i].getFullBoundsReference().getWidth() - valueNodes[i].getFullBoundsReference().getWidth() ) / 2 ) - ( BAR_WIDTH / 2 );
            double yOffset = gob.getMaxY() - valueNodes[i].getFullBoundsReference().getHeight() - VALUE_Y_MARGIN;
            valueNodes[i].setOffset( xOffset, yOffset );
        }
        // icons, centered below bars
        assert( iconNodes.length == barNodes.length );
        double maxIconHeight = 0;
        for ( int i = 0; i < iconNodes.length; i++ ) {
            maxIconHeight = Math.max( maxIconHeight, iconNodes[i].getFullBoundsReference().getHeight() );
        }
        for ( int i = 0; i < iconNodes.length; i++ ) {
            double xOffset = barNodes[i].getXOffset() - ( iconNodes[i].getFullBoundsReference().getWidth() / 2 );
            double yOffset = gob.getMaxY() + ( ( maxIconHeight - iconNodes[i].getFullBoundsReference().getHeight() ) / 2 ) + 10;
            iconNodes[i].setOffset( xOffset, yOffset );
        }
        // labels, centered below icons
        assert( labelNodes.length == iconNodes.length );
        for ( int i = 0; i < labelNodes.length; i++ ) {
            double xOffset = iconNodes[i].getXOffset() + ( iconNodes[i].getFullBoundsReference().getWidth() - labelNodes[i].getFullBoundsReference().getWidth() ) / 2;
            double yOffset = iconNodes[i].getFullBoundsReference().getMaxY() + 5;
            labelNodes[i].setOffset( xOffset, yOffset );
        }

        setMolecule( H3O_INDEX, ABSImages.H3O_PLUS_MOLECULE, ABSSymbols.H3O_PLUS, ABSConstants.H3O_COLOR );
        setMolecule( OH_INDEX, ABSImages.OH_MINUS_MOLECULE, ABSSymbols.OH_MINUS, ABSConstants.OH_COLOR );
        setMolecule( H2O_INDEX, ABSImages.H2O_MOLECULE, ABSSymbols.H2O, ABSConstants.H2O_COLOR );
        setFormat( H2O_INDEX, H2O_FORMAT );
        
        //XXX remove this
        setConcentration( 0, 1E1 );
        setConcentration( 1, 1E0 );
        setConcentration( 2, 1E-1 );
        setConcentration( 3, 1E-2 );
        setConcentration( 4, 1E-3 );
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setH3OConcentration( double concentration ) {
        setConcentration( H3O_INDEX, concentration );
    }
    
    public void setOHConcentration( double concentration ) {
        setConcentration( OH_INDEX, concentration );
    }
    
    public void setH2OConcentration( double concentration ) {
        setConcentration( H2O_INDEX, concentration );
    }
    
    protected void setLabel( int index, String text ) {
        labelNodes[index].setText( text );
    }
    
    protected void setMolecule( int index, Image image, String text, Color barColor ) {
        iconNodes[index].setImage( image );
        labelNodes[index].setText( text );
        barNodes[index].setPaint( barColor );
    }
    
    protected void setConcentration( int index, double value ) {
        valueNodes[index].setValue( value );
        barNodes[index].setBarHeight( calculateBarHeight( value ) );
    }
    
    protected void setVisible( int index, boolean visible ) {
        barNodes[index].setVisible( visible );
        valueNodes[index].setVisible( visible );
        iconNodes[index].setVisible( visible );
        labelNodes[index].setVisible( visible );
    }
    
    protected void setFormat( int index, NumberFormat format ) {
        valueNodes[index].setFormat( format );
    }
    
    /*
     * Calculates a bar height in view coordinates, given a model value.
     */
    private double calculateBarHeight( final double modelValue ) {
        final double maxTickHeight = outlineSize.getHeight() - TICKS_TOP_MARGIN;
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
        
        public LabelNode() {
            this( "" );
        }
        
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
        
        public IconNode() {
            this( null );
        }
        
        public IconNode( Image image ) {
            super();
            imageNode = new PImage( image );
            addChild( imageNode );
            scale( MOLECULE_ICON_SCALE );
        }
        
        public void setImage( Image image ) {
            imageNode.setImage( image );
        }
    }
}
