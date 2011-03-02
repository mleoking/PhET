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
 * This base class adds no new functionality to Equation, it simply provides a more convenient constructor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DecompositionEquation extends Equation {

    protected DecompositionEquation( String name, EquationTerm reactant1, EquationTerm product1, EquationTerm product2 ) {
        super( name, new EquationTerm[] { reactant1 }, new EquationTerm[] { product1, product2 } );
    }

    protected DecompositionEquation( EquationTerm reactant1, EquationTerm product1, EquationTerm product2 ) {
        super( new EquationTerm[] { reactant1 }, new EquationTerm[] { product1, product2 } );
    }

//    protected DecompositionEquation( String name, int c1, Molecule reactant1, int c2, Molecule product1, int c3, Molecule product2  ) {
//        super( name, new EquationTerm[] { new EquationTerm( c1, reactant1 ) }, new EquationTerm[] { new EquationTerm( c2, product1 ), new EquationTerm( c2, product2 ) } );
//    }
//
//    protected DecompositionEquation( int c1, Molecule reactant1, int c2, Molecule product1, int c3, Molecule product2 ) {
//        super( new EquationTerm[] { new EquationTerm( c1, reactant1 ) }, new EquationTerm[] { new EquationTerm( c2, product1 ), new EquationTerm( c2, product2 ) } );
//    }

    // 2 H2O -> 2 H2 + O2
    public static class Equation_2H2O_2H2_O2 extends DecompositionEquation {
        public Equation_2H2O_2H2_O2() {
            super( BCEStrings.SEPARATE_WATER, new EquationTerm( 2, new H2O() ), new EquationTerm( 2, new H2() ), new EquationTerm( 1, new O2() ) );
        }
    }

    // 2 HCl -> H2 + Cl2
    public static class Equation_2HCl_H2_Cl2 extends DecompositionEquation {
        public Equation_2HCl_H2_Cl2() {
            super( new EquationTerm( 2, new HCl() ), new EquationTerm( 1, new H2() ), new EquationTerm( 1, new Cl2() ) );
        }
    }

    // CH3OH -> CO + 2 H2
    public static class Equation_CH3OH_CO_2H2 extends DecompositionEquation {
        public Equation_CH3OH_CO_2H2() {
            super( new EquationTerm( 1, new CH3OH() ), new EquationTerm( 1, new CO() ), new EquationTerm( 2, new H2() ) );
        }
    }

    // C2H6 -> C2H4 + H2
    public static class Equation_C2H6_C2H4_H2 extends DecompositionEquation {
        public Equation_C2H6_C2H4_H2() {
            super( new EquationTerm( 1, new C2H6() ), new EquationTerm( 1, new C2H4() ), new EquationTerm( 1, new H2() ) );
        }
    }

    // 2 CO2 -> 2 CO + O2
    public static class Equation_2CO2_2CO_O2 extends DecompositionEquation {
        public Equation_2CO2_2CO_O2() {
            super( new EquationTerm( 2, new CO2() ), new EquationTerm( 2, new CO() ), new EquationTerm( 1, new O2() ) );
        }
    }

    // 2 CO -> C + CO2
    public static class Equation_2CO_C_CO2 extends DecompositionEquation {
        public Equation_2CO_C_CO2() {
            super( new EquationTerm( 2, new CO() ), new EquationTerm( 1, new CMolecule() ), new EquationTerm( 1, new CO2() ) );
        }
    }

    // 2 NH3 -> N2 + 3 H2
    public static class Equation_2NH3_N2_3H2 extends DecompositionEquation {
        public Equation_2NH3_N2_3H2() {
            super( new EquationTerm( 2, new NH3() ), new EquationTerm( 1, new N2() ), new EquationTerm( 3, new H2() ) );
        }
    }

    // 2 NO -> N2 + O2
    public static class Equation_2NO_N2_O2 extends DecompositionEquation {
        public Equation_2NO_N2_O2() {
            super( new EquationTerm( 2, new NO() ), new EquationTerm( 1, new N2() ), new EquationTerm( 1, new O2() ) );
        }
    }

    // 2 NO2 -> 2 NO + O2
    public static class Equation_2NO2_2NO_O2 extends DecompositionEquation {
        public Equation_2NO2_2NO_O2() {
            super( new EquationTerm( 2, new NO2() ), new EquationTerm( 2, new NO() ), new EquationTerm( 1, new O2() ) );
        }
    }

    // 4 PCl3 -> P4 + 6 Cl2
    public static class Equation_4PCl3_P4_6Cl2 extends DecompositionEquation {
        public Equation_4PCl3_P4_6Cl2() {
            super( new EquationTerm( 4, new PCl3() ), new EquationTerm( 1, new P4() ), new EquationTerm( 6, new Cl2() ) );
        }
    }

    // PCl5 -> PCl3 + Cl2
    public static class Equation_PCl5_PCl3_Cl2 extends DecompositionEquation {
        public Equation_PCl5_PCl3_Cl2() {
            super( new EquationTerm( 1, new PCl5() ), new EquationTerm( 1, new PCl3() ), new EquationTerm( 1, new Cl2() ) );
        }
    }


    // 2 SO3 -> 2 SO2 + O2
    public static class Equation_2SO3_2SO2_O2 extends DecompositionEquation {
        public Equation_2SO3_2SO2_O2() {
            super( new EquationTerm( 2, new SO3() ), new EquationTerm( 2, new SO2() ), new EquationTerm( 1, new O2() ) );
        }
    }
}
