/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view.graph;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.TimesTenNumberFormat;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/*
 * * Base class for all concentration graphs, y-axis is log moles/L. Has a max
 * of 4 bars, knows nothing about the model.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
abstract class AbstractConcentrationGraphNode extends PComposite {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final int NUMBER_OF_BARS = 4;

    // graph outline
    private static final Stroke OUTLINE_STROKE = new BasicStroke( 1f );
    private static final Color OUTLINE_STROKE_COLOR = Color.BLACK;
    private static final Color OUTLINE_FILL_COLOR = Color.WHITE;

    // bars
    private static final double BAR_WIDTH = 45;
    private static final Color DEFAULT_BAR_COLOR = Color.GRAY;

    // numeric values
    private static final Font VALUE_FONT = new PhetFont( Font.PLAIN, 18 );
    private static final double NEGLIGIBLE_THRESHOLD = 0;

    // molecule icons and labels
    private static final Font MOLECULE_LABEL_FONT = new PhetFont( 16 );
    private static final double MAX_MOLECULE_LABEL_WIDTH = BAR_WIDTH * 1.25;
    private static final double MOLECULE_LABEL_ROTATION_ANGLE = Math.PI / 4;
    private static final double MOLECULE_ICON_SCALE = 1.0;

    // y ticks
    private static final double TICK_LENGTH = 6;
    private static final Stroke TICK_STROKE = new BasicStroke( 1f );
    private static final Color TICK_COLOR = Color.BLACK;
    private static final Font TICK_LABEL_FONT = new PhetFont( 14 );
    private static final Color TICK_LABEL_COLOR = Color.BLACK;
    private static final double TICKS_TOP_MARGIN = 10;
    private static final int NUMBER_OF_TICKS = 11;
    private static final int BIGGEST_TICK_EXPONENT = 2;
    private static final int TICK_EXPONENT_SPACING = 1;

    // horizontal gridlines
    private static final Stroke GRIDLINE_STROKE = new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3, 3 }, 0 ); // dashed
    private static final Color GRIDLINE_COLOR = new Color( 192, 192, 192 ); // gray

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final PDimension outlineSize;
    private final PPath graphOutlineNode;
    private final ConcentrationBarNode[] barNodes;
    private final ValueNode[] valueNodes;
    private final IconNode[] iconNodes;
    private final LabelNode[] labelNodes;
    private final ConcentrationYAxisNode yAxisNode;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public AbstractConcentrationGraphNode( PDimension outlineSize, boolean xAxisLabeled ) {
        super();

        // not interactive
        setPickable( false );
        setChildrenPickable( false );

        this.outlineSize = new PDimension( outlineSize );

        // graphOutlineNode is not instance data because we do NOT want to use its bounds for calculations.
        // It's stroke width will cause calculation errors.  Use _graphOutlineSize instead.
        Rectangle2D r = new Rectangle2D.Double( 0, 0, outlineSize.getWidth(), outlineSize.getHeight() );
        graphOutlineNode = new PPath( r );
        graphOutlineNode.setStroke( OUTLINE_STROKE );
        graphOutlineNode.setStrokePaint( OUTLINE_STROKE_COLOR );
        graphOutlineNode.setPaint( OUTLINE_FILL_COLOR );
        addChild( graphOutlineNode );

        // y axis
        yAxisNode = new ConcentrationYAxisNode( outlineSize, NUMBER_OF_TICKS, TICKS_TOP_MARGIN, BIGGEST_TICK_EXPONENT, TICK_EXPONENT_SPACING, TICK_LENGTH, TICK_STROKE, TICK_COLOR, TICK_LABEL_FONT, TICK_LABEL_COLOR, GRIDLINE_STROKE, GRIDLINE_COLOR );
        addChild( yAxisNode );

        // bars
        final double outlineHeight = outlineSize.getHeight();
        barNodes = new ConcentrationBarNode[NUMBER_OF_BARS];
        for ( int i = 0; i < barNodes.length; i++ ) {
            barNodes[i] = new ConcentrationBarNode( BAR_WIDTH, DEFAULT_BAR_COLOR, outlineHeight );
            addChild( barNodes[i] );
        }

        // line along the bottom of the graph, where bars overlap the outline
        PPath bottomLineNode = new PPath( new Line2D.Double( 0, outlineSize.getHeight(), outlineSize.getWidth(), outlineSize.getHeight() ) );
        bottomLineNode.setStroke( OUTLINE_STROKE );
        bottomLineNode.setStrokePaint( OUTLINE_STROKE_COLOR );
        addChild( bottomLineNode );

        // values
        valueNodes = new ValueNode[NUMBER_OF_BARS];
        for ( int i = 0; i < valueNodes.length; i++ ) {
            valueNodes[i] = new ValueNode();
            addChild( valueNodes[i] );
        }

        // icons
        iconNodes = new IconNode[NUMBER_OF_BARS];
        for ( int i = 0; i < iconNodes.length; i++ ) {
            iconNodes[i] = new IconNode();
            if ( xAxisLabeled ) {
                addChild( iconNodes[i] );
            }
        }

        // labels
        labelNodes = new LabelNode[NUMBER_OF_BARS];
        for ( int i = 0; i < labelNodes.length; i++ ) {
            labelNodes[i] = new LabelNode();
            if ( xAxisLabeled ) {
                addChild( labelNodes[i] );
            }
        }

        // layout
        graphOutlineNode.setOffset( 0, 0 );
        PBounds gob = graphOutlineNode.getFullBoundsReference();
        // y axis, to left of graph
        yAxisNode.setOffset( graphOutlineNode.getOffset() );
        // bars, evenly distributed across graph width
        final double xSpacing = ( gob.getWidth() - ( barNodes.length * BAR_WIDTH ) ) / ( barNodes.length + 1 );
        assert ( xSpacing > 0 );
        for ( int i = 0; i < barNodes.length; i++ ) {
            double xOffset = graphOutlineNode.getXOffset() + xSpacing + ( i * ( xSpacing + BAR_WIDTH ) ) + ( BAR_WIDTH / 2. );
            double yOffset = outlineSize.getHeight();
            barNodes[i].setOffset( xOffset, yOffset );
        }
        // values, horizontally centered in bars
        for ( int i = 0; i < valueNodes.length; i++ ) {
            double xOffset = barNodes[i].getXOffset() + ( ( barNodes[i].getFullBoundsReference().getWidth() - valueNodes[i].getFullBoundsReference().getWidth() ) / 2 ) - ( BAR_WIDTH / 2 );
            double yOffset = gob.getMaxY() - 10;
            valueNodes[i].setOffset( xOffset, yOffset );
        }
        // layout of icons and labels will be handled when they are set
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------

    protected void setMolecule( int index, String symbol, Image image, Color barColor, NumberFormat format ) {
        setMolecule( index, symbol, image, barColor, format, false /* negligibleEnabled */);
    }

    protected void setMolecule( int index, String symbol, Image image, Color barColor, NumberFormat format, boolean negligibleEnabled ) {
        labelNodes[index].setText( symbol );
        iconNodes[index].setImage( image );
        barNodes[index].setPaint( barColor );
        valueNodes[index].setFormat( format );
        valueNodes[index].setNegligibleEnabled( negligibleEnabled, NEGLIGIBLE_THRESHOLD );
        updateMoleculeLayout( index );
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

    protected void setAllVisible( boolean visible ) {
        for ( int i = 0; i < NUMBER_OF_BARS; i++ ) {
            setVisible( i, visible );
        }
    }

    private void updateMoleculeLayout( int index ) {
        // icon, centered below bar
        IconNode iconNode = iconNodes[index];
        double xOffset = barNodes[index].getXOffset() - ( iconNode.getFullBoundsReference().getWidth() / 2 ); // careful! bar may have zero dimensions.
        double yOffset = graphOutlineNode.getFullBoundsReference().getMaxY() + 10;
        iconNode.setOffset( xOffset, yOffset );
        // label, centered below icon
        LabelNode labelNode = labelNodes[index];
        labelNode.setRotation( 0 );
        if ( labelNode.getFullBoundsReference().getWidth() > MAX_MOLECULE_LABEL_WIDTH ) {
            // if the max width is exceeded, then rotate the label
            labelNode.setRotation( MOLECULE_LABEL_ROTATION_ANGLE );
        }
        xOffset = iconNode.getFullBoundsReference().getCenterX() - ( labelNode.getFullBoundsReference().getWidth() / 2 ) - PNodeLayoutUtils.getOriginXOffset( labelNode );
        yOffset = iconNode.getFullBoundsReference().getMaxY() + 5;
        labelNode.setOffset( xOffset, yOffset );
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
    private static class ValueNode extends NegligibleValueNode {

        public ValueNode() {
            super( 0, new TimesTenNumberFormat( "0.00" ) );
            setFont( VALUE_FONT );
            rotate( -Math.PI / 2 );
        }
    }

    /*
     * Labels for the molecules.
     */
    private static class LabelNode extends HTMLNode {

        public LabelNode() {
            this( "" );
        }

        public LabelNode( String text ) {
            super( HTMLUtils.toHTMLString( text ) );
            setFont( MOLECULE_LABEL_FONT );
        }

        public void setText( String text ) {
            setHTML( HTMLUtils.toHTMLString( text ) );
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
