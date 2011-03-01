// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.module.introduction;

import java.awt.Color;
import java.awt.Dimension;

import edu.colorado.phet.balancingchemicalequations.BCEConstants;
import edu.colorado.phet.balancingchemicalequations.BCEGlobalProperties;
import edu.colorado.phet.balancingchemicalequations.control.BalancedRepresentationChoiceNode;
import edu.colorado.phet.balancingchemicalequations.control.BalancedRepresentationChoiceNode.BalancedRepresentation;
import edu.colorado.phet.balancingchemicalequations.control.EquationChoiceNode;
import edu.colorado.phet.balancingchemicalequations.view.*;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;

/**
 * Canvas for the "Balance Equation" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntroductionCanvas extends BCECanvas {

    private static final Dimension BOX_SIZE = new Dimension( 475, 220 );
    private static final double BOX_SEPARATION = 90;

    private final Property<BalancedRepresentation> balanceChoiceProperty; // determines the visual representation of "balanced"
    private final BoxesNode boxesNode;

    public IntroductionCanvas( final IntroductionModel model, final BCEGlobalProperties globalProperties, Resettable resettable ) {
        super( globalProperties.getCanvasColorProperty() );

        HorizontalAligner aligner = new HorizontalAligner( BOX_SIZE, BOX_SEPARATION );

        balanceChoiceProperty = new Property<BalancedRepresentation>( BalancedRepresentation.NONE );

        // control for choosing an equation
        EquationChoiceNode equationChoiceNode = new EquationChoiceNode( model.getEquations(), model.getCurrentEquationProperty(), globalProperties.getCanvasColorProperty() );
        addChild( equationChoiceNode );

        // equation, in formula format
        final EquationNode equationNode = new EquationNode( model.getCurrentEquationProperty(), model.getCoefficientsRange(), true /* editable */, aligner );
        addChild( equationNode );

        // boxes that show molecules corresponding to the equation coefficients
        boxesNode = new BoxesNode( model.getCurrentEquationProperty(), model.getCoefficientsRange(), aligner,
                globalProperties.getBoxColorProperty(), globalProperties.getMoleculesVisibleProperty() );
        addChild( boxesNode );

        // control for choosing the visual representation of "balanced"
        BalancedRepresentationChoiceNode balanceChoiceNode = new BalancedRepresentationChoiceNode( balanceChoiceProperty, globalProperties.getCanvasColorProperty() );
        addChild( balanceChoiceNode );

        // bar charts
        final BarChartsNode barChartsNode = new BarChartsNode( model.getCurrentEquationProperty(), aligner );
        addChild( barChartsNode );

        // balance scales
        final BalanceScalesNode balanceScalesNode = new BalanceScalesNode( model.getCurrentEquationProperty(), aligner );
        addChild( balanceScalesNode );

        // smiley face
        BalanceEquationFaceNode faceNode = new BalanceEquationFaceNode( model.getCurrentEquationProperty() );
        addChild( faceNode );

        // Reset All button
        final ResetAllButtonNode resetAllButtonNode = new ResetAllButtonNode( resettable, globalProperties.getFrame(), 12, Color.BLACK, Color.WHITE );
        resetAllButtonNode.scale( BCEConstants.SWING_SCALE );
        addChild( resetAllButtonNode );

        // Shows the answer (dev)
        final DevAnswerNode answerNode = new DevAnswerNode( model.getCurrentEquationProperty() );
        addChild( answerNode );
        answerNode.setVisible( globalProperties.getShowAnswersProperty().getValue() );

        /*
         * Layout - all of the major visual representations have x-offset=0,
         * and handle their own horizontal alignment via HorizontalAligner.
         */
        {
            double x, y;

            // equation choices above equation, right justified
            x = boxesNode.getFullBoundsReference().getMaxX() - equationChoiceNode.getFullBoundsReference().getWidth();
            equationChoiceNode.setOffset( x, 0 );

            // equation below choices
            y = equationChoiceNode.getFullBoundsReference().getMaxY() + 38;
            equationNode.setOffset( 0, y );

            // boxes below equation
            y = equationNode.getFullBoundsReference().getMaxY() + 22;
            boxesNode.setOffset( 0, y );

            // face node between boxes
            x = boxesNode.getFullBoundsReference().getCenterX() - ( faceNode.getFullBoundsReference().getWidth() / 2 );
            y = boxesNode.getFullBoundsReference().getMaxY() + 15;
            faceNode.setOffset( x, y );

            // bar charts and balance scales below boxes (centering is handle by nodes themselves)
            y = boxesNode.getFullBoundsReference().getMaxY() + 180;
            barChartsNode.setOffset( 0, y );
            y = barChartsNode.getYOffset() - 10;
            balanceScalesNode.setOffset( 0, y );

            // balance choices below bar charts and balance scales, left justified
            y = Math.max( barChartsNode.getFullBoundsReference().getMaxY(), balanceScalesNode.getFullBoundsReference().getMaxY() ) + 28;
            balanceChoiceNode.setOffset( 0, y );

            // Reset All button at bottom right
            x = boxesNode.getFullBoundsReference().getMaxX() - resetAllButtonNode.getFullBoundsReference().getWidth();
            y = balanceChoiceNode.getFullBoundsReference().getMinY();
            resetAllButtonNode.setOffset( x, y );

            // answer left-justified below boxes
            x = boxesNode.getFullBoundsReference().getMinX();
            y = boxesNode.getFullBoundsReference().getMaxY() + 4;
            answerNode.setOffset( x, y );
        }

        // observers
        balanceChoiceProperty.addObserver( new SimpleObserver() {
            public void update() {
                barChartsNode.setVisible( balanceChoiceProperty.getValue().equals( BalancedRepresentation.BAR_CHARTS ) );
                balanceScalesNode.setVisible( balanceChoiceProperty.getValue().equals( BalancedRepresentation.BALANCE_SCALES ) );
            }
        } );
        globalProperties.getShowAnswersProperty().addObserver( new SimpleObserver() {
            public void update() {
                answerNode.setVisible( globalProperties.getShowAnswersProperty().getValue() );
            }
        } );
    }

    public void reset() {
        balanceChoiceProperty.reset();
    }
}
