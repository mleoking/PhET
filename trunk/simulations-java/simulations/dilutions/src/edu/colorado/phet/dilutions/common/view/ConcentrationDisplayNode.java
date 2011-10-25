// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.common.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.dilutions.DilutionsColors;
import edu.colorado.phet.dilutions.DilutionsResources.Strings;
import edu.colorado.phet.dilutions.common.model.Solution;
import edu.colorado.phet.dilutions.common.util.SmartDoubleFormat;
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
    private static final PhetFont TICK_FONT = new PhetFont( 16 );
    private static final double TICK_LENGTH = 10;
    private static final SmartDoubleFormat TICK_FORMAT = new SmartDoubleFormat( "0.00", true, true );
    private static final SmartDoubleFormat VALUE_FORMAT = new SmartDoubleFormat( "0.00", false, false );

    public ConcentrationDisplayNode( String title, final PDimension barSize, final Solution solution, final DoubleRange concentrationRange, Property<Boolean> valuesVisible ) {

        // this node is not interactive
        setPickable( false );
        setChildrenPickable( false );

        // nodes
        final TitleNode titleNode = new TitleNode( title );
        final BarNode barNode = new BarNode( barSize );
        final PointerNode pointerNode = new PointerNode( barSize, concentrationRange, solution.getConcentration() );
        final TickMarkNode maxNode = new TickMarkNode( concentrationRange.getMax(), Strings.UNITS_MOLARITY, Strings.HIGH, TICK_FONT, TICK_LENGTH, TICK_FORMAT );
        final TickMarkNode minNode = new TickMarkNode( concentrationRange.getMin(), Strings.UNITS_MOLARITY, Strings.LOW, TICK_FONT, TICK_LENGTH, TICK_FORMAT );
        final SaturationIndicatorNode saturationIndicatorNode = new SaturationIndicatorNode( barSize, solution.getSaturatedConcentration(), concentrationRange.getMax() );

        // rendering order
        {
            addChild( titleNode );
            addChild( barNode );
            addChild( maxNode );
            addChild( minNode );
            addChild( pointerNode );
            addChild( saturationIndicatorNode );
        }

        // layout
        {
            // max label at top of bar
            maxNode.setOffset( 0, 0 );
            // min label at bottom of bar
            minNode.setOffset( 0, barSize.getHeight() );
            // title centered above subtitle
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
                Paint paint = createGradient( solution.solute.get().solutionColor, barSize.getHeight(), solution.getSaturatedConcentration(), concentrationRange.getMax() );
                barNode.setPaint( paint );
                pointerNode.setArrowPaint( paint );
                saturationIndicatorNode.setSaturationPoint( solution.getSaturatedConcentration() );
                saturationIndicatorNode.setVisible( solution.getSaturatedConcentration() < concentrationRange.getMax() );
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

    // Creates a gradient for the bar and pointer, taking into account the saturation point
    private static final GradientPaint createGradient( Color soluteColor, double barHeight, double saturatedConcentration, double maxConcentration ) {
        double y = barHeight - ( barHeight * ( saturatedConcentration / maxConcentration ) );
        return new GradientPaint( 0f, (float) y, soluteColor, 0f, (float) barHeight, DilutionsColors.WATER_COLOR );
    }

    // Title above the bar
    private static class TitleNode extends HTMLNode {
        public TitleNode( String title ) {
            super( title );
            setFont( TITLE_FONT );
        }
    }

    // Vertical bar. Origin at upper left.
    private static class BarNode extends PPath {
        public BarNode( final PDimension size ) {
            setPathTo( new Rectangle2D.Double( 0, 0, size.getWidth(), size.getHeight() ) );
        }
    }

    // A gray bar above the saturation point, drawn in the coordinate frame of the bar.
    private static class SaturationIndicatorNode extends PPath {

        private PDimension barSize;
        private double maxConcentration;

        public SaturationIndicatorNode( PDimension barSize, double saturationPoint, double maxConcentration ) {
            this.barSize = barSize;
            this.maxConcentration = maxConcentration;
            setPaint( Color.LIGHT_GRAY );
            setSaturationPoint( saturationPoint );
        }

        public void setSaturationPoint( double saturationPoint ) {
            double height = barSize.getHeight() - ( barSize.getHeight() * ( saturationPoint / maxConcentration ) );
            setPathTo( new Rectangle2D.Double( 0, 0, barSize.getWidth(), height ) );
        }
    }

    // Arrow with a value next to it, drawn in the coordinate frame of the bar to simplifying filling with a gradient paint.
    private static class PointerNode extends PComposite {

        private static final int ARROW_LENGTH = 35;
        private static final int ARROW_HEAD_HEIGHT = 25;
        private static final int ARROW_HEAD_WIDTH = 25;
        private static final int ARROW_TAIL_WIDTH = 13;

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
            String valueString = MessageFormat.format( Strings.PATTERN_0VALUE_1UNITS, VALUE_FORMAT.format( value ), Strings.UNITS_MOLARITY );
            valueNode.setText( valueString );
            valueNode.setOffset( arrowNode.getFullBoundsReference().getMaxX() + 3,
                                 arrowNode.getFullBoundsReference().getCenterY() - ( valueNode.getFullBoundsReference().getHeight() / 2 ) );
        }

        public void setValueVisible( boolean visible ) {
            valueNode.setVisible( visible );
        }
    }
}
