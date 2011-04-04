// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.module.introduction;

import java.awt.Color;
import java.awt.Dimension;

import edu.colorado.phet.balancingchemicalequations.BCEConstants;
import edu.colorado.phet.balancingchemicalequations.BCEGlobalProperties;
import edu.colorado.phet.balancingchemicalequations.control.BalancedRepresentationChoiceNode;
import edu.colorado.phet.balancingchemicalequations.control.EquationChoiceNode;
import edu.colorado.phet.balancingchemicalequations.view.*;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;

/**
 * Canvas for the "Introduction" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */ class IntroductionCanvas extends BCECanvas {

    private static final Dimension BOX_SIZE = new Dimension( 475, 220 );
    private static final double BOX_SEPARATION = 90;

    private final Property<BalancedRepresentation> balanceChoiceProperty; // determines the visual representation of "balanced"
    private final BoxesNode boxesNode;

    public IntroductionCanvas( final IntroductionModel model, final BCEGlobalProperties globalProperties, Resettable resettable ) {
        super( globalProperties.canvasColor );

        HorizontalAligner aligner = new HorizontalAligner( BOX_SIZE, BOX_SEPARATION );

        balanceChoiceProperty = new Property<BalancedRepresentation>( BalancedRepresentation.NONE );

        // control for choosing an equation
        EquationChoiceNode equationChoiceNode = new EquationChoiceNode( model.getEquations(), model.currentEquation, globalProperties.canvasColor );
        addChild( equationChoiceNode );

        // equation, in formula format
        final EquationNode equationNode = new EquationNode( model.currentEquation, model.getCoefficientsRange(), aligner );
        addChild( equationNode );

        // boxes that show molecules corresponding to the equation coefficients
        boxesNode = new BoxesNode( model.currentEquation, model.getCoefficientsRange(), aligner,
                globalProperties.boxColor, globalProperties.moleculesVisible );
        addChild( boxesNode );

        // control for choosing the visual representation of "balanced"
        BalancedRepresentationChoiceNode balanceChoiceNode = new BalancedRepresentationChoiceNode( balanceChoiceProperty, globalProperties.canvasColor );
        addChild( balanceChoiceNode );

        // bar charts
        final BarChartsNode barChartsNode = new BarChartsNode( model.currentEquation, aligner );
        addChild( barChartsNode );

        // balance scales
        final BalanceScalesNode balanceScalesNode = new BalanceScalesNode( model.currentEquation, aligner );
        addChild( balanceScalesNode );

        // smiley face
        IntroductionFaceNode faceNode = new IntroductionFaceNode( model.currentEquation );
        addChild( faceNode );

        // Reset All button
        final ResetAllButtonNode resetAllButtonNode = new ResetAllButtonNode( resettable, globalProperties.frame, 12, Color.BLACK, Color.WHITE );
        resetAllButtonNode.scale( BCEConstants.SWING_SCALE );
        addChild( resetAllButtonNode );

        // Shows the answer (dev)
        final DevAnswerNode answerNode = new DevAnswerNode( model.currentEquation );
        addChild( answerNode );
        answerNode.setVisible( globalProperties.answersVisible.getValue() );

        /*
         * Layout - all of the major visual representations have x-offset=0,
         * and handle their own horizontal alignment via HorizontalAligner.
         */
        {
            double ySpacing = 35;
            double x, y;

            // equation choices above equation, right justified
            x = boxesNode.getFullBoundsReference().getMaxX() - equationChoiceNode.getFullBoundsReference().getWidth();
            equationChoiceNode.setOffset( x, 0 );

            // equation below choices
            y = equationChoiceNode.getFullBoundsReference().getMaxY() + ySpacing;
            equationNode.setOffset( 0, y );

            // boxes below equation
            y = equationNode.getFullBoundsReference().getMaxY() + ySpacing;
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
            y = Math.max( barChartsNode.getFullBoundsReference().getMaxY(), balanceScalesNode.getFullBoundsReference().getMaxY() ) + ySpacing;
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
        globalProperties.answersVisible.addObserver( new SimpleObserver() {
            public void update() {
                answerNode.setVisible( globalProperties.answersVisible.getValue() );
            }
        } );
    }

    public void reset() {
        balanceChoiceProperty.reset();
    }
}
