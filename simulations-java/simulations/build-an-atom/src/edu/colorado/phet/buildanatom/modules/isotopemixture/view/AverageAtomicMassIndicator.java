/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.modules.isotopemixture.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.util.List;

import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.buildanatom.modules.isotopemixture.model.IsotopeMixturesModel;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * A Piccolo2D node that monitors that average atomic mass of a set of
 * isotopes in a model and graphically displays it.
 *
 * @author John Blanco
 */
public class AverageAtomicMassIndicator extends PNode {

    private static double INDICATOR_WIDTH = 300; // In screen units, which is close to pixels.

    public AverageAtomicMassIndicator( final IsotopeMixturesModel model ){

        // Add the title.
        // TODO: i18n
        PText title = new PText("Average Atomic Mass"){{
            setFont( new PhetFont(20, true) );
        }};
        addChild( title );

        // Add the bar that makes up "spine" of the indicator.
        final double barOffsetY = title.getFullBoundsReference().getMaxY() + 40;
        DoubleGeneralPath barShape = new DoubleGeneralPath( 0, 0 );
        barShape.lineTo( INDICATOR_WIDTH, 0 );
        PNode barNode = new PhetPPath( barShape.getGeneralPath(), new BasicStroke(3), Color.BLACK ){{
            setOffset( 0, barOffsetY );
        }};
        addChild( barNode );

        // Add the layer where the tick marks will be maintained.
        final PNode tickMarkLayer = new PNode();
        addChild( tickMarkLayer );

        // Listen for changes to the list of possible isotopes and update the
        // tick marks when changes occur.
        model.getPossibleIsotopesProperty().addObserver( new SimpleObserver() {
            public void update() {
                tickMarkLayer.removeAllChildren();
                List< ImmutableAtom > possibleIsotopeList = model.getPossibleIsotopesProperty().getValue();
                double interTickSpacingX = INDICATOR_WIDTH / possibleIsotopeList.size();
                double tickMarkStartOffsetX = interTickSpacingX / 2;
                int tickMarkCount = 0;
                for ( ImmutableAtom isotope : model.getPossibleIsotopesProperty().getValue() ){
                    IsotopeTickMark tickMark = new IsotopeTickMark( isotope );
                    tickMark.setOffset( tickMarkStartOffsetX + interTickSpacingX * tickMarkCount, barOffsetY );
                    tickMarkCount++;
                    tickMarkLayer.addChild( tickMark );
                }
            }
        });

        // Add the moving readout.
        PNode readoutPointer = new ReadoutPointer( model );
        readoutPointer.setOffset( barNode.getFullBoundsReference().getCenterX(), barOffsetY + 20 );
        addChild( readoutPointer );

    }

    /**
     * Convenience class for creating tick marks.
     */
    private static class IsotopeTickMark extends PNode {

        // Constants that control overall appearance, tweak as needed.
        private static final double TICK_MARK_HEIGHT = 10;
        private static final Stroke TICK_MARK_STROKE = new BasicStroke( 5 );
        private static final Font LABEL_FONT = new PhetFont(18);

        public IsotopeTickMark( ImmutableAtom isotopeConfig ){
            // Create the tick mark itself.  It is positioned such that
            // (0,0) is the center of the mark.
            DoubleGeneralPath shape = new DoubleGeneralPath( 0, -TICK_MARK_HEIGHT / 2 );
            shape.lineTo( 0, TICK_MARK_HEIGHT / 2 );
            addChild( new PhetPPath(shape.getGeneralPath(), TICK_MARK_STROKE, Color.BLACK));
            // Create the label that goes above the tick mark.
            HTMLNode label = new HTMLNode( "<html><sup>" + isotopeConfig.getMassNumber() + "</sup>" + isotopeConfig.getSymbol() + "</html>" ){{
                setFont( LABEL_FONT );
                setOffset( -getFullBoundsReference().width / 2, -getFullBoundsReference().height - TICK_MARK_HEIGHT / 2 );
            }};
            addChild( label );
        }
    }

    /**
     * This class define the "readout pointer", which is an indicator
     * that contains a textual indication of the average atomic mass and
     * also has a pointer on the top that can be used to indicate the position
     * on a linear scale.
     *
     * This node is set up such that the (0,0) point is at the top center of
     * the node, which is where the point of the pointer exists.  This is done
     * to make it easy to position the node under the mass indication line.
     *
     * @author John Blanco
     */
    private static final class ReadoutPointer extends PNode {

        private static final Dimension2D SIZE = new PDimension( 90, 25 );
        private static final double TRIANGULAR_POINTER_HEIGHT = 15;
        private static final double TRIANGULAR_POINTER_WIDTH = 20;
        private static final DecimalFormat READOUT_FORMATTER = new DecimalFormat( "0.#####" );

        public ReadoutPointer ( final IsotopeMixturesModel model ){

            // Add the triangular pointer.  This is created such that the
            // point of the triangle is at (0,0) for this node.
            DoubleGeneralPath pointerShape = new DoubleGeneralPath(0, 0);
            pointerShape.lineTo( -TRIANGULAR_POINTER_WIDTH / 2, TRIANGULAR_POINTER_HEIGHT );
            pointerShape.lineTo( TRIANGULAR_POINTER_WIDTH / 2, TRIANGULAR_POINTER_HEIGHT );
            pointerShape.closePath();
            PNode triangularPointerNode = new PhetPPath( pointerShape.getGeneralPath(), new Color( 0, 143, 212 ) );
            addChild( triangularPointerNode );

            // Create the background for the readout.
            final PNode readoutBackgroundNode = new PhetPPath( new RoundRectangle2D.Double( -SIZE.getWidth() / 2, TRIANGULAR_POINTER_HEIGHT, SIZE.getWidth(), SIZE.getHeight(), 5, 5), Color.WHITE, new BasicStroke( 3 ), Color.BLACK );
            addChild( readoutBackgroundNode );

            // Add the textual readout.
            final PText textualReadout = new PText(){{
                setFont( new PhetFont( 12 ) );
            }};
            readoutBackgroundNode.addChild( textualReadout );
            // Observe the average atomic weight property in the model and
            // update the textual readout whenever it changes.
            model.getIsotopeTestChamber().getAverageAtomicMassProperty().addObserver( new SimpleObserver() {
                public void update() {
                    textualReadout.setText( READOUT_FORMATTER.format( model.getIsotopeTestChamber().getAverageAtomicMassProperty().getValue() ) );
                    textualReadout.centerFullBoundsOnPoint(
                            readoutBackgroundNode.getFullBoundsReference().getCenterX(),
                            readoutBackgroundNode.getFullBounds().getCenterY() );
                }
            });
        }
    }
}
