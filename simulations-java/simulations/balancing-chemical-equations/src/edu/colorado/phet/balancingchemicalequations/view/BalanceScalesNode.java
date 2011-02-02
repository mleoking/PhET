// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import java.util.ArrayList;

import edu.colorado.phet.balancingchemicalequations.model.Atom;
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

    // Internal data structure for counting the number of each atom type.
    private static class AtomCount {

        public final Atom atom;
        public int reactantsCount, productsCount;

        public AtomCount( Atom atom, int reactantsCount, int productsCount ) {
            this.atom = atom;
            this.reactantsCount = reactantsCount;
            this.productsCount = productsCount;
        }
    }

    private final SimpleObserver coefficientsObserver;

    private Equation equation;

    public BalanceScalesNode( final Property<Equation> equationProperty ) {

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
        double x = 0;
        ArrayList<AtomCount> atomCounts = countAtoms( equation.getReactants(), equation.getProducts() );
        for ( AtomCount atomCount : atomCounts ) {
            BalanceScaleNode scaleNode = new BalanceScaleNode( atomCount.atom, atomCount.reactantsCount, atomCount.productsCount );
            scaleNode.setOffset( x, 0 );
            addChild( scaleNode );
            x += ( scaleNode.getBeamLength() + 25 );
        }
    }

    /*
     * Given a set of terms, returns a count of each type of atom.
     * This is a brute force algorithm, but our number of terms is always small,
     * and this is easy to implement and understand.
     */
    private static ArrayList<AtomCount> countAtoms( EquationTerm[] reactantTerms, EquationTerm[] productTerms ) {
        ArrayList<AtomCount> atomCounts = new ArrayList<AtomCount>();

        // reactants
        for ( EquationTerm term : reactantTerms ) {
            for ( Atom atom : term.getMolecule().getAtoms() ) {
                boolean found = false;
                for ( AtomCount count : atomCounts ) {
                    // add to an existing atom count
                    if ( count.atom.getClass().equals( atom.getClass() ) ) {
                        count.reactantsCount += term.getActualCoefficient();
                        found = true;
                        break;
                    }
                }
                // if not existing atom count was found, create one.
                if ( !found ) {
                    atomCounts.add( new AtomCount( atom, term.getActualCoefficient(), 0 ) );
                }
            }
        }

        // products
        for ( EquationTerm term : productTerms ) {
            for ( Atom atom : term.getMolecule().getAtoms() ) {
                boolean found = false;
                for ( AtomCount count : atomCounts ) {
                    // add to an existing atom count
                    if ( count.atom.getClass().equals( atom.getClass() ) ) {
                        count.productsCount += term.getActualCoefficient();
                        found = true;
                        break;
                    }
                }
                // if not existing atom count was found, create one.
                if ( !found ) {
                    atomCounts.add( new AtomCount( atom, 0, term.getActualCoefficient() ) );
                }
            }
        }

        return atomCounts;
    }
}
