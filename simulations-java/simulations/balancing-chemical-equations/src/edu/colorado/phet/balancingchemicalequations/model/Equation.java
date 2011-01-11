// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.model;

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
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Equation {

    public final String name;
    public final EquationTerm[] reactants, products;
    public final Property<Boolean> balancedProperty;

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

        // equation is balanced if all terms are balanced.
        SimpleObserver o = new SimpleObserver() {
            public void update() {
                boolean balanced = true;
                for ( int i = 0; i < reactants.length && balanced; i++ ) {
                    balanced = reactants[i].isBalanced();
                }
                for ( int i = 0; i < products.length && balanced; i++ ) {
                    balanced = products[i].isBalanced();
                }
                balancedProperty.setValue( balanced );
            }
        };
        for ( EquationTerm term : reactants ) {
            term.getBalancedProperty().addObserver( o );
        }
        for ( EquationTerm term : products ) {
            term.getBalancedProperty().addObserver( o );
        }
    }

    public String getName() {
        return name;
    }

    public EquationTerm[] getReactants() {
        return reactants;
    }

    public EquationTerm[] getProducts() {
        return products;
    }

    public boolean isBalanced() {
        return balancedProperty.getValue();
    }

    public Property<Boolean> getBalancedProperty() {
        return balancedProperty;
    }

    public void reset() {
        for ( EquationTerm term : reactants ) {
            term.reset();
        }
        for ( EquationTerm term : products ) {
            term.reset();
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
