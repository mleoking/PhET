// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;

/**
 * Face node used in the "Introduction" module to indicate when the equation is balanced.
 * The face is always a smiley, but is visible only when the equation is balanced.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntroductionFaceNode extends FaceNode {

    private Equation equation;

    public IntroductionFaceNode( final Property<Equation> currentEquationProperty ) {
        super( 120 );
        smile();

        this.equation = currentEquationProperty.get();

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
                equation = currentEquationProperty.get();
                equation.getBalancedProperty().addObserver( balancedObserver );
            }
        } );
    }
}
