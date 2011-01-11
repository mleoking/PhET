// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import java.awt.Color;

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
        setTextPaint( Color.RED );
        this.equation = equation;
        observer = new SimpleObserver() {
            public void update() {
                setText( getEquation().isBalanced() ? "Balanced" : "Unbalanced" );
                setTextPaint( getEquation().isBalanced() ? new Color( 37, 185, 24 ) : Color.RED );
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
