// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.model;

import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.C2H2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.C2H6;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.CH2O;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.CH3OH;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.CMolecule;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.CO;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.CO2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.CS2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.F2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.H2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.H2O;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.HF;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.N2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.N2O;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.NH3;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.O2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.P4;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.PF3;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.PH3;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.SMolecule;

/**
 * Base class for equations with 2 reactants and 1 product.
 * This base class adds no new functionality to Equation, it simply provides convenient constructors.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class TwoReactantsOneProductEquation extends Equation {

    protected TwoReactantsOneProductEquation( String name, EquationTerm reactant1, EquationTerm reactant2, EquationTerm product1 ) {
        super( name, new EquationTerm[] { reactant1, reactant2 }, new EquationTerm[] { product1 } );
    }

    protected TwoReactantsOneProductEquation( EquationTerm reactant1, EquationTerm reactant2, EquationTerm product1 ) {
        super( new EquationTerm[] { reactant1, reactant2 }, new EquationTerm[] { product1 } );
    }

    // 2 H2 + O2 -> 2 H2O
    public static class Equation_2H2_O2_2H2O extends TwoReactantsOneProductEquation {
        public Equation_2H2_O2_2H2O() {
            super( new EquationTerm( 2, new H2() ), new EquationTerm( 1, new O2() ), new EquationTerm( 2, new H2O() ) );
        }
    }

    // H2 + F2 -> 2 HF
    public static class Equation_H2_F2_2HF extends TwoReactantsOneProductEquation {
        public Equation_H2_F2_2HF() {
            super( new EquationTerm( 1, new H2() ), new EquationTerm( 1, new F2() ), new EquationTerm( 2, new HF() ) );
        }
    }

    // CH2O + H2 -> CH3OH
    public static class Equation_CH2O_H2_CH3OH extends TwoReactantsOneProductEquation {
        public Equation_CH2O_H2_CH3OH() {
            super( new EquationTerm( 1, new CH2O() ), new EquationTerm( 1, new H2() ), new EquationTerm( 1, new CH3OH() ) );
        }
    }

    // C2H2 + 2 H2 -> C2H6
    public static class Equation_C2H2_2H2_C2H6 extends TwoReactantsOneProductEquation {
        public Equation_C2H2_2H2_C2H6() {
            super( new EquationTerm( 1, new C2H2() ), new EquationTerm( 2, new H2() ), new EquationTerm( 1, new C2H6() ) );
        }
    }

    // C + O2 -> CO2
    public static class Equation_C_O2_CO2 extends TwoReactantsOneProductEquation {
        public Equation_C_O2_CO2() {
            super( new EquationTerm( 1, new CMolecule() ), new EquationTerm( 1, new O2() ), new EquationTerm( 1, new CO2() ) );
        }
    }

    // 2 C + O2 -> 2 CO
    public static class Equation_2C_O2_2CO extends TwoReactantsOneProductEquation {
        public Equation_2C_O2_2CO() {
            super( new EquationTerm( 2, new CMolecule() ), new EquationTerm( 1, new O2() ), new EquationTerm( 2, new CO() ) );
        }
    }

    // C + 2 S -> CS2
    public static class Equation_C_2S_CS2 extends TwoReactantsOneProductEquation {
        public Equation_C_2S_CS2() {
            super( new EquationTerm( 1, new CMolecule() ), new EquationTerm( 2, new SMolecule() ), new EquationTerm( 1, new CS2() ) );
        }
    }

    // N2 + 3 H2 -> 2 NH3
    public static class Equation_N2_3H2_2NH3 extends TwoReactantsOneProductEquation {
        public Equation_N2_3H2_2NH3() {
            super( BCEStrings.MAKE_AMMONIA, new EquationTerm( 1, new N2() ), new EquationTerm( 3, new H2() ), new EquationTerm( 2, new NH3() ) );
        }
    }

    // 2 N2 + O2 -> 2 N2O
    public static class Equation_2N2_O2_2N2O extends TwoReactantsOneProductEquation {
        public Equation_2N2_O2_2N2O() {
            super( new EquationTerm( 2, new N2() ), new EquationTerm( 1, new O2() ), new EquationTerm( 2, new N2O() ) );
        }
    }

    // P4 + 6 H2 -> 4 PH3
    public static class Equation_P4_6H2_4PH3 extends TwoReactantsOneProductEquation {
        public Equation_P4_6H2_4PH3() {
            super( new EquationTerm( 1, new P4() ), new EquationTerm( 6, new H2() ), new EquationTerm( 4, new PH3() ) );
        }
    }

    // P4 + 6 F2 -> 4 PF3
    public static class Equation_P4_6F2_4PF3 extends TwoReactantsOneProductEquation {
        public Equation_P4_6F2_4PF3() {
            super( new EquationTerm( 1, new P4() ), new EquationTerm( 6, new F2() ), new EquationTerm( 4, new PF3() ) );
        }
    }
}