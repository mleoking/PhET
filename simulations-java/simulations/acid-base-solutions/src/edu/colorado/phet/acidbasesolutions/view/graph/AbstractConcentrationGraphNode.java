/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view.graph;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;

import edu.colorado.phet.acidbasesolutions.model.Molecule;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.TimesTenNumberFormat;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
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
 * Origin is at upper-left corner of chart's interior outline.
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
    private static final Stroke BAR_STROKE = null;
    private static final Color BAR_STROKE_COLOR = Color.BLACK;
    private static final double BAR_ARROW_HEAD_HEIGHT = 60;
    private static final double BAR_ARROW_PERCENT_ABOVE_GRAPH = 0.15;
    private static final double BAR_X_SPACING = 45; // chosen so that bars are (approximated) aligned with terms in reaction equation

    // numeric values
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
    private final ConcentrationNode[] concentrationNodes;
    private final ConcentrationYAxisNode yAxisNode;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public AbstractConcentrationGraphNode( PDimension outlineSize, boolean iconsVisible, boolean symbolsVisible ) {
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

        // nodes for each bar
        final double outlineHeight = outlineSize.getHeight();
        concentrationNodes = new ConcentrationNode[NUMBER_OF_BARS];
        for ( int i = 0; i < concentrationNodes.length; i++ ) {
            concentrationNodes[i] = new ConcentrationNode( outlineHeight, iconsVisible, symbolsVisible );
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
        
        private final double graphHeight;
        private final BarNode barNode;
        private final ValueNode valueNode;
        private final IconNode iconNode;
        private final SymbolNode symbolNode;
        
        public ConcentrationNode( double graphHeight, boolean iconVisible, boolean symbolVisible ) {
            this.graphHeight = graphHeight;
            
            barNode = new BarNode( BAR_WIDTH, graphHeight );
            addChild( barNode );
            
            valueNode = new ValueNode();
            addChild( valueNode );
            
            iconNode = new IconNode();
            iconNode.setVisible( iconVisible );
            addChild( iconNode );
            
            symbolNode = new SymbolNode();
            symbolNode.setVisible( symbolVisible );
            addChild( symbolNode );
        }
        
        public void setMolecule( Molecule molecule, NumberFormat format, boolean negligibleEnabled ) {
            barNode.setPaint( molecule.getColor() );
            valueNode.setFormat( format );
            valueNode.setNegligibleEnabled( negligibleEnabled, NEGLIGIBLE_THRESHOLD );
            valueNode.setNegligibleColor( molecule.getColor() );
            iconNode.setImage( molecule.getIcon() );
            symbolNode.setText( molecule.getSymbol() );
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
            final double maxTickHeight = graphHeight - TICKS_TOP_MARGIN;
            final double maxExponent = BIGGEST_TICK_EXPONENT;
            final double minExponent = BIGGEST_TICK_EXPONENT - NUMBER_OF_TICKS + 1;
            final double modelValueExponent = MathUtil.log10( modelValue );
            return maxTickHeight * ( modelValueExponent - minExponent ) / ( maxExponent - minExponent );
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
                // if the max width is exceeded, then rotate the symbol
                symbolNode.setRotation( 0 );
                if ( symbolNode.getFullBoundsReference().getWidth() > MAX_MOLECULE_LABEL_WIDTH ) {
                    symbolNode.setRotation( MOLECULE_LABEL_ROTATION_ANGLE );
                }
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
     * A bar, which turns into an upward-pointing arrow if 
     * the bar's height exceeds the height of the graph.
     */
    private static class BarNode extends PPath {
        
        private final double barWidth;
        private final double graphHeight;
        private final double arrowHeadWidth;
        private final GeneralPath barShape;
        
        public BarNode( double barWidth, double graphHeight ) {
            
            setPickable( false );
            setChildrenPickable( false );
            
            this.barWidth = barWidth;
            this.graphHeight = graphHeight;
            this.arrowHeadWidth = barWidth + 15;
            
            barShape = new GeneralPath();
            
            setPaint( DEFAULT_BAR_COLOR );
            setStroke( BAR_STROKE );
            setStrokePaint( BAR_STROKE_COLOR );
            
            setBarHeight( 1 );
        }
        
        public void setBarHeight( final double barHeight ) {
            
            barShape.reset();
            
            if ( barHeight > graphHeight ) {
                // rectangle with arrowhead at the top, origin at bottom center
                final double adjustedBarLength = graphHeight - ( ( 1 - BAR_ARROW_PERCENT_ABOVE_GRAPH ) * BAR_ARROW_HEAD_HEIGHT );
                barShape.moveTo( (float) -( barWidth / 2 ), 0f );
                barShape.lineTo( (float) ( barWidth / 2 ), 0f );
                barShape.lineTo( (float) ( barWidth / 2 ), (float) -adjustedBarLength );
                barShape.lineTo( (float) ( arrowHeadWidth / 2 ), (float) -adjustedBarLength );
                barShape.lineTo( 0f, (float) -( adjustedBarLength + BAR_ARROW_HEAD_HEIGHT ) );
                barShape.lineTo( (float) -( arrowHeadWidth / 2 ), (float) -adjustedBarLength );
                barShape.lineTo( (float) -( barWidth / 2 ), (float) -adjustedBarLength );
                barShape.closePath();
            }
            else if ( barHeight > 0 ) {
                // rectangle, origin at bottom center
                barShape.moveTo( (float) -( barWidth / 2 ), 0f );
                barShape.lineTo( (float) ( barWidth / 2 ), 0f );
                barShape.lineTo( (float) ( barWidth / 2 ), (float) -barHeight );
                barShape.lineTo( (float) -( barWidth / 2 ), (float) -barHeight );
                barShape.closePath();
            }
            
            setPathTo( barShape );
        }
    }

    /*
     * Values displayed on the bars.
     * Origin is at upper left of bounding rectangle.
     */
    private static class ValueNode extends NegligibleValueNode {

        public ValueNode() {
            super( 0, new TimesTenNumberFormat( "0.00" ) );
            rotate( -Math.PI / 2 );
        }
    }

    /*
     * Labels for the molecules.
     * Origin is at upper left of bounding rectangle.
     */
    private static class SymbolNode extends HTMLNode {

        public SymbolNode() {
            this( "" );
        }

        public SymbolNode( String text ) {
            super( HTMLUtils.toHTMLString( text ) );
            setFont( MOLECULE_LABEL_FONT );
        }

        public void setText( String text ) {
            setHTML( HTMLUtils.toHTMLString( text ) );
        }
    }

    /*
     * Icons for the molecules.
     * Origin is at upper left of bounding rectangle.
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
