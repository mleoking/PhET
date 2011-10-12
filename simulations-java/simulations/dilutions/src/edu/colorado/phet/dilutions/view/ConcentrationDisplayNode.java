// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.view;

import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
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
    private static final PhetFont LABEL_FONT = new PhetFont( 16 );

    public ConcentrationDisplayNode( final PDimension barSize, final Solution solution, DoubleRange range, Property<Boolean> valuesVisible ) {

        // this node is not interactive
        setPickable( false );
        setChildrenPickable( false );

        // nodes
        final TitleNode titleNode = new TitleNode();
        final BarNode barNode = new BarNode( barSize );
        final PointerNode pointerNode = new PointerNode( barSize, range, solution.getConcentration() );
        final ConcentrationValueNode maxValueNode = new ConcentrationValueNode( range.getMax() );
        final ConcentrationValueNode minValueNode = new ConcentrationValueNode( range.getMin() );
        final ConcentrationQualityNode maxQualityNode = new ConcentrationQualityNode( Strings.HIGH );
        final ConcentrationQualityNode minQualityNode = new ConcentrationQualityNode( Strings.LOW );

        // rendering order
        {
            addChild( titleNode );
            addChild( barNode );
            addChild( maxValueNode );
            addChild( minValueNode );
            addChild( maxQualityNode );
            addChild( minQualityNode );
            addChild( pointerNode );
        }

        // layout
        {
            // max labels centered above the bar
            maxValueNode.setOffset( barNode.getFullBoundsReference().getCenterX() - ( maxValueNode.getFullBoundsReference().getWidth() / 2 ),
                                    barNode.getFullBoundsReference().getMinY() - maxValueNode.getFullBoundsReference().getHeight() - 3 );
            maxQualityNode.setOffset( barNode.getFullBoundsReference().getCenterX() - ( maxQualityNode.getFullBoundsReference().getWidth() / 2 ),
                                      barNode.getFullBoundsReference().getMinY() - maxQualityNode.getFullBoundsReference().getHeight() - 3 );
            // min labels centered below the bar
            minValueNode.setOffset( barNode.getFullBoundsReference().getCenterX() - ( minValueNode.getFullBoundsReference().getWidth() / 2 ),
                                    barNode.getFullBoundsReference().getMaxY() + 3 );
            minQualityNode.setOffset( barNode.getFullBoundsReference().getCenterX() - ( minQualityNode.getFullBoundsReference().getWidth() / 2 ),
                                      barNode.getFullBoundsReference().getMaxY() + 3 );
            // title centered above max label
            titleNode.setOffset( barNode.getFullBounds().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 ),
                                 Math.min( maxValueNode.getFullBoundsReference().getMinY(), maxQualityNode.getFullBoundsReference().getMinY() ) - titleNode.getFullBoundsReference().getHeight() - 3 );
        }

        // Pointer position and value corresponds to the solution's concentration.
        solution.addConcentrationObserver( new SimpleObserver() {
            public void update() {
                pointerNode.setConcentration( solution.getConcentration() );
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
                maxValueNode.setVisible( visible );
                minValueNode.setVisible( visible );
                maxQualityNode.setVisible( !visible );
                minQualityNode.setVisible( !visible );
            }
        } );
    }

    private static class TitleNode extends HTMLNode {
        public TitleNode() {
            super( Strings.SOLUTION_CONCENTRATION );
            setFont( TITLE_FONT );
        }
    }

    // Vertical bar
    private static class BarNode extends PPath {
        public BarNode( final PDimension size ) {
            setPathTo( new Rectangle2D.Double( 0, 0, size.getWidth(), size.getHeight() ) );
        }
    }

    // Concentration values
    private static class ConcentrationValueNode extends PComposite {

        private final PText textNode;

        public ConcentrationValueNode( double concentration ) {
            textNode = new PText() {{
                setFont( LABEL_FONT );
            }};
            addChild( textNode );
            setValue( concentration );
        }

        // Converts concentration value to a string with units, with special treatment of zero.
        public void setValue( double concentration ) {
            String valueString = ( concentration == 0 ) ? "0" : new DecimalFormat( "0.00" ).format( concentration );
            textNode.setText( MessageFormat.format( Strings.PATTERN_0VALUE_1UNITS, valueString, Strings.UNITS_MOLARITY ) );
        }
    }

    // A qualitative concentration label
    private static class ConcentrationQualityNode extends PText {
        public ConcentrationQualityNode( String text ) {
            super( text );
            setFont( LABEL_FONT );
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
        private final ConcentrationValueNode valueNode;

        public PointerNode( PDimension barSize, DoubleRange range, double concentration ) {
            this.barSize = barSize;
            this.function = new LinearFunction( range.getMin(), range.getMax(), barSize.getHeight(), 0 );
            this.arrowNode = new PNode();
            this.valueNode = new ConcentrationValueNode( range.getMin() );
            addChild( valueNode );
            setConcentration( concentration );
        }

        public void setArrowPaint( Paint paint ) {
            arrowNode.setPaint( paint );
        }

        public void setConcentration( double concentration ) {

            // update the arrow
            double y = function.evaluate( concentration );
            Paint paint = arrowNode.getPaint();
            removeChild( arrowNode );
            arrowNode = new ArrowNode( new Point2D.Double( barSize.getWidth() + ARROW_LENGTH, y ),
                                       new Point2D.Double( barSize.getWidth(), y ),
                                       ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH );
            arrowNode.setPaint( paint );
            addChild( arrowNode );

            // update the value
            valueNode.setValue( concentration );
            valueNode.setOffset( arrowNode.getFullBoundsReference().getMaxX() + 3,
                                 arrowNode.getFullBoundsReference().getCenterY() - ( valueNode.getFullBoundsReference().getHeight() / 2 ) );
        }

        public void setValueVisible( boolean visible ) {
            valueNode.setVisible( visible );
        }
    }
}
