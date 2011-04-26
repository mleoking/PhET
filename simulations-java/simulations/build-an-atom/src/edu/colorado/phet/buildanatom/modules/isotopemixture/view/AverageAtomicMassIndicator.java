/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.modules.isotopemixture.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.AtomIdentifier;
import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.buildanatom.modules.isotopemixture.model.MixIsotopesModel;
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

    private double massSpan = 3; // In amu.
    private double minMass = 0; // In amu.

    public AverageAtomicMassIndicator( final MixIsotopesModel model ){
        // Root node onto which all other nodes are added.  This is done so
        // so that the root node can be offset at the end of construction in
        // such a way that the (0,0) location will be in the upper left corner.
        PNode rootNode = new PNode();
        addChild( rootNode );

        // Add the bar that makes up "spine" of the indicator.
        final double barOffsetY =  0;
        DoubleGeneralPath barShape = new DoubleGeneralPath( 0, 0 );
        barShape.lineTo( INDICATOR_WIDTH, 0 );
        final PNode barNode = new PhetPPath( barShape.getGeneralPath(), new BasicStroke(3), Color.BLACK );
        rootNode.addChild( barNode );

        // Add the layer where the tick marks will be maintained.
        final PNode tickMarkLayer = new PNode();
        rootNode.addChild( tickMarkLayer );

        // Listen for changes to the list of possible isotopes and update the
        // tick marks when changes occur.
        model.getPossibleIsotopesProperty().addObserver( new SimpleObserver() {
            public void update() {
                tickMarkLayer.removeAllChildren();
                List< ImmutableAtom > possibleIsotopeList = model.getPossibleIsotopesProperty().getValue();
                double lightestIsotopeMass = Double.POSITIVE_INFINITY;
                double heaviestIsotopeMass = 0;
                minMass = Double.POSITIVE_INFINITY;
                for ( ImmutableAtom isotope : possibleIsotopeList ){
                    if ( isotope.getAtomicMass() > heaviestIsotopeMass ) {
                        heaviestIsotopeMass = isotope.getAtomicMass();
                    }
                    if ( isotope.getAtomicMass() < lightestIsotopeMass ) {
                        lightestIsotopeMass = isotope.getAtomicMass();
                    }
                }
                massSpan = heaviestIsotopeMass - lightestIsotopeMass;
                if ( massSpan < 2 ){
                    massSpan = 2; // Mass span must be at least 2 or the spacing doesn't look good.
                }
                // Adjust the span so that there is some space at the ends of the line.
                massSpan *= 1.2;
                // Set the low end of the mass range, needed for positioning on line.
                minMass = (heaviestIsotopeMass + lightestIsotopeMass) / 2 - massSpan / 2;

                // Add the new tick marks.
                for ( ImmutableAtom isotope : model.getPossibleIsotopesProperty().getValue() ){
                    IsotopeTickMark tickMark = new IsotopeTickMark( isotope );
                    tickMark.setOffset( calcXOffsetFromAtomicMass( isotope.getAtomicMass() ), barOffsetY );
                    tickMarkLayer.addChild( tickMark );
                }
            }
        }, false );

        // Add the moving readout.
        final PNode readoutPointer = new ReadoutPointer( model );
        readoutPointer.setOffset( barNode.getFullBoundsReference().getCenterX(), barOffsetY + 20 );
        rootNode.addChild( readoutPointer );

        // Add a listener to position the moving readout in a location that
        // corresponds to the average atomic mass.
        model.getIsotopeTestChamber().addAverageAtomicMassPropertyListener( new SimpleObserver() {
            public void update() {
                if ( model.getIsotopeTestChamber().getTotalIsotopeCount() > 0 ){
                    readoutPointer.setOffset( calcXOffsetFromAtomicMass( model.getIsotopeTestChamber().getAverageAtomicMass() ), barOffsetY  );
                    readoutPointer.setVisible( true );
                }
                else{
                    readoutPointer.setVisible( false );
                }
            }
        });

        // Set the root node's offset so that the (0,0) location for this node
        // is in the upper left.
        rootNode.setOffset( 0, IsotopeTickMark.OVERALL_HEIGHT );
    }

    /**
     * Calculate the X offset on the bar given the atomic mass.  This is
     * clamped to never return a value less than 0.
     *
     * @param atomicMass
     * @return
     */
    private double calcXOffsetFromAtomicMass( double atomicMass ){
        return Math.max( ( atomicMass - minMass ) / massSpan * INDICATOR_WIDTH, 0 );
    }

    /**
     * Convenience class for creating tick marks.  This includes both the
     * actual mark and the label.
     */
    private static class IsotopeTickMark extends PNode {

        // Constants that control overall appearance, tweak as needed.
        public static final double OVERALL_HEIGHT = 40;
        private static final double TICK_MARK_LINE_HEIGHT = 10;
        private static final double TICK_MARK_LABEL_HEIGHT = OVERALL_HEIGHT - TICK_MARK_LINE_HEIGHT;
        private static final Stroke TICK_MARK_STROKE = new BasicStroke( 5 );

        public IsotopeTickMark( ImmutableAtom isotopeConfig ){
            // Create the tick mark itself.  It is positioned such that
            // (0,0) is the center of the mark.
            DoubleGeneralPath shape = new DoubleGeneralPath( 0, -TICK_MARK_LINE_HEIGHT / 2 );
            shape.lineTo( 0, TICK_MARK_LINE_HEIGHT / 2 );
            addChild( new PhetPPath(shape.getGeneralPath(), TICK_MARK_STROKE, Color.BLACK));
            // Create the label that goes above the tick mark.
            HTMLNode label = new HTMLNode( "<html><sup>" + isotopeConfig.getMassNumber() + "</sup>" + isotopeConfig.getSymbol() + "</html>" ){{
                setFont( new PhetFont( 14 ) );
                setScale( TICK_MARK_LABEL_HEIGHT / getFullBoundsReference().height );
                setOffset( -getFullBoundsReference().width / 2, -getFullBoundsReference().height - TICK_MARK_LINE_HEIGHT / 2 );
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

        private static final Dimension2D SIZE = new PDimension( 120, 25 );
        private static final double TRIANGULAR_POINTER_HEIGHT = 15;
        private static final double TRIANGULAR_POINTER_WIDTH = 20;
        private static final int DECIMAL_PLACES_FOR_USERS_MIX = 5;
        private final MixIsotopesModel model;
        private final PText textualReadout;
        private final PNode readoutBackgroundNode;

        public ReadoutPointer ( MixIsotopesModel model ){
            this.model = model;
            // Add the triangular pointer.  This is created such that the
            // point of the triangle is at (0,0) for this node.
            DoubleGeneralPath pointerShape = new DoubleGeneralPath(0, 0);
            pointerShape.lineTo( -TRIANGULAR_POINTER_WIDTH / 2, TRIANGULAR_POINTER_HEIGHT );
            pointerShape.lineTo( TRIANGULAR_POINTER_WIDTH / 2, TRIANGULAR_POINTER_HEIGHT );
            pointerShape.closePath();
            PNode triangularPointerNode = new PhetPPath( pointerShape.getGeneralPath(), new Color( 0, 143, 212 ) );
            addChild( triangularPointerNode );

            readoutBackgroundNode = new PhetPPath( new RoundRectangle2D.Double( -SIZE.getWidth() / 2,
                    TRIANGULAR_POINTER_HEIGHT, SIZE.getWidth(), SIZE.getHeight(), 5, 5), Color.WHITE, new BasicStroke( 1 ), Color.BLACK );
            addChild( readoutBackgroundNode );

            textualReadout = new PText(){{
                setFont( new PhetFont( 18 ) );
            }};
            addChild( textualReadout );

            // Observe the average atomic weight property in the model and
            // update the textual readout whenever it changes.
            model.getIsotopeTestChamber().addAverageAtomicMassPropertyListener( new SimpleObserver() {
                public void update() {
                    updateReadout();
                }
            });
            // Observe whether the user's mix or nature's mix is being
            // portrayed and update the readout when this changes.
            model.getShowingNaturesMixProperty().addObserver( new SimpleObserver() {
                public void update() {
                    updateReadout();
                }
            });
        }

        private void updateReadout(){
            double weight;
            int numDecimalPlacesToDisplay;
            if ( model.getShowingNaturesMixProperty().getValue() ){
                weight = AtomIdentifier.getStandardAtomicMassPrecionDecimal( model.getAtom().getNumProtons() ).getPreciseValue();
                numDecimalPlacesToDisplay = Math.min(
                        AtomIdentifier.getStandardAtomicMassPrecionDecimal( model.getAtom().getNumProtons() ).getNumberOfDecimalPlaces(),
                        5 ); // Max of 5 decimal places of resolution.
            }
            else{
                weight = model.getIsotopeTestChamber().getAverageAtomicMass();
                numDecimalPlacesToDisplay = DECIMAL_PLACES_FOR_USERS_MIX;
            }
            textualReadout.setText( VariablePrecisionNumberFormat.format( weight, numDecimalPlacesToDisplay ) + BuildAnAtomStrings.UNITS_AMU );
            textualReadout.setScale( 1 );
            if ( textualReadout.getFullBoundsReference().width >= readoutBackgroundNode.getFullBoundsReference().getWidth() * 0.95 ){
                textualReadout.setScale( readoutBackgroundNode.getFullBoundsReference().width / textualReadout.getFullBoundsReference().width * 0.95 );
            }
            textualReadout.centerFullBoundsOnPoint(
                    readoutBackgroundNode.getFullBoundsReference().getCenterX(),
                    readoutBackgroundNode.getFullBounds().getCenterY() );
        }
    }
}
