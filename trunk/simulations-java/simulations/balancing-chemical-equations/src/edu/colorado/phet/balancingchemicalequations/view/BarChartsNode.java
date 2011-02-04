// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import java.awt.Font;
import java.util.ArrayList;

import edu.colorado.phet.balancingchemicalequations.BCEColors;
import edu.colorado.phet.balancingchemicalequations.BCESymbols;
import edu.colorado.phet.balancingchemicalequations.model.AtomCount;
import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.balancingchemicalequations.model.EquationTerm;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Visual representation of an equation as a pair of bar charts, for left and right side of equation.
 * An indicator between the charts (equals or not equals) indicates whether they are balanced.
 * <p>
 * This implementation is very brute force, just about everything is recreated each time
 * a coefficient is changed in the equations.  But we have a small number of coefficients,
 * and nothing else is happening in the sim.  So we're trading efficiency for simplcity of
 * implementation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BarChartsNode extends PComposite {

    private final HorizontalAligner aligner;
    private final PText equalitySignNode;
    private final PNode reactantsChartParent, productsChartParent;
    private final SimpleObserver coefficientsObserver;

    private Equation equation;

    public BarChartsNode( final Property<Equation> equationProperty, HorizontalAligner aligner ) {

        this.aligner = aligner;

        reactantsChartParent = new PComposite();
        addChild( reactantsChartParent );

        productsChartParent = new PComposite();
        addChild( productsChartParent );

        equalitySignNode = new PText();
        equalitySignNode.setFont( new PhetFont( Font.BOLD, 60 ) );
        equalitySignNode.setTextPaint( BCEColors.UNBALANCED_ARROW_COLOR );
        addChild( equalitySignNode );

        coefficientsObserver = new SimpleObserver() {
            public void update() {
               updateAll();
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

            updateAll();
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
    private void updateAll() {
        updateChart( reactantsChartParent, equation.getReactants(), true /* isReactants */ );
        updateChart( productsChartParent, equation.getProducts(), false /* isReactants */ );
        updateEqualitySign();
        updateLayout();
    }

    /*
     * Creates a bar chart under a parent node.
     */
    private void updateChart( PNode parent, EquationTerm[] terms, boolean isReactants ) {
        parent.removeAllChildren();
        double x = 0;
        ArrayList<AtomCount> atomCounts = equation.getAtomCounts();
        for ( AtomCount atomCount : atomCounts ) {
            int count = ( isReactants ? atomCount.getReactantsCount() : atomCount.getProductsCount() );
            BarNode barNode = new BarNode( atomCount.getAtom(), count );
            barNode.setOffset( x, 0 );
            parent.addChild( barNode );
            x = barNode.getFullBoundsReference().getMaxX() + 60;
        }
    }

    /*
     * Updates the equality sign.
     */
    private void updateEqualitySign() {
        if ( equation.isAllCoefficientsZero() || equation.isBalanced() ) {
            equalitySignNode.setText( BCESymbols.EQUALS );
        }
        else {
            equalitySignNode.setText( BCESymbols.NOT_EQUALS );
        }
    }

    /*
     * Updates the layout.
     */
    private void updateLayout() {

        final double xSpacing = 80;

        // equality sign at center
        double x = aligner.getCenterXOffset() - ( equalitySignNode.getFullBoundsReference().getWidth() / 2 );
        double y = -equalitySignNode.getFullBoundsReference().getHeight() / 2;
        equalitySignNode.setOffset( x, y );

        // reactants chart to the left
        x = equalitySignNode.getFullBoundsReference().getMinX() - reactantsChartParent.getFullBoundsReference().getWidth() - PNodeLayoutUtils.getOriginXOffset( reactantsChartParent ) - xSpacing;
        y = 0;
        reactantsChartParent.setOffset( x, y );

        // products chart to the right
        x = equalitySignNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( productsChartParent ) + xSpacing;
        y = 0;
        productsChartParent.setOffset( x, y );
    }
}
