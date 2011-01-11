// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Debug node for indicating whether an equation is balanaced.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BalancedIndicatorNode extends PText {

    private Equation equation;
    private final SimpleObserver observer;

    public BalancedIndicatorNode( Equation equation ) {
        this.equation = equation;
        observer = new SimpleObserver() {
            public void update() {
                setText( getEquation().getBalancedProperty().getValue() ? "Balanced" : "Unbalanced" );
            }
        };
        equation.getBalancedProperty().addObserver( observer );
    }

    public void setEquation( Equation equation ) {
        if ( equation != this.equation ) {
            this.equation.getBalancedProperty().removeObserver( observer );
            this.equation = equation;
            this.equation.getBalancedProperty().addObserver( observer );
        }
    }

    private Equation getEquation() {
        return equation;
    }
}
