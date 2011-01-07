// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.view.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;

import edu.colorado.phet.acidbasesolutions.model.Molecule;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for all concentration graphs, y-axis is log moles/L.
 * Has a max of 4 bars, knows nothing about the model.
 * Origin is at upper-left corner of the outline around the graph's data area.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */ abstract class AbstractConcentrationGraphNode extends PComposite {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // graph outline
    private static final Stroke OUTLINE_STROKE = new BasicStroke( 1f );
    protected static final Color OUTLINE_STROKE_COLOR = Color.BLACK;
    protected static final Color OUTLINE_FILL_COLOR = Color.WHITE;

    // bars
    private static final int NUMBER_OF_BARS = 4;
    private static final double BAR_WIDTH = 45;
    private static final double BAR_X_SPACING = 45; // chosen so that bars are (approximated) aligned with terms in reaction equation

    // numeric values
    private static final double NEGLIGIBLE_THRESHOLD = 0;

    // molecule symbols
    private static final Font SYMBOL_FONT = new PhetFont( 16 );

    // y ticks
    private static final int NUMBER_OF_TICKS = 11;
    private static final int BIGGEST_TICK_EXPONENT = 2;
    private static final int TICK_EXPONENT_DELTA = 1;
    private static final double TICKS_TOP_MARGIN = 10; // distance between top tick mark and top of graph's data area

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final PDimension outlineSize;
    private final PPath graphOutlineNode;
    private final ConcentrationNode[] concentrationNodes;
    private final ConcentrationYAxisNode yAxisNode;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * @param outlineSize size of the outline that goes around the data portion of the graph
     * @param iconsVisible are molecule icons visible on the x axis?
     * @param symbols are molecule symbols visible on the x axis?
     */
    public AbstractConcentrationGraphNode( PDimension outlineSize, boolean iconsVisible, boolean symbolsVisible ) {
        super();

        // not interactive
        setPickable( false );
        setChildrenPickable( false );

        // outline of graph's data area
        this.outlineSize = new PDimension( outlineSize );
        Rectangle2D r = new Rectangle2D.Double( 0, 0, outlineSize.getWidth(), outlineSize.getHeight() );
        graphOutlineNode = new PPath( r );
        graphOutlineNode.setStroke( OUTLINE_STROKE );
        graphOutlineNode.setStrokePaint( OUTLINE_STROKE_COLOR );
        graphOutlineNode.setPaint( OUTLINE_FILL_COLOR );
        addChild( graphOutlineNode );

        // y axis
        yAxisNode = new ConcentrationYAxisNode( outlineSize, NUMBER_OF_TICKS, TICKS_TOP_MARGIN, BIGGEST_TICK_EXPONENT, TICK_EXPONENT_DELTA );
        addChild( yAxisNode );

        // nodes for each bar
        double maxBarHeight = outlineSize.getHeight() - TICKS_TOP_MARGIN;
        concentrationNodes = new ConcentrationNode[NUMBER_OF_BARS];
        for ( int i = 0; i < concentrationNodes.length; i++ ) {
            concentrationNodes[i] = new ConcentrationNode( maxBarHeight, iconsVisible, symbolsVisible );
            addChild( concentrationNodes[i] );
        }

        // line along the bottom of the graph, where bars overlap the outline
        PPath bottomLineNode = new PPath( new Line2D.Double( 0, outlineSize.getHeight(), outlineSize.getWidth(), outlineSize.getHeight() ) );
        bottomLineNode.setStroke( OUTLINE_STROKE );
        bottomLineNode.setStrokePaint( OUTLINE_STROKE_COLOR );
        addChild( bottomLineNode );

        // layout
        // graph outline
        graphOutlineNode.setOffset( 0, 0 );
        // y axis, to left of graph
        yAxisNode.setOffset( graphOutlineNode.getOffset() );
        // bars
        updateLayout();
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------

    protected void setMolecule( int index, Molecule molecule, NumberFormat format ) {
        setMolecule( index, molecule, format, false /* negligibleEnabled */);
    }

    /*
     * Sets the properties for one of the bars in the graph.
     * The bars are numbered from left to right.
     *
     * @param index bar number
     * @param molecule molecule that determines the symbol, icon, and color associated with the bar
     * @param format format of the concentration value
     * @param negligibleEnabled whether to display "negligible" when concentration is below a threshold
     */
    protected void setMolecule( int index, Molecule molecule, NumberFormat format, boolean negligibleEnabled ) {
        concentrationNodes[index].setMolecule( molecule, format, negligibleEnabled );
    }

    protected void setConcentration( int index, double value ) {
        concentrationNodes[index].setConcentration( value );
    }

    protected void setVisible( int index, boolean visible ) {
        concentrationNodes[index].setVisible( visible );
    }

    protected void setAllVisible( boolean visible ) {
        for ( int i = 0; i < NUMBER_OF_BARS; i++ ) {
            setVisible( i, visible );
        }
    }

    //----------------------------------------------------------------------------
    // Layout of nodes
    //----------------------------------------------------------------------------

    protected void updateLayout() {

        // count the number of visible bars
        int visibleBars = 0;
        for ( ConcentrationNode node : concentrationNodes ) {
            if ( node.getVisible() ) {
                visibleBars++;
            }
        }

        if ( visibleBars > 0 ) {

            // determine the x margin
            double xMargin = ( outlineSize.getWidth() - ( visibleBars * BAR_WIDTH ) - ( ( visibleBars - 1 ) * BAR_X_SPACING ) ) / 2;
            assert ( xMargin >= 0 );

            // set offsets
            for ( int i = 0; i < concentrationNodes.length; i++ ) {
                double x = graphOutlineNode.getXOffset() + xMargin + ( i * ( BAR_X_SPACING + BAR_WIDTH ) ) + ( BAR_WIDTH / 2. );
                double y = outlineSize.getHeight();
                concentrationNodes[i].setOffset( x, y );
            }
        }
    }

    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------

    /*
     * Concentration representation, includes a bar, value, icon and symbol.
     */
    private static class ConcentrationNode extends PComposite {

        private final double maxBarHeight;
        private final BarNode barNode;
        private final NegligibleValueNode valueNode;
        private final PImage iconNode;
        private final HTMLNode symbolNode;

        public ConcentrationNode( double maxBarHeight, boolean iconVisible, boolean symbolVisible ) {
            this.maxBarHeight = maxBarHeight;

            barNode = new BarNode( BAR_WIDTH );
            addChild( barNode );

            valueNode = new NegligibleValueNode();
            valueNode.rotate( -Math.PI / 2 );
            addChild( valueNode );

            iconNode = new PImage();
            iconNode.setVisible( iconVisible );
            addChild( iconNode );

            symbolNode = new HTMLNode();
            symbolNode.setFont( SYMBOL_FONT );
            symbolNode.setVisible( symbolVisible );
            addChild( symbolNode );
        }

        public void setMolecule( Molecule molecule, NumberFormat format, boolean negligibleEnabled ) {
            barNode.setPaint( molecule.getColor() );
            valueNode.setFormat( format );
            valueNode.setNegligibleEnabled( negligibleEnabled, NEGLIGIBLE_THRESHOLD );
            iconNode.setImage( molecule.getImage() );
            symbolNode.setHTML( molecule.getSymbol() );
            updateLayout();
        }

        public void setConcentration( double value ) {
            valueNode.setValue( value );
            barNode.setBarHeight( calculateBarHeight( value ) );
        }

        /*
         * Calculates a bar height in view coordinates, given a model value.
         */
        private double calculateBarHeight( final double modelValue ) {
            final double maxExponent = BIGGEST_TICK_EXPONENT;
            final double minExponent = BIGGEST_TICK_EXPONENT - NUMBER_OF_TICKS + 1;
            final double modelValueExponent = MathUtil.log10( modelValue );
            return maxBarHeight * ( modelValueExponent - minExponent ) / ( maxExponent - minExponent );
        }

        private void updateLayout() {

            // bar node at origin
            double x = 0;
            double y = 0;
            barNode.setOffset( x, y );

            // value centered in bar
            x = barNode.getFullBoundsReference().getCenterX() - ( valueNode.getFullBoundsReference().getWidth() / 2 );
            y = barNode.getFullBoundsReference().getMaxY() - 10;
            valueNode.setOffset( x, y );

            // icon centered below bar
            if ( iconNode.getVisible() ) {
                x = barNode.getFullBoundsReference().getCenterX() - ( iconNode.getFullBoundsReference().getWidth() / 2 ); // careful! bar may have zero dimensions.
                y = barNode.getFullBoundsReference().getMaxY() + 10;
                iconNode.setOffset( x, y );
            }

            // symbol
            if ( symbolNode.getVisible() ) {
                if ( iconNode.getVisible() ) {
                    // centered below icon
                    x = iconNode.getFullBoundsReference().getCenterX() - ( symbolNode.getFullBoundsReference().getWidth() / 2 ) - PNodeLayoutUtils.getOriginXOffset( symbolNode );
                    y = iconNode.getFullBoundsReference().getMaxY() + 5;
                    symbolNode.setOffset( x, y );
                }
                else {
                    // centered below bar
                    x = barNode.getFullBoundsReference().getCenterX() - ( iconNode.getFullBoundsReference().getWidth() / 2 ); // careful! bar may have zero dimensions.
                    y = barNode.getFullBoundsReference().getMaxY() + 10;
                    symbolNode.setOffset( x, y );
                }
            }
        }
    }

    /*
     * Rectangular bar, height represents concentration.
     * Origin is at bottom center.
     */
    private static class BarNode extends PPath {

        private final double barWidth;
        private final Rectangle2D rectangle;

        public BarNode( double barWidth ) {
            this.barWidth = barWidth;
            this.rectangle = new Rectangle2D.Double();
            setPaint( Color.BLACK ); // default
            setStroke( null );
            setBarHeight( 1 );
        }

        public void setBarHeight( final double barHeight ) {
            rectangle.setRect( -( barWidth / 2 ), -barHeight, barWidth, barHeight );
            setPathTo( rectangle );
        }
    }
}
