// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import java.awt.Font;
import java.util.ArrayList;

import edu.colorado.phet.balancingchemicalequations.BCESymbols;
import edu.colorado.phet.balancingchemicalequations.model.Atom;
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
 * <p>
 * This implementation is very brute force, just about everything is recreated each time
 * a coefficient is changed in the equations.  But we have a small number of coefficients,
 * and nothing else is happening in the sim.  So we're trading efficiency for simplcity of
 * implementation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BarChartsNode extends PComposite {

    // Internal data structure for counting the number of each atom type.
    private static class AtomCount {

        public final Atom atom;
        public int count;

        public AtomCount( Atom atom, int count ) {
            this.atom = atom;
            this.count = count;
        }
    }

    private final PText equalitySignNode;
    private final PNode reactantsChartParent, productsChartParent;
    private final SimpleObserver coefficientsObserver;

    private Equation equation;

    public BarChartsNode( final Property<Equation> equationProperty ) {

        reactantsChartParent = new PComposite();
        addChild( reactantsChartParent );

        productsChartParent = new PComposite();
        addChild( productsChartParent );

        equalitySignNode = new PText();
        equalitySignNode.setFont( new PhetFont( Font.BOLD, 60 ) );
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
        updateChart( reactantsChartParent, equation.getReactants() );
        updateChart( productsChartParent, equation.getProducts() );
        updateEqualitySign();
        updateLayout();
    }

    /*
     * Creates a bar chart under a parent node.
     */
    private static void updateChart( PNode parent, EquationTerm[] terms ) {
        parent.removeAllChildren();
        double x = 0;
        ArrayList<AtomCount> atomCounts = countAtoms( terms );
        for ( AtomCount atomCount : atomCounts ) {
            BarNode barNode = new BarNode( atomCount.atom, atomCount.count );
            barNode.setOffset( x, 0 );
            parent.addChild( barNode );
            x = barNode.getFullBoundsReference().getMaxX() + 40;
        }
    }

    /*
     * Updates the equality sign.
     */
    private void updateEqualitySign() {
        if ( equation.allCoefficientsZero() || equation.isBalanced() ) {
            equalitySignNode.setText( BCESymbols.EQUALS );
        }
        else {
            equalitySignNode.setText( BCESymbols.NOT_EQUALS );
        }
    }

    /*
     * Updates the layout.
     */
    //TODO implement horizontal alignment with equation terms
    private void updateLayout() {
        double x = 0;
        double y = 0;
        reactantsChartParent.setOffset( x, y );
        x = reactantsChartParent.getFullBoundsReference().getMaxX() + 40;
        y = -equalitySignNode.getFullBoundsReference().getHeight();
        equalitySignNode.setOffset( x, y );
        x = equalitySignNode.getFullBoundsReference().getMaxX() + 40;
        y = reactantsChartParent.getYOffset();
        productsChartParent.setOffset( x, y );
    }

    /*
     * Given a set of terms, returns a count of each type of atom.
     * This is a brute force algorithm, but our number of terms is always small,
     * and this is easy to implement and understand.
     */
    private static ArrayList<AtomCount> countAtoms( EquationTerm[] terms ) {
        ArrayList<AtomCount> atomCounts = new ArrayList<AtomCount>();
        for ( EquationTerm term : terms ) {
            for ( Atom atom : term.getMolecule().getAtoms() ) {
                boolean found = false;
                for ( AtomCount count : atomCounts ) {
                    // add to an existing atom count
                    if ( count.atom.getClass().equals( atom.getClass() ) ) {
                        count.count += term.getActualCoefficient();
                        found = true;
                        break;
                    }
                }
                // if not existing atom count was found, create one.
                if ( !found ) {
                    atomCounts.add( new AtomCount( atom, term.getActualCoefficient() ) );
                }
            }
        }
        return atomCounts;
    }
}
