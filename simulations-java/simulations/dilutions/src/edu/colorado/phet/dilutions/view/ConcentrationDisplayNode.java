// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.view;

import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.dilutions.DilutionsColors;
import edu.colorado.phet.dilutions.DilutionsResources.Strings;
import edu.colorado.phet.dilutions.model.Solution;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Vertical bar that displays the concentration of a solution.
 * The bar is colored using a gradient that corresponds to the solute's color.
 * A pointer to the right of the bar indicates the concentration value.
 * The pointer is color corresponds to its location on the bar.
 * Origin is at the upper-left corner of the bar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationDisplayNode extends PComposite {

    private static final PhetFont TITLE_FONT = new PhetFont( Font.BOLD, 16 );
    private static final PhetFont VALUE_FONT = new PhetFont( 16 );
    private static final PhetFont LABEL_FONT = new PhetFont( 16 );
    private static final double TICK_LENGTH = 10;
    private static final NumberFormat TICK_FORMAT = new DefaultDecimalFormat( "0.00" );
    private static final NumberFormat VALUE_FORMAT = new DefaultDecimalFormat( "0.00" );

    public ConcentrationDisplayNode( final PDimension barSize, final Solution solution, DoubleRange range, Property<Boolean> valuesVisible ) {

        // this node is not interactive
        setPickable( false );
        setChildrenPickable( false );

        // nodes
        final TitleNode titleNode = new TitleNode();
        final BarNode barNode = new BarNode( barSize );
        final PointerNode pointerNode = new PointerNode( barSize, range, solution.getConcentration() );
        final TickMarkNode maxNode = new TickMarkNode( range.getMax(), Strings.HIGH );
        final TickMarkNode minNode = new TickMarkNode( range.getMin(), Strings.LOW );

        // rendering order
        {
            addChild( titleNode );
            addChild( barNode );
            addChild( maxNode );
            addChild( minNode );
            addChild( pointerNode );
        }

        // layout
        {
            // max label at top of bar
            maxNode.setOffset( 0, 0 );
            // min label at bottom of bar
            minNode.setOffset( 0, barSize.getHeight() );
            // title centered above bar
            titleNode.setOffset( barNode.getFullBounds().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 ),
                                 -titleNode.getFullBoundsReference().getHeight() - ( maxNode.getFullBoundsReference().getHeight() / 2 ) - 3 );
        }

        // Pointer position and value corresponds to the solution's concentration.
        solution.addConcentrationObserver( new SimpleObserver() {
            public void update() {
                pointerNode.setValue( solution.getConcentration() );
            }
        } );

        // Color the bar and pointer using a gradient that corresponds to the solute's color.
        solution.solute.addObserver( new SimpleObserver() {
            public void update() {
                Paint paint = new GradientPaint( 0f, 0f, solution.solute.get().solutionColor,
                                                 0f, (float) barSize.getHeight(), DilutionsColors.WATER_COLOR );
                barNode.setPaint( paint );
                pointerNode.setArrowPaint( paint );
            }
        } );

        // Show values
        valuesVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                pointerNode.setValueVisible( visible );
                maxNode.setValueVisible( visible );
                minNode.setValueVisible( visible );
            }
        } );
    }

    private static class TitleNode extends HTMLNode {
        public TitleNode() {
            super( Strings.CONCENTRATION_MOLARITY );
            setFont( TITLE_FONT );
        }
    }

    // Vertical bar. Origin at upper left.
    private static class BarNode extends PPath {
        public BarNode( final PDimension size ) {
            setPathTo( new Rectangle2D.Double( 0, 0, size.getWidth(), size.getHeight() ) );
        }
    }

    // Tick mark, a horizontal line with a label to the left of it.  Origin at right center to simplify layout.
    private static final class TickMarkNode extends PComposite {

        private final PNode valueNode, qualityNode;

        public TickMarkNode( double value, String qualityLabel ) {

            // nodes
            PNode tickNode = new PPath( new Line2D.Double( -TICK_LENGTH, 0, 0, 0 ) );
            valueNode = new PText( valueToString( value ) ) {{
                setFont( LABEL_FONT );
            }};
            qualityNode = new PText( qualityLabel ) {{
                setFont( LABEL_FONT );
            }};

            // rendering order
            addChild( tickNode );
            addChild( valueNode );
            addChild( qualityNode );

            // layout
            valueNode.setOffset( tickNode.getFullBoundsReference().getMinX() - valueNode.getFullBoundsReference().getWidth() - 2,
                                 -( valueNode.getFullBoundsReference().getHeight() / 2 ) );
            qualityNode.setOffset( tickNode.getFullBoundsReference().getMinX() - qualityNode.getFullBoundsReference().getWidth() - 2,
                                   -( qualityNode.getFullBoundsReference().getHeight() / 2 ) );
        }

        public void setValueVisible( boolean visible ) {
            valueNode.setVisible( visible );
            qualityNode.setVisible( !visible );
        }

        // Converts value to a string, with special treatment for integers.
        private String valueToString( double value ) {
            String valueString = ( value % 1 == 0 ) ? String.valueOf( (int) value ) : TICK_FORMAT.format( value );
            return MessageFormat.format( Strings.PATTERN_0VALUE_1UNITS, valueString, Strings.UNITS_MOLARITY );
        }
    }

    // Arrow with a value next to it, drawn in the coordinate frame of the bar to simplifying filling with a gradient paint.
    private static class PointerNode extends PComposite {

        private static final int ARROW_LENGTH = 28;
        private static final int ARROW_HEAD_HEIGHT = 20;
        private static final int ARROW_HEAD_WIDTH = 20;
        private static final int ARROW_TAIL_WIDTH = 10;

        private final PDimension barSize;
        private final LinearFunction function;
        private PNode arrowNode;
        private final PText valueNode;

        public PointerNode( PDimension barSize, DoubleRange range, double value ) {
            this.barSize = barSize;
            this.function = new LinearFunction( range.getMin(), range.getMax(), barSize.getHeight(), 0 );
            this.arrowNode = new PNode();
            this.valueNode = new PText() {{
                setFont( VALUE_FONT );
            }};
            addChild( valueNode );
            setValue( value );
        }

        public void setArrowPaint( Paint paint ) {
            arrowNode.setPaint( paint );
        }

        public void setValue( double value ) {

            // update the arrow
            double y = function.evaluate( value );
            Paint paint = arrowNode.getPaint();
            removeChild( arrowNode );
            arrowNode = new ArrowNode( new Point2D.Double( barSize.getWidth() + ARROW_LENGTH, y ),
                                       new Point2D.Double( barSize.getWidth(), y ),
                                       ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH );
            arrowNode.setPaint( paint );
            addChild( arrowNode );

            // update the value
            valueNode.setText( valueToString( value ) );
            valueNode.setOffset( arrowNode.getFullBoundsReference().getMaxX() + 3,
                                 arrowNode.getFullBoundsReference().getCenterY() - ( valueNode.getFullBoundsReference().getHeight() / 2 ) );
        }

        public void setValueVisible( boolean visible ) {
            valueNode.setVisible( visible );
        }

        // Converts value to a string, with special treatment for "0".
        private static String valueToString( double concentration ) {
            String valueString = ( concentration == 0 ) ? "0" : new DecimalFormat( "0.00" ).format( concentration );
            return MessageFormat.format( Strings.PATTERN_0VALUE_1UNITS, valueString, Strings.UNITS_MOLARITY );
        }
    }
}
