// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.module.balanceequation;

import java.awt.Color;
import java.awt.Frame;

import edu.colorado.phet.balancingchemicalequations.control.EquationChoiceNode;
import edu.colorado.phet.balancingchemicalequations.view.BCECanvas;
import edu.colorado.phet.balancingchemicalequations.view.BalancedIndicatorNode;
import edu.colorado.phet.balancingchemicalequations.view.EquationNode;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;

/**
 * Canvas for the "Balance Equation" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BalanceEquationCanvas extends BCECanvas {

    public BalanceEquationCanvas( Frame parentFrame, Resettable resettable, final BalanceEquationModel model ) {

        EquationChoiceNode equationChoiceNode = new EquationChoiceNode( model.getEquations(), model.getCurrentEquationProperty() );
        addChild( equationChoiceNode );
        equationChoiceNode.setOffset( 5, 50 );//XXX

        final EquationNode equationNode = new EquationNode( model.getCurrentEquationProperty(), new IntegerRange( 0, 3 ), true );
        addChild( equationNode );
        equationNode.setOffset( equationChoiceNode.getXOffset(), equationChoiceNode.getFullBoundsReference().getMaxY() + 10 );

        final BalancedIndicatorNode balancedIndicatorNode = new BalancedIndicatorNode( model.getCurrentEquation() );
        addChild( balancedIndicatorNode );
        balancedIndicatorNode.setOffset( 5, 200 );//XXX
        model.getCurrentEquationProperty().addObserver( new SimpleObserver() {
            public void update() {
                balancedIndicatorNode.setEquation( model.getCurrentEquation() );
            }
        } );

        ResetAllButtonNode resetAllButtonNode = new ResetAllButtonNode( resettable, parentFrame, 12, Color.BLACK, Color.WHITE );
        resetAllButtonNode.setConfirmationEnabled( false );
        addChild( resetAllButtonNode );
        resetAllButtonNode.setOffset( 300, 300 );//XXX
    }
}
