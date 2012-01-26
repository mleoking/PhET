// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.common.view;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.ColorRange;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.simsharing.NonInteractiveEventHandler;
import edu.colorado.phet.dilutions.MolarityConstants;
import edu.colorado.phet.dilutions.MolarityResources.Strings;
import edu.colorado.phet.dilutions.MolaritySimSharing.UserComponents;
import edu.colorado.phet.dilutions.common.model.Solution;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Vertical bar that displays the concentration of a solution.
 * The bar is colored using a gradient that corresponds to the solute's color.
 * A pointer to the right of the bar indicates the concentration on the scale.
 * The pointer is color corresponds to its location on the bar.
 * Origin is at the upper-left corner of the bar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationDisplayNode extends PNode {

    public ConcentrationDisplayNode( String title, String subtitle,
                                     final PDimension barSize,
                                     final Solution solution,
                                     final DoubleRange concentrationRange, String concentrationUnits,
                                     Property<Boolean> valuesVisible ) {
        // nodes
        final PNode titleNode = new HTMLNode( title, Color.BLACK, MolarityConstants.SLIDER_TITLE_FONT );
        final PNode subtitleNode = new HTMLNode( subtitle, Color.BLACK, MolarityConstants.SLIDER_SUBTITLE_FONT );
        final BarNode barNode = new BarNode( barSize );
        final PointerNode pointerNode = new PointerNode( barSize, concentrationRange, solution.getConcentration(), concentrationUnits, valuesVisible );
        final PNode maxNode = new DualLabelNode( MolarityConstants.RANGE_FORMAT.format( concentrationRange.getMax() ), Strings.HIGH, valuesVisible, MolarityConstants.SLIDER_RANGE_FONT );
        final PNode minNode = new DualLabelNode( MolarityConstants.RANGE_FORMAT.format( concentrationRange.getMin() ), Strings.ZERO, valuesVisible, MolarityConstants.SLIDER_RANGE_FONT );
        final SaturationIndicatorNode saturationIndicatorNode = new SaturationIndicatorNode( barSize, solution.getSaturatedConcentration(), concentrationRange.getMax() );

        // rendering order
        {
            addChild( titleNode );
            addChild( subtitleNode );
            addChild( barNode );
            addChild( maxNode );
            addChild( minNode );
            addChild( pointerNode );
            addChild( saturationIndicatorNode );
        }

        // layout
        {
            // max label centered above the bar
            maxNode.setOffset( barNode.getFullBoundsReference().getCenterX(),
                               barNode.getFullBoundsReference().getMinY() - maxNode.getFullBoundsReference().getHeight() - 3 );
            // min label centered below the bar
            minNode.setOffset( barNode.getFullBoundsReference().getCenterX(),
                               barNode.getFullBoundsReference().getMaxY() + 3 );
            // subtitle centered above max label
            subtitleNode.setOffset( barNode.getFullBounds().getCenterX() - ( subtitleNode.getFullBoundsReference().getWidth() / 2 ),
                                    maxNode.getFullBoundsReference().getMinY() - subtitleNode.getFullBoundsReference().getHeight() - 8 );
            // title centered above subtitle
            titleNode.setOffset( barNode.getFullBounds().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 ),
                                 subtitleNode.getFullBoundsReference().getMinY() - titleNode.getFullBoundsReference().getHeight() - 2 );
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

        // sim-sharing non-interactive components that we think the user might try to interact with
        barNode.addInputEventListener( new NonInteractiveEventHandler( UserComponents.concentrationBar ) );
        pointerNode.addInputEventListener( new NonInteractiveEventHandler( UserComponents.concentrationPointer ) );
    }

    // Creates a gradient for the bar and pointer, taking into account the saturation point
    private static final GradientPaint createGradient( ColorRange soluteColor, double barHeight, double saturatedConcentration, double maxConcentration ) {
        double y = barHeight - ( barHeight * ( saturatedConcentration / maxConcentration ) );
        return new GradientPaint( 0f, (float) y, soluteColor.getMax(), 0f, (float) barHeight, soluteColor.getMin() );
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
        private final String units;
        private final LinearFunction function;
        private PNode arrowNode;
        private final PText valueNode;

        public PointerNode( PDimension barSize, DoubleRange range, double value, String units, Property<Boolean> valuesVisible ) {
            this.barSize = barSize;
            this.units = units;
            this.function = new LinearFunction( range.getMin(), range.getMax(), barSize.getHeight(), 0 );
            this.arrowNode = new PNode();
            this.valueNode = new PText() {{
                setFont( MolarityConstants.SLIDER_VALUE_FONT );
            }};
            addChild( valueNode );
            setValue( value );

            valuesVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    valueNode.setVisible( visible );
                }
            } );
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
            String valueString = MessageFormat.format( Strings.PATTERN_0VALUE_1UNITS, MolarityConstants.VALUE_FORMAT.format( value ), units );
            valueNode.setText( valueString );
            valueNode.setOffset( arrowNode.getFullBoundsReference().getMaxX() + 3,
                                 arrowNode.getFullBoundsReference().getCenterY() - ( valueNode.getFullBoundsReference().getHeight() / 2 ) );
        }
    }
}
