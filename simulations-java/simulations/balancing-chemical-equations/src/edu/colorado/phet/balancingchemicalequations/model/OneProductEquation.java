// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.model;

import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.F2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.H2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.H2O;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.HF;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.N2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.NH3;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.O2;

/**
 * Base class for equations with one product.
 * This base class adds no new functionality to Equation, it simply provides a more convenient constructor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class OneProductEquation extends Equation {

    public OneProductEquation( String name, EquationTerm reactant1, EquationTerm reactant2, EquationTerm product1 ) {
        super( name, new EquationTerm[] { reactant1, reactant2 }, new EquationTerm[] { product1 } );
    }

    // 1N2 + 3H2 -> 2NH3
    public static class AmmoniaEquation extends OneProductEquation {
        public AmmoniaEquation() {
            super( BCEStrings.AMMONIA, new EquationTerm( 1, new N2() ), new EquationTerm( 3, new H2() ), new EquationTerm( 2, new NH3() ) );
        }
    }

    // 2H2 + 1O2 -> 2H2O
    public static class WaterEquation extends OneProductEquation {
        public WaterEquation() {
            super( BCEStrings.WATER, new EquationTerm( 2, new H2() ), new EquationTerm( 1, new O2() ), new EquationTerm( 2, new H2O() ) );
        }
    }

    // H2 + F2 -> 2 HF
    public static class Equation_H2_F2_2HF extends OneProductEquation {
        public Equation_H2_F2_2HF() {
            super( BCEStrings.WATER, new EquationTerm( 1, new H2() ), new EquationTerm( 1, new F2() ), new EquationTerm( 2, new HF() ) );
        }
    }

    // H2 + Cl2 -> 2 HCl
    // CO + 2 H2 -> CH3OH
    // CH2O + H2 -> CH3OH
    // C2H4 + H2 -> C2H6
    // C2H2 + 2 H2 -> C2H6
    // C + O2 -> CO2
    // 2 C + O2 -> 2 CO
    // 2 CO + O2 -> 2 CO2
    // C + CO2 -> 2 CO
    // C + 2 S -> CS2
    // N2 + 3 H2 -> 2 NH3
    // N2 + O2 -> 2 NO
    // 2 NO + O2 -> 2 NO2
    // 2 N2 + O2 -> 2 N2O
    // P4 + 6 H2 -> 4 PH3
    // P4 + 6 F2 -> 4 PF3
    // P4 + 6 Cl2 -> 4 PCl3
    // P4 + 10 Cl2 -> 4 PCl5
    // PCl3 + Cl2 -> PCl5
    // 2 SO2 + O2 -> 2 SO3
}