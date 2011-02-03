// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import java.util.ArrayList;

import edu.colorado.phet.balancingchemicalequations.model.AtomCount;
import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.balancingchemicalequations.model.EquationTerm;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Visual representation of an equation as a set of balance scales, one for each atom type.
 * The left side of the scale is the reactants, the right side is the products.
 * <p>
 * This implementation is very brute force, just about everything is recreated each time
 * a coefficient is changed in the equations.  But we have a small number of coefficients,
 * and nothing else is happening in the sim.  So we're trading efficiency for simplcity of
 * implementation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BalanceScalesNode extends PComposite {

    private final SimpleObserver coefficientsObserver;

    private final HorizontalAligner aligner;
    private Equation equation;

    public BalanceScalesNode( final Property<Equation> equationProperty, HorizontalAligner aligner ) {

        this.aligner = aligner;

        coefficientsObserver = new SimpleObserver() {
            public void update() {
               updateNode();
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

            // disconnect observers from old equation
            if ( this.equation != null ) {
                removeCoefficientObserver( this.equation.getReactants(), coefficientsObserver );
                removeCoefficientObserver( this.equation.getProducts(), coefficientsObserver );
            }

            // add observers to new equation
            this.equation = equation;
            addCoefficientObserver( this.equation.getReactants(), coefficientsObserver );
            addCoefficientObserver( this.equation.getProducts(), coefficientsObserver );

            updateNode();
        }
    }

    /*
     * Adds an observer to all coefficients.
     */
    private void addCoefficientObserver( EquationTerm[] terms, SimpleObserver observer ) {
        for ( EquationTerm term : terms ) {
            term.getActualCoefficientProperty().addObserver( observer );
        }
    }

    /*
     * Removes an observer from all coefficients.
     */
    private void removeCoefficientObserver( EquationTerm[] terms, SimpleObserver observer ) {
        for ( EquationTerm term : terms ) {
            term.getActualCoefficientProperty().removeObserver( observer );
        }
    }

    /*
     * Updates this node's entire geometry and layout
     */
    private void updateNode() {
        removeAllChildren();
        ArrayList<AtomCount> atomCounts = equation.getAtomCounts();
        final double xSpacing = 25;
        final double dx = BalanceScaleNode.getBeamLength() + xSpacing;
        double x = aligner.getCenterXOffset() - ( ( atomCounts.size() - 1 ) * BalanceScaleNode.getBeamLength() / 2 ) - ( ( atomCounts.size() - 1 ) * xSpacing / 2 );
        for ( AtomCount atomCount : atomCounts ) {
            BalanceScaleNode scaleNode = new BalanceScaleNode( atomCount.getAtom(), atomCount.getReactantsCount(), atomCount.getProductsCount() );
            addChild( scaleNode );
            scaleNode.setOffset( x, 0 );
            x += dx;
        }
    }
}
