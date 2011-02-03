// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.module.balanceequation;

import java.awt.Color;
import java.awt.Dimension;

import edu.colorado.phet.balancingchemicalequations.BCEGlobalProperties;
import edu.colorado.phet.balancingchemicalequations.control.BalanceChoiceNode;
import edu.colorado.phet.balancingchemicalequations.control.BalanceChoiceNode.BalanceChoice;
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
public class BalanceEquationCanvas extends BCECanvas {

    private static final Dimension BOX_SIZE = new Dimension( 300, 140 );
    private static final double BOX_SEPARATION = 60;

    private final Property<BalanceChoice> balanceChoiceProperty; // determines the visual representation of "balanced"
    private final BoxesNode boxesNode;

    public BalanceEquationCanvas( final BalanceEquationModel model, final BCEGlobalProperties globalProperties, Resettable resettable ) {
        super( globalProperties.getCanvasColorProperty() );

        HorizontalAligner aligner = new HorizontalAligner( BOX_SIZE, BOX_SEPARATION );

        balanceChoiceProperty = new Property<BalanceChoice>( BalanceChoice.NONE );

        // control for choosing an equation
        EquationChoiceNode equationChoiceNode = new EquationChoiceNode( model.getEquations(), model.getCurrentEquationProperty() );
        addChild( equationChoiceNode );

        // equation, in formula format
        final EquationNode equationNode = new EquationNode( model.getCurrentEquationProperty(), model.getCoefficientsRange(), true /* editable */, aligner );
        addChild( equationNode );

        // boxes that show molecules corresponding to the equation coefficients
        boxesNode = new BoxesNode(  model.getCurrentEquationProperty(), model.getCoefficientsRange(), aligner );
        addChild( boxesNode );

        // control for choosing the visual representation of "balanced"
        BalanceChoiceNode balanceChoiceNode = new BalanceChoiceNode( balanceChoiceProperty );
        addChild( balanceChoiceNode );

        // bar charts
        final BarChartsNode barChartsNode = new BarChartsNode( model.getCurrentEquationProperty(), aligner );
        addChild( barChartsNode );

        // balance scales
        final BalanceScalesNode balanceScalesNode = new BalanceScalesNode( model.getCurrentEquationProperty(), aligner );
        addChild( balanceScalesNode );

        // Reset All button
        ResetAllButtonNode resetAllButtonNode = new ResetAllButtonNode( resettable, globalProperties.getFrame(), 12, Color.BLACK, Color.WHITE );
        resetAllButtonNode.addResettable( globalProperties );
        addChild( resetAllButtonNode );

        // Dev, shows balanced coefficients
        BalancedEquationNode balancedEquationNode = new BalancedEquationNode( model.getCurrentEquationProperty() );
        if ( globalProperties.isDev() ) {
            addChild( balancedEquationNode );
        }

        /*
         * Layout - all of the major visual representations have x-offset=0,
         * and handle their own horizontal alignment via HorizontalAligner.
         */
        {
            equationChoiceNode.setOffset( 0, 0 );
            double y = equationChoiceNode.getFullBoundsReference().getMaxY() + 15;
            equationNode.setOffset( 0, y );
            y = equationNode.getFullBoundsReference().getMaxY() + 15;
            boxesNode.setOffset( 0, y );
            y = boxesNode.getFullBoundsReference().getMaxY() + 110;
            barChartsNode.setOffset( 0, y );
            y = barChartsNode.getYOffset() - 10;
            balanceScalesNode.setOffset( 0, y );
            y = Math.max( barChartsNode.getFullBoundsReference().getMaxY(), balanceScalesNode.getFullBoundsReference().getMaxY() ) + 25;
            balanceChoiceNode.setOffset( 0, y );
            double x = boxesNode.getFullBoundsReference().getMaxX() - resetAllButtonNode.getFullBoundsReference().getWidth();
            y = equationChoiceNode.getFullBoundsReference().getMinY();
            resetAllButtonNode.setOffset( x, y );
            x = aligner.getCenterXOffset();
            balancedEquationNode.setOffset( x, 0 );
        }

        // observers
        balanceChoiceProperty.addObserver( new SimpleObserver() {
            public void update() {
                barChartsNode.setVisible( balanceChoiceProperty.getValue().equals( BalanceChoice.BAR_CHARTS ) );
                balanceScalesNode.setVisible( balanceChoiceProperty.getValue().equals( BalanceChoice.BALANCE_SCALES ) );
            }
        } );

        globalProperties.getMoleculesVisibleProperty().addObserver( new SimpleObserver() {
            public void update() {
                boxesNode.setMoleculesVisible( globalProperties.getMoleculesVisibleProperty().getValue() );
            }
        } );

    }

    public void reset() {
        balanceChoiceProperty.reset();
    }
}
