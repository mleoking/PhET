// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.model;

import edu.colorado.phet.chemistry.model.Atom;
import edu.colorado.phet.chemistry.model.Element;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Base class for all chemical equations.
 * A chemical equation has 2 sets of terms, reactants and products.
 * During the chemical reaction represented by the equation, reactants are transformed into products.
 * <p>
 * An equation is "balanced" when each term's user coefficient is an integer multiple N of
 * the balanced coefficient, N is the same for all terms in the equation, and N >= 1.
 * An equation is "balanced and simplified" when it is balanced and N=1.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Equation {

    public final EquationTerm[] reactants, products;
    public final Property<Boolean> balancedProperty;
    public final Property<Boolean> balancedAndSimplifiedProperty;

    /**
     * Constructor.
     * @param reactants
     * @param products
     */
    public Equation( final EquationTerm[] reactants, final EquationTerm[] products ) {

        // check arguments
        if ( !( ( reactants.length > 1 && products.length > 0 ) || ( reactants.length > 0 && products.length > 1 ) ) ) {
            throw new IllegalArgumentException( "equation requires at least 2 reactants and 1 product, or 1 reactant and 2 products" );
        }

        this.reactants = reactants;
        this.products = products;
        this.balancedProperty = new Property<Boolean>( false );
        this.balancedAndSimplifiedProperty = new Property<Boolean>( false );

        // equation is balanced if all terms are balanced.
        {
            SimpleObserver coefficientObserver = new SimpleObserver() {
                public void update() {
                    updateBalancedProperties();
                }
            };
            for ( EquationTerm term : reactants ) {
                term.getUserCoefficientProperty().addObserver( coefficientObserver );
            }
            for ( EquationTerm term : products ) {
                term.getUserCoefficientProperty().addObserver( coefficientObserver );
            }
        }
    }

    public void reset() {
        for ( EquationTerm term : reactants ) {
            term.reset();
        }
        for ( EquationTerm term : products ) {
            term.reset();
        }
        // balanced properties are automatically reset when terms are reset
    }

    /*
     * An equation is balanced if all of its terms have a coefficient that is the
     * same integer multiple of the term's balanced coefficient.  If the integer
     * multiple is 1, then the term is balanced with lowest possible coefficients.
     */
    private void updateBalancedProperties() {

        // Get integer multiplier from the first reactant term.
        final int multiplier = reactants[0].getUserCoefficient() / reactants[0].getBalancedCoefficient();

        boolean balanced = ( multiplier > 0 );

        // Check each term to see if the actual coefficient is the same integer multiple of the balanced coefficient.
        for ( EquationTerm reactant : reactants ) {
            balanced = balanced && ( reactant.getUserCoefficient() == multiplier * reactant.getBalancedCoefficient() );
        }
        for ( EquationTerm product : products ) {
            balanced = balanced && ( product.getUserCoefficient() == multiplier * product.getBalancedCoefficient() );
        }

        balancedAndSimplifiedProperty.set( balanced && ( multiplier == 1 ) ); // set the more specific property first
        balancedProperty.set( balanced );
    }

    /**
     * Gets the display name for the equation.
     * By default, this is an HTML representation of the equation formula.
     * Subclasses may override this to provide a more user-friendly name, eg "make water".
     * @return
     */
    public String getName() {
        return createName( reactants, products );
    }

    /**
     * Gets the reactants, the terms on the left side of the equation.
     * @return
     */
    public EquationTerm[] getReactants() {
        return reactants;
    }

    /**
     * Gets the products, the terms on the left side of the equation.
     * @return
     */
    public EquationTerm[] getProducts() {
        return products;
    }

    public boolean isBalanced() {
        return balancedProperty.get();
    }

    public Property<Boolean> getBalancedProperty() {
        return balancedProperty;
    }

    public boolean isBalancedAndSimplified() {
        return balancedAndSimplifiedProperty.get();
    }

    /**
     * Balances the equation by copying the balanced coefficient value to
     * the user coefficient value for each term in the equation.
     */
    public void balance() {
        for ( EquationTerm term : reactants ) {
            term.setUserCoefficient( term.getBalancedCoefficient() );
        }
        for ( EquationTerm term : products ) {
            term.setUserCoefficient( term.getBalancedCoefficient() );
        }
    }

    /**
     * Returns a count of each type of atom, based on the user coefficients.
     * <p>
     * The order of atoms will be the same order that they are encountered in the reactant terms.
     * For example, if the left-hand side of the equation is CH4 + O2, then the order of atoms
     * will be [C,H,O].
     */
    public ArrayList<AtomCount> getAtomCounts() {
        ArrayList<AtomCount> atomCounts = new ArrayList<AtomCount>();
        setAtomCounts( atomCounts, reactants, true /* isReactants */ );
        setAtomCounts( atomCounts, products, false /* isReactants */ );
        return atomCounts;
    }

    /*
     * Some of our visual representations of "balanced" (ie, balance scales and bar charts)
     * compare the number of atoms on the left and right side of the equation.
     * <p>
     * This algorithm supports those representations by computing the atom counts.
     * It examines a collection of terms in the equation (either reactants or products),
     * examines those terms' molecules, and counts the number of each atom type.
     * <p>
     * This is a brute force algorithm, but our number of terms is always small,
     * and this is easy to implement and understand.
     *
     * @param atomCounts
     * @param terms
     * @param isReactants true if the terms are the reactants, false if they are the products
     */
    private static void setAtomCounts(  ArrayList<AtomCount> atomCounts, EquationTerm[] terms, boolean isReactants ) {
        for ( EquationTerm term : terms ) {
            for ( Atom atom : term.getMolecule().getAtoms() ) {
                boolean found = false;
                for ( AtomCount count : atomCounts ) {
                    // add to an existing count
                    if ( count.getElement().equals( atom.getElement() ) ) {
                        if ( isReactants ) {
                            count.setReactantsCount( count.getReactantsCount() + term.getUserCoefficient() );
                        }
                        else {
                            count.setProductsCount( count.getProductsCount() + term.getUserCoefficient() );
                        }
                        found = true;
                        break;
                    }
                }
                // if no existing count was found, create one.
                if ( !found ) {
                    if ( isReactants ) {
                        atomCounts.add( new AtomCount( atom.getElement(), term.getUserCoefficient(), 0 ) );
                    }
                    else {
                        atomCounts.add( new AtomCount( atom.getElement(), 0, term.getUserCoefficient() ) );
                    }
                }
            }
        }
    }

    /**
     * Convenience method for adding an observer to all coefficients.
     */
    public void addCoefficientsObserver( SimpleObserver observer ) {
        for ( EquationTerm term : reactants ) {
            term.getUserCoefficientProperty().addObserver( observer );
        }
        for ( EquationTerm term : products ) {
            term.getUserCoefficientProperty().addObserver( observer );
        }
    }

    /**
     * Convenience method for removing an observer from all coefficients.
     */
    public void removeCoefficientsObserver( SimpleObserver observer ) {
        for ( EquationTerm term : reactants ) {
            term.getUserCoefficientProperty().removeObserver( observer );
        }
        for ( EquationTerm term : products ) {
            term.getUserCoefficientProperty().removeObserver( observer );
        }
    }

    /**
     * Does this equation contain at least one "big" molecule?
     * This affects degree of difficulty in the Game.
     * @return
     */
    public boolean hasBigMolecule() {
        for ( EquationTerm term : reactants ) {
            if ( term.getMolecule().isBig() ) {
                return true;
            }
        }
        for ( EquationTerm term : products ) {
            if ( term.getMolecule().isBig() ) {
                return true;
            }
        }
        return false;
    }

    /*
     * Creates an plaintext string that shows the equation formula.
     * Used for equations that don't have a more general name (eg, "Make Ammonia").
     */
    private static String createName( EquationTerm[] reactants, final EquationTerm[] products ) {
        StringBuffer b = new StringBuffer();
        for ( int i = 0; i < reactants.length; i++ ) {
            b.append( reactants[i].getBalancedCoefficient() );
            b.append( " " );
            b.append( reactants[i].getMolecule().getSymbol() );
            if ( i <  reactants.length - 1 ) {
                b.append( " + " );
            }
        }
        b.append( " -> " );
        for ( int i = 0; i < products.length; i++ ) {
            b.append( products[i].getBalancedCoefficient() );
            b.append( " " );
            b.append( products[i].getMolecule().getSymbol() );
            if ( i <  products.length - 1 ) {
                b.append( " + " );
            }
        }
        return b.toString();
    }
}
