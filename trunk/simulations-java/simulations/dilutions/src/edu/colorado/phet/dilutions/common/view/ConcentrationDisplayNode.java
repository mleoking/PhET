// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.common.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.util.ColorRange;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.dilutions.DilutionsResources.Strings;
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
public class ConcentrationDisplayNode extends PComposite {

    private static final PhetFont TITLE_FONT = new PhetFont( Font.BOLD, 16 );
    private static final PhetFont MIN_MAX_FONT = new PhetFont( 16 );

    public ConcentrationDisplayNode( String title, final PDimension barSize, final Solution solution, final DoubleRange concentrationRange ) {

        // this node is not interactive
        setPickable( false );
        setChildrenPickable( false );

        // nodes
        final PNode titleNode = new HTMLNode( title, Color.BLACK, TITLE_FONT );
        final BarNode barNode = new BarNode( barSize );
        final PointerNode pointerNode = new PointerNode( barSize, concentrationRange, solution.getConcentration() );
        final PNode maxNode = new PText( Strings.HIGH ) {{
            setFont( MIN_MAX_FONT );
        }};
        final PNode minNode = new PText( Strings.ZERO ) {{
            setFont( MIN_MAX_FONT );
        }};
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
            // max label centered above the bar
            maxNode.setOffset( barNode.getFullBoundsReference().getCenterX() - ( maxNode.getFullBoundsReference().getWidth() / 2 ),
                               barNode.getFullBoundsReference().getMinY() - maxNode.getFullBoundsReference().getHeight() - 3 );
            // min label centered below the bar
            minNode.setOffset( barNode.getFullBoundsReference().getCenterX() - ( minNode.getFullBoundsReference().getWidth() / 2 ),
                               barNode.getFullBoundsReference().getMaxY() + 3 );
            // title centered above max label
            titleNode.setOffset( barNode.getFullBounds().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 ),
                                 maxNode.getFullBoundsReference().getMinY() - titleNode.getFullBoundsReference().getHeight() - 8 );
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
        private final LinearFunction function;
        private PNode arrowNode;

        public PointerNode( PDimension barSize, DoubleRange range, double value ) {
            this.barSize = barSize;
            this.function = new LinearFunction( range.getMin(), range.getMax(), barSize.getHeight(), 0 );
            this.arrowNode = new PNode();
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
        }
    }
}
