// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.model;

import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.C2H4;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.C2H6;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.CH3OH;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.CMolecule;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.CO;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.CO2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.Cl2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.H2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.H2O;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.HCl;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.N2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.NH3;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.NO;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.NO2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.O2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.P4;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.PCl3;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.PCl5;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.SO2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.SO3;

/**
 * Base class for decomposition equations.
 * In a decomposition reaction, a more complex substance breaks down into its more simple parts.
 * All decomposition equations in this sim have 1 reactant and 2 products.
 * <p>
 * This base class adds no new functionality to Equation, it simply provides a more convenient constructor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class DecompositionEquation extends Equation {

    private DecompositionEquation( int r1, Molecule reactant1, int p1, Molecule product1, int p2, Molecule product2 ) {
        super( new EquationTerm[] { new EquationTerm( r1, reactant1 ) }, new EquationTerm[] { new EquationTerm( p1, product1 ), new EquationTerm( p2, product2 ) } );
    }

    // 2 H2O -> 2 H2 + O2
    public static class Equation_2H2O_2H2_O2 extends DecompositionEquation {
        public Equation_2H2O_2H2_O2() {
            super( 2, new H2O(), 2, new H2(), 1, new O2() );
        }

        @Override
        public String getName() {
            return BCEStrings.SEPARATE_WATER;
        }
    }

    // 2 HCl -> H2 + Cl2
    public static class Equation_2HCl_H2_Cl2 extends DecompositionEquation {
        public Equation_2HCl_H2_Cl2() {
            super( 2, new HCl(), 1, new H2(), 1, new Cl2() );
        }
    }

    // CH3OH -> CO + 2 H2
    public static class Equation_CH3OH_CO_2H2 extends DecompositionEquation {
        public Equation_CH3OH_CO_2H2() {
            super( 1, new CH3OH(), 1, new CO(), 2, new H2() );
        }
    }

    // C2H6 -> C2H4 + H2
    public static class Equation_C2H6_C2H4_H2 extends DecompositionEquation {
        public Equation_C2H6_C2H4_H2() {
            super( 1, new C2H6(), 1, new C2H4(), 1, new H2() );
        }
    }

    // 2 CO2 -> 2 CO + O2
    public static class Equation_2CO2_2CO_O2 extends DecompositionEquation {
        public Equation_2CO2_2CO_O2() {
            super( 2, new CO2(), 2, new CO(), 1, new O2() );
        }
    }

    // 2 CO -> C + CO2
    public static class Equation_2CO_C_CO2 extends DecompositionEquation {
        public Equation_2CO_C_CO2() {
            super( 2, new CO(), 1, new CMolecule(), 1, new CO2() );
        }
    }

    // 2 NH3 -> N2 + 3 H2
    public static class Equation_2NH3_N2_3H2 extends DecompositionEquation {
        public Equation_2NH3_N2_3H2() {
            super( 2, new NH3(), 1, new N2(), 3, new H2() );
        }
    }

    // 2 NO -> N2 + O2
    public static class Equation_2NO_N2_O2 extends DecompositionEquation {
        public Equation_2NO_N2_O2() {
            super( 2, new NO(), 1, new N2(), 1, new O2() );
        }
    }

    // 2 NO2 -> 2 NO + O2
    public static class Equation_2NO2_2NO_O2 extends DecompositionEquation {
        public Equation_2NO2_2NO_O2() {
            super( 2, new NO2(), 2, new NO(), 1, new O2() );
        }
    }

    // 4 PCl3 -> P4 + 6 Cl2
    public static class Equation_4PCl3_P4_6Cl2 extends DecompositionEquation {
        public Equation_4PCl3_P4_6Cl2() {
            super( 4, new PCl3(), 1, new P4(), 6, new Cl2() );
        }
    }

    // PCl5 -> PCl3 + Cl2
    public static class Equation_PCl5_PCl3_Cl2 extends DecompositionEquation {
        public Equation_PCl5_PCl3_Cl2() {
            super( 1, new PCl5(), 1, new PCl3(), 1, new Cl2() );
        }
    }

    // 2 SO3 -> 2 SO2 + O2
    public static class Equation_2SO3_2SO2_O2 extends DecompositionEquation {
        public Equation_2SO3_2SO2_O2() {
            super( 2, new SO3(), 2, new SO2(), 1, new O2() );
        }
    }
}
