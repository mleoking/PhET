// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.model;

import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.C2H4;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.CH4;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.CMolecule;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.CO;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.CO2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.H2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.H2O;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.O2;

/**
 * Base class for equations with 2 products.
 * This base class adds no new functionality to Equation, it simply provides a more convenient constructor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class TwoProductEquation extends Equation {

    public TwoProductEquation( String name, EquationTerm reactant1, EquationTerm reactant2, EquationTerm product1, EquationTerm product2 ) {
        super( name, new EquationTerm[] { reactant1, reactant2 }, new EquationTerm[] { product1, product2 } );
    }

    public TwoProductEquation( EquationTerm reactant1, EquationTerm reactant2, EquationTerm product1, EquationTerm product2 ) {
        super( new EquationTerm[] { reactant1, reactant2 }, new EquationTerm[] { product1, product2 } );
    }

    // 1CH4 + 2O2 -> 1CO2 + 2H2O
    public static class MethaneEquation extends TwoProductEquation {
        public MethaneEquation() {
            super( BCEStrings.METHANE, new EquationTerm( 1, new CH4() ), new EquationTerm( 2, new O2() ), new EquationTerm( 1, new CO2() ), new EquationTerm( 2, new H2O() ) );
        }
    }

    // 2 C + 2 H2O -> CH4 + CO2
    public static class Equation_2C_2H2O_CH4_CO2 extends TwoProductEquation {
        public Equation_2C_2H2O_CH4_CO2() {
            super( new EquationTerm( 2, new CMolecule() ), new EquationTerm( 2, new H2O() ), new EquationTerm( 1, new CH4() ), new EquationTerm( 1, new CO2() ) );
        }
    }

    // CH4 + H2O -> 3 H2 + CO
    public static class Equation_CH4_H2O_3H2_CO extends TwoProductEquation {
        public Equation_CH4_H2O_3H2_CO() {
            super( new EquationTerm( 1, new CH4() ), new EquationTerm( 1, new H2O() ), new EquationTerm( 3, new H2() ), new EquationTerm( 1, new CO() ) );
        }
    }

    // C2H4 + 3 O2 -> 2 CO2 + 2 H2O
    public static class Equation_C2H4_3O2_2CO2_2H2O extends TwoProductEquation {
        public Equation_C2H4_3O2_2CO2_2H2O() {
            super( new EquationTerm( 1, new C2H4() ), new EquationTerm( 3, new O2() ), new EquationTerm( 2, new CO2() ), new EquationTerm( 2, new H2O() ) );
        }
    }

    // C2H6 + Cl2 -> C2H5Cl + HCl
    // CH4 + 4 S -> CS2 + 2 H2S
    // CS2 + 3 O2 -> CO2 + 2 SO2
    // SO2 + 2 H2 -> S + 2 H2O
    // SO2 + 3 H2 -> H2S + 2 H2O
    // 2 F2 + H2O -> OF2 + 2 HF
    // OF2 + H2O -> O2 + 2 HF

    // 2 C2H6 + 7 O2 -> 4 CO2 + 6 H2O
    // 2 C2H2 + 5 O2 -> 4 CO2 + 2 H2O
    // C2H5OH + 3 O2 -> 2 CO2 + 3 H2O
    // 4 NH3 + 3 O2 -> 2 N2 + 6 H2O
    // 4 NH3 + 5 O2 -> 4 NO + 6 H2O
    // 4 NH3 + 7 O2 -> 4 NO2 + 6 H2O
    // 4 NH3 + 6 NO -> 5 N2 + 6 H2O
}