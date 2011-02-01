// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import java.awt.Font;

import edu.colorado.phet.balancingchemicalequations.BCESymbols;
import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.balancingchemicalequations.model.EquationTerm;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Visual representation of an equation as a pair of bar charts, for left and right side of equation.
 * An indicator between the charts (equals or not equals) indicates whether they are balanced.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BarChartsNode extends PComposite {

    private final PText equalityNode;
    private final PNode reactantsParent, productsParent;
    private final SimpleObserver reactantCoefficientsObserver, productCoefficientsObserver;

    private Equation equation;

    public BarChartsNode( final Property<Equation> equationProperty ) {

        reactantsParent = new PComposite();
        addChild( reactantsParent );
        productsParent = new PComposite();
        addChild( productsParent );

        equalityNode = new PText();
        equalityNode.setFont( new PhetFont( Font.BOLD, 60 ) );
        addChild( equalityNode );

        reactantCoefficientsObserver = new SimpleObserver() {
            public void update() {
               updateReactantsChart();
               updateEquality();
            }
        };

        productCoefficientsObserver = new SimpleObserver() {
            public void update() {
               updateProductsChart();
               updateEquality();
            }
        };

        equationProperty.addObserver( new SimpleObserver() {
            public void update() {
                setEquation( equationProperty.getValue() );
            }
        } );
    }

    private void setEquation( Equation equation ) {
        if ( equation != this.equation ) {

            if ( this.equation != null ) {
                removeCoefficientObserver( this.equation.getReactants(), reactantCoefficientsObserver );
                removeCoefficientObserver( this.equation.getProducts(), productCoefficientsObserver );
            }
            this.equation = equation;
            addCoefficientObserver( this.equation.getReactants(), reactantCoefficientsObserver );
            addCoefficientObserver( this.equation.getProducts(), productCoefficientsObserver );

            updateReactantsChart();
            updateProductsChart();
            updateEquality();
        }
    }

    private void addCoefficientObserver( EquationTerm[] terms, SimpleObserver observer ) {
        for ( EquationTerm term : terms ) {
            term.getActualCoefficientProperty().addObserver( observer );
        }
    }

    private void removeCoefficientObserver( EquationTerm[] terms, SimpleObserver observer ) {
        for ( EquationTerm term : terms ) {
            term.getActualCoefficientProperty().removeObserver( observer );
        }
    }

    private void updateReactantsChart() {
        updateChart( reactantsParent, equation.getReactants() );
    }

    private void updateProductsChart() {
        updateChart( productsParent, equation.getProducts() );
    }

    private void updateChart( PNode parent, EquationTerm[] terms ) {
        parent.removeAllChildren();
        //XXX
    }

    private void updateEquality() {
        if ( equation.isBalanced() ) {
            equalityNode.setText( BCESymbols.EQUALS );
        }
        else {
            equalityNode.setText( BCESymbols.NOT_EQUALS );
        }
    }
}
