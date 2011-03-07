// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.model;

import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.C2H2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.C2H4;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.C2H5Cl;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.C2H5OH;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.C2H6;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.CH4;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.CMolecule;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.CO;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.CO2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.CS2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.Cl2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.F2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.H2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.H2O;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.H2S;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.HCl;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.HF;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.N2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.NH3;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.NO;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.NO2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.O2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.OF2;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.SMolecule;
import edu.colorado.phet.balancingchemicalequations.model.Molecule.SO2;

/**
 * Base class for displacement equations.
 * In a displacement reactions, compounds exchange bonds or ions to form different compounds.
 * All displacement equations in this sim have 2 reactants and 2 products.
 * <p>
 * This base class adds no new functionality to Equation, it simply provides convenient constructors.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DisplacementEquation extends Equation {

    /*
     * Convenience constructor.
     *
     * @param r1 balanced coefficient for reactant1
     * @param reactant1
     * @param r2 balanced coefficient for reactant2
     * @param reactant2
     * @param p1 balanced coefficient for product1
     * @param product1
     * @param p2 balanced coefficient for product2
     * @param product2
     */
    private DisplacementEquation( int r1, Molecule reactant1, int r2, Molecule reactant2, int p1, Molecule product1, int p2, Molecule product2 ) {
        super( new EquationTerm[] { new EquationTerm( r1, reactant1 ), new EquationTerm( r2, reactant2 ) }, new EquationTerm[] { new EquationTerm( p1, product1 ), new EquationTerm( p2, product2 ) } );
    }

    // CH4 + 2 O2 -> CO2 + 2 H2O
    public static class Displacement_CH4_2O2_CO2_2H2O extends DisplacementEquation {
        public Displacement_CH4_2O2_CO2_2H2O() {
            super( 1, new CH4(), 2, new O2(), 1, new CO2(), 2, new H2O() );
        }

        @Override
        public String getName() {
            return BCEStrings.COMBUST_METHANE; // this equation has a fancy name
        }
    }

    // 2 C + 2 H2O -> CH4 + CO2
    public static class Displacement_2C_2H2O_CH4_CO2 extends DisplacementEquation {
        public Displacement_2C_2H2O_CH4_CO2() {
            super( 2, new CMolecule(), 2, new H2O(), 1, new CH4(), 1, new CO2() );
        }
    }

    // CH4 + H2O -> 3 H2 + CO
    public static class Displacement_CH4_H2O_3H2_CO extends DisplacementEquation {
        public Displacement_CH4_H2O_3H2_CO() {
            super( 1, new CH4(), 1, new H2O(), 3, new H2(), 1, new CO() );
        }
    }

    // C2H4 + 3 O2 -> 2 CO2 + 2 H2O
    public static class Displacement_C2H4_3O2_2CO2_2H2O extends DisplacementEquation {
        public Displacement_C2H4_3O2_2CO2_2H2O() {
            super( 1, new C2H4(), 3, new O2(), 2, new CO2(), 2, new H2O() );
        }
    }

    // C2H6 + Cl2 -> C2H5Cl + HCl
    public static class Displacement_C2H6_Cl2_C2H5Cl_HCl extends DisplacementEquation {
        public Displacement_C2H6_Cl2_C2H5Cl_HCl() {
            super( 1, new C2H6(), 1, new Cl2(), 1, new C2H5Cl(), 1, new HCl() );
        }
    }

    // CH4 + 4 S -> CS2 + 2 H2S
    public static class Displacement_CH4_4S_CS2_2H2S extends DisplacementEquation {
        public Displacement_CH4_4S_CS2_2H2S() {
            super( 1, new CH4(), 4, new SMolecule(), 1, new CS2(), 2, new H2S() );
        }
    }

    // CS2 + 3 O2 -> CO2 + 2 SO2
    public static class Displacement_CS2_3O2_CO2_2SO2 extends DisplacementEquation {
        public Displacement_CS2_3O2_CO2_2SO2() {
            super( 1, new CS2(), 3, new O2(), 1, new CO2(), 2, new SO2() );
        }
    }

    // SO2 + 2 H2 -> S + 2 H2O
    public static class Displacement_SO2_2H2_S_2H2O extends DisplacementEquation {
        public Displacement_SO2_2H2_S_2H2O() {
            super( 1, new SO2(), 2, new H2(), 1, new SMolecule(), 2, new H2O() );
        }
    }

    // SO2 + 3 H2 -> H2S + 2 H2O
    public static class Displacement_SO2_3H2_H2S_2H2O extends DisplacementEquation {
        public Displacement_SO2_3H2_H2S_2H2O() {
            super( 1, new SO2(), 3, new H2(), 1, new H2S(), 2, new H2O() );
        }
    }

    // 2 F2 + H2O -> OF2 + 2 HF
    public static class Displacement_2F2_H2O_OF2_2HF extends DisplacementEquation {
        public Displacement_2F2_H2O_OF2_2HF() {
            super( 2, new F2(), 1, new H2O(), 1, new OF2(), 2, new HF() );
        }
    }

    // OF2 + H2O -> O2 + 2 HF
    public static class Displacement_OF2_H2O_O2_2HF extends DisplacementEquation {
        public Displacement_OF2_H2O_O2_2HF() {
            super( 1, new OF2(), 1, new H2O(), 1, new O2(), 2, new HF() );
        }
    }

    // 2 C2H6 + 7 O2 -> 4 CO2 + 6 H2O
    public static class Displacement_2C2H6_7O2_4CO2_6H2O extends DisplacementEquation {
        public Displacement_2C2H6_7O2_4CO2_6H2O() {
            super( 2, new C2H6(), 7, new O2(), 4, new CO2(), 6, new H2O() );
        }
    }

    // 2 C2H2 + 5 O2 -> 4 CO2 + 2 H2O
    public static class Displacement_2C2H2_5O2_4CO2_2H2O extends DisplacementEquation {
        public Displacement_2C2H2_5O2_4CO2_2H2O() {
            super( 2, new C2H2(), 5, new O2(), 4, new CO2(), 2, new H2O() );
        }
    }

    // C2H5OH + 3 O2 -> 2 CO2 + 3 H2O
    public static class Displacement_C2H5OH_3O2_2CO2_3H2O extends DisplacementEquation {
        public Displacement_C2H5OH_3O2_2CO2_3H2O() {
            super( 1, new C2H5OH(), 3, new O2(), 2, new CO2(), 3, new H2O() );
        }
    }

    // 4 NH3 + 3 O2 -> 2 N2 + 6 H2O
    public static class Displacement_4NH3_3O2_2N2_6H2O extends DisplacementEquation {
        public Displacement_4NH3_3O2_2N2_6H2O() {
            super( 4, new NH3(), 3, new O2(), 2, new N2(), 6, new H2O() );
        }
    }

    // 4 NH3 + 5 O2 -> 4 NO + 6 H2O
    public static class Displacement_4NH3_5O2_4NO_6H2O extends DisplacementEquation {
        public Displacement_4NH3_5O2_4NO_6H2O() {
            super( 4, new NH3(), 5, new O2(), 4, new NO(), 6, new H2O() );
        }
    }

    // 4 NH3 + 7 O2 -> 4 NO2 + 6 H2O
    public static class Displacement_4NH3_7O2_4NO2_6H2O extends DisplacementEquation {
        public Displacement_4NH3_7O2_4NO2_6H2O() {
            super( 4, new NH3(), 7, new O2(), 4, new NO2(), 6, new H2O() );
        }
    }

    // 4 NH3 + 6 NO -> 5 N2 + 6 H2O
    public static class Displacement_4NH3_6NO_5N2_6H2O extends DisplacementEquation {
        public Displacement_4NH3_6NO_5N2_6H2O() {
            super( 4, new NH3(), 6, new NO(), 5, new N2(), 6, new H2O() );
        }
    }
}
