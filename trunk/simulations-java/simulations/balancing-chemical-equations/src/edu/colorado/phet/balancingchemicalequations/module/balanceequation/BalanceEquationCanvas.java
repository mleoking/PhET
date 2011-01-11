// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.module.balanceequation;

import java.awt.Color;
import java.awt.Frame;

import edu.colorado.phet.balancingchemicalequations.control.BalanceChoiceNode;
import edu.colorado.phet.balancingchemicalequations.control.BalanceChoiceNode.BalanceChoice;
import edu.colorado.phet.balancingchemicalequations.control.EquationChoiceNode;
import edu.colorado.phet.balancingchemicalequations.view.BCECanvas;
import edu.colorado.phet.balancingchemicalequations.view.BalancedIndicatorNode;
import edu.colorado.phet.balancingchemicalequations.view.BoxesNode;
import edu.colorado.phet.balancingchemicalequations.view.EquationNode;
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

    private final Property<BalanceChoice> balanceChoiceProperty; // determines the visual representation of "balanced"

    public BalanceEquationCanvas( Frame parentFrame, Resettable resettable, final BalanceEquationModel model, boolean dev ) {

        balanceChoiceProperty = new Property<BalanceChoice>( BalanceChoice.BAR_CHART );

        // control for choosing an equation
        EquationChoiceNode equationChoiceNode = new EquationChoiceNode( model.getEquations(), model.getCurrentEquationProperty() );
        addChild( equationChoiceNode );

        // equation, in formula format
        final EquationNode equationNode = new EquationNode( model.getCurrentEquationProperty(), model.getCoefficientsRange(), true );
        addChild( equationNode );

        // boxes that show molecules corresponding to the equation coefficients
        BoxesNode boxesNode = new BoxesNode(  model.getCurrentEquationProperty(), model.getCoefficientsRange() );
        addChild( boxesNode );

        // control for choosing the visual representation of "balanced"
        BalanceChoiceNode balanceChoiceNode = new BalanceChoiceNode( balanceChoiceProperty );
        addChild( balanceChoiceNode );

        // Reset All button
        ResetAllButtonNode resetAllButtonNode = new ResetAllButtonNode( resettable, parentFrame, 12, Color.BLACK, Color.WHITE );
        resetAllButtonNode.setConfirmationEnabled( false );
        addChild( resetAllButtonNode );

        // dev node to indicate whether the equation is balanced
        final BalancedIndicatorNode balancedIndicatorNode = new BalancedIndicatorNode( model.getCurrentEquation() );
        if ( dev ) {
            addChild( balancedIndicatorNode );
        }
        model.getCurrentEquationProperty().addObserver( new SimpleObserver() {
            public void update() {
                balancedIndicatorNode.setEquation( model.getCurrentEquation() );
            }
        } );

        // layout
        double x = 0;
        double y = 0;
        equationChoiceNode.setOffset( x, y );
        y = equationChoiceNode.getFullBoundsReference().getMaxY() + 20;
        equationNode.setOffset( x, y );
        y = equationNode.getFullBoundsReference().getMaxY() + 20;
        boxesNode.setOffset( x, y );
        y = boxesNode.getFullBoundsReference().getMaxY() + 20;
        balanceChoiceNode.setOffset( x, y );
        x = balanceChoiceNode.getFullBoundsReference().getMaxX() + 20;
        y = balanceChoiceNode.getFullBoundsReference().getCenterY() - ( balancedIndicatorNode.getFullBoundsReference().getHeight() / 2 );
        balancedIndicatorNode.setOffset( x, y );
        x = boxesNode.getFullBoundsReference().getMaxX() - resetAllButtonNode.getFullBoundsReference().getWidth();
        y = boxesNode.getFullBoundsReference().getMaxY() + 120;//XXX
        resetAllButtonNode.setOffset( x, y );

        // observers
        balanceChoiceProperty.addObserver( new SimpleObserver() {
            public void update() {
                //XXX show either chart or balance scale
            }
        } );
    }

    public void reset() {
        balanceChoiceProperty.reset();
    }
}
