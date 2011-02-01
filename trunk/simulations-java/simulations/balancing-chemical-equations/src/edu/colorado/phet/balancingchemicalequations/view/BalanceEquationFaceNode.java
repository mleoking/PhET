// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;

/**
 * Face node used in the "Balance Equation" module to indicate when the equation is balanced.
 * The face is always a smiley, but is visible only when the equation is balanaced.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BalanceEquationFaceNode extends FaceNode {

    private Equation equation;

    public BalanceEquationFaceNode( final Property<Equation> currentEquationProperty ) {
        super( 40 );
        smile();

        this.equation = currentEquationProperty.getValue();

        // set visibility based on whether the current equation is balanced
        final SimpleObserver balancedObserver = new SimpleObserver() {
            public void update() {
                setVisible( equation.isBalanced() );
            }
        };

        // handle equation changes
        currentEquationProperty.addObserver( new SimpleObserver() {
            public void update() {
                equation.getBalancedProperty().removeObserver( balancedObserver );
                equation = currentEquationProperty.getValue();
                equation.getBalancedProperty().addObserver( balancedObserver );
            }
        } );
    }
}
