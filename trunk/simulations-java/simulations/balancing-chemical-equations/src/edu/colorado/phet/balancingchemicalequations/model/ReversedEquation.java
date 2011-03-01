// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.model;

import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.H2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.H2O;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.O2;

/**
 * This was added rather late during development.
 * It was decided that some reversed reactions should be included, and here they live.
 * This base class adds no new functionality to Equation, it simply provides a more convenient constructor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ReversedEquation extends Equation {

    protected ReversedEquation( String name, EquationTerm reactant1, EquationTerm product1, EquationTerm product2 ) {
        super( name, new EquationTerm[] { reactant1 }, new EquationTerm[] { product1, product2 } );
    }

    protected ReversedEquation( EquationTerm reactant1, EquationTerm product1, EquationTerm product2 ) {
        super( new EquationTerm[] { reactant1 }, new EquationTerm[] { product1, product2 } );
    }

    public static class SeparateWaterEquation extends ReversedEquation {
        public SeparateWaterEquation() {
            super( BCEStrings.SEPARATE_WATER, new EquationTerm( 2, new H2O() ), new EquationTerm( 2, new H2() ), new EquationTerm( 1, new O2() ) );
        }
    }
}
