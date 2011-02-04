// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.model;

import java.util.ArrayList;

import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.CH4;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.CO2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.H2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.H2O;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.N2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.NH3;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.O2;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Base class for all chemical equations.
 * A chemical equation has 2 sets of terms, reactants and products.
 * During the chemical reaction represented by the equation, reactants are transformed into products.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Equation {

    public final String name;
    public final EquationTerm[] reactants, products;
    public final Property<Boolean> balancedProperty;
    public final Property<Boolean> balancedWithLowestCoefficientsProperty;

    /**
     * Constructor.
     * @param name user-visible name for the equation
     * @param reactants
     * @param products
     */
    public Equation( String name, final EquationTerm[] reactants, final EquationTerm[] products ) {

        // check arguments
        if ( reactants.length < 2 ) {
            throw new IllegalArgumentException( "equation requires at least 2 reactants" );
        }
        if ( products.length < 1 ) {
            throw new IllegalArgumentException( "equation requires at least 1 product" );
        }

        this.name = name;
        this.reactants = reactants;
        this.products = products;
        this.balancedProperty = new Property<Boolean>( false );
        this.balancedWithLowestCoefficientsProperty = new Property<Boolean>( false );

        // equation is balanced if all terms are balanced.
        SimpleObserver o = new SimpleObserver() {
            public void update() {
                updateBalancedProperties();
            }
        };
        for ( EquationTerm term : reactants ) {
            term.getActualCoefficientProperty().addObserver( o );
        }
        for ( EquationTerm term : products ) {
            term.getActualCoefficientProperty().addObserver( o );
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
        final int multiplier = (int)( reactants[0].getActualCoefficient() / reactants[0].getBalancedCoefficient() );

        boolean balanced = ( multiplier > 0 );

        // Check each term to see if the actual coefficient is the same integer multiple of the balanced coefficient.
        for ( EquationTerm reactant : reactants ) {
            balanced = balanced && ( reactant.getActualCoefficient() == multiplier * reactant.getBalancedCoefficient() );
        }
        for ( EquationTerm product : products ) {
            balanced = balanced && ( product.getActualCoefficient() == multiplier * product.getBalancedCoefficient() );
        }

        balancedWithLowestCoefficientsProperty.setValue( balanced && ( multiplier == 1 ) ); // set the more specific property first
        balancedProperty.setValue( balanced );
    }

    public String getName() {
        return name;
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
        return balancedProperty.getValue();
    }

    public Property<Boolean> getBalancedProperty() {
        return balancedProperty;
    }

    public boolean isBalancedWithLowestCoefficients() {
        return balancedWithLowestCoefficientsProperty.getValue();
    }

    public Property<Boolean> getBalancedWithLowestCoefficientsProperty() {
        return balancedWithLowestCoefficientsProperty;
    }

    public boolean isAllCoefficientsZero() {
        boolean allZero = true;
        for ( EquationTerm term : reactants ) {
            if ( term.getActualCoefficient() > 0 ) {
                allZero = false;
                break;
            }
        }
        if ( allZero ) {
            for ( EquationTerm term : products ) {
                if ( term.getActualCoefficient() > 0 ) {
                    allZero = false;
                }
            }
        }
        return allZero;
    }

    /**
     * Returns a count of each type of atom.
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
     * Sets atom counts for on collection of terms (reactants or products).
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
                    if ( count.getAtom().getClass().equals( atom.getClass() ) ) {
                        if ( isReactants ) {
                            count.setReactantsCount( count.getReactantsCount() + term.getActualCoefficient() );
                        }
                        else {
                            count.setProductsCount( count.getProductsCount() + term.getActualCoefficient() );
                        }
                        found = true;
                        break;
                    }
                }
                // if no existing count was found, create one.
                if ( !found ) {
                    if ( isReactants ) {
                        atomCounts.add( new AtomCount( atom, term.getActualCoefficient(), 0 ) );
                    }
                    else {
                        atomCounts.add( new AtomCount( atom, 0, term.getActualCoefficient() ) );
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
            term.getActualCoefficientProperty().addObserver( observer );
        }
        for ( EquationTerm term : products ) {
            term.getActualCoefficientProperty().addObserver( observer );
        }
    }

    /**
     * Convenience method for removing an observer from all coefficients.
     */
    public void removeCoefficientsObserver( SimpleObserver observer ) {
        for ( EquationTerm term : reactants ) {
            term.getActualCoefficientProperty().removeObserver( observer );
        }
        for ( EquationTerm term : products ) {
            term.getActualCoefficientProperty().removeObserver( observer );
        }
    }

    //------------------------------------------------------------------------
    // One-product equations
    //------------------------------------------------------------------------

    private static abstract class OneProductEquations extends Equation {

        public OneProductEquations( String name, EquationTerm reactant1, EquationTerm reactant2, EquationTerm product1 ) {
            super( name, new EquationTerm[] { reactant1, reactant2 }, new EquationTerm[] { product1 } );
        }
    }

    // 1N2 + 3H2 -> 2NH3
    public static class AmmoniaEquation extends OneProductEquations {

        public AmmoniaEquation() {
            super( BCEStrings.AMMONIA, new EquationTerm( 1, new N2() ), new EquationTerm( 3, new H2() ), new EquationTerm( 2, new NH3() ) );
        }
    }

    // 2H2 + 1O2 -> 2H2O
    public static class WaterEquation extends OneProductEquations {

        public WaterEquation() {
            super( BCEStrings.WATER, new EquationTerm( 2, new H2() ), new EquationTerm( 1, new O2() ), new EquationTerm( 2, new H2O() ) );
        }
    }

    //------------------------------------------------------------------------
    // Two-product equations
    //------------------------------------------------------------------------

    private static abstract class TwoProductEquations extends Equation {

        public TwoProductEquations( String name, EquationTerm reactant1, EquationTerm reactant2, EquationTerm product1, EquationTerm product2 ) {
            super( name, new EquationTerm[] { reactant1, reactant2 }, new EquationTerm[] { product1, product2 } );
        }
    }

    // 1CH4 + 2O2 -> 1CO2 + 2H2O
    public static class MethaneEquation extends TwoProductEquations {

        public MethaneEquation() {
            super( BCEStrings.METHANE, new EquationTerm( 1, new CH4() ), new EquationTerm( 2, new O2() ), new EquationTerm( 1, new CO2() ), new EquationTerm( 2, new H2O() ) );
        }
    }
}
