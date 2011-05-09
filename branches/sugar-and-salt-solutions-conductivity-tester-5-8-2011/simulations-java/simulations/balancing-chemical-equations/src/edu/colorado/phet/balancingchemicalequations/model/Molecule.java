// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.model;

import java.awt.Image;

import edu.colorado.phet.chemistry.model.Atom;
import edu.colorado.phet.chemistry.model.Atom.C;
import edu.colorado.phet.chemistry.model.Atom.Cl;
import edu.colorado.phet.chemistry.model.Atom.F;
import edu.colorado.phet.chemistry.model.Atom.H;
import edu.colorado.phet.chemistry.model.Atom.N;
import edu.colorado.phet.chemistry.model.Atom.O;
import edu.colorado.phet.chemistry.model.Atom.P;
import edu.colorado.phet.chemistry.model.Atom.S;

import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.CNode;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.CO2Node;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.CONode;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.CS2Node;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.Cl2Node;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.F2Node;
import edu.colorado.phet.chemistry.molecules.*;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.H2Node;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.N2Node;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.N2ONode;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.NONode;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.O2Node;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.SNode;
import edu.colorado.phet.chemistry.utils.ChemUtils;
import edu.umd.cs.piccolo.PNode;

/**
 * Base class for molecules.
 * Inner classes for each specific molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Molecule {

    private final Image image;
    private final Atom[] atoms;
    private final String symbol;

    /**
     * Constructor.
     * Order of atoms determines their display order and format of symbol.
     * Image must be provided because there is no general method for
     * creating a visual representation based on a list of atoms.
     * @param image
     * @param atoms
     */
    public Molecule( Image image, Atom... atoms ) {
        this.image = image;
        this.atoms = atoms;
        this.symbol = ChemUtils.createSymbol( atoms );
    }

    protected Molecule( PNode node, Atom... atoms ) {
        this( node.toImage(), atoms );
    }

    public String getSymbol() {
        return symbol;
    }

    public Image getImage() {
        return image;
    }

    public Atom[] getAtoms() {
        return atoms;
    }

    /**
     * Any molecule with more than 5 atoms is considered "big".
     * This affects degree of difficulty in the Game.
     * @return
     */
    public boolean isBig() {
        return atoms.length > 5;
    }

    // There is technically no such thing as a single-atom molecule, but this simplifies the Equation model.
    public static class CMolecule extends Molecule {
        public CMolecule() {
            super( new CNode(), new C() );
        }
    }

    public static class C2H2 extends Molecule {
        public C2H2() {
            super( new C2H2Node(), new C(), new C(), new H(), new H() );
        }
    }

    public static class C2H5Cl extends Molecule {
        public C2H5Cl() {
            super( new C2H5ClNode(), new C(), new C(), new H(), new H(), new H(), new H(), new H(), new Cl() );
        }
    }

    public static class C2H5OH extends Molecule {
        public C2H5OH() {
            super( new C2H5OHNode(), new C(), new C(), new H(), new H(), new H(), new H(), new H(), new O(), new H() );
        }
    }

    public static class C2H6 extends Molecule {
        public C2H6() {
            super( new C2H6Node(), new C(), new C(), new H(), new H(), new H(), new H(), new H(), new H() );
        }
    }

    public static class C2H4 extends Molecule {
        public C2H4() {
            super( new C2H4Node(), new C(), new C(), new H(), new H(), new H(), new H() );
        }
    }

    public static class CH2O extends Molecule {
        public CH2O() {
            super( new CH2ONode(), new C(), new H(), new H(), new O() );
        }
    }

    public static class CH3OH extends Molecule {
        public CH3OH() {
            super( new CH3OHNode(), new C(), new H(), new H(), new H(), new O(), new H() );
        }
    }

    public static class CH4 extends Molecule {
        public CH4() {
            super( new CH4Node(), new C(), new H(), new H(), new H(), new H() );
        }
    }

    public static class Cl2 extends Molecule {
        public Cl2() {
            super( new Cl2Node(), new Cl(), new Cl() );
        }
    }

    public static class CO extends Molecule {
        public CO() {
            super( new CONode(), new C(), new O() );
        }
    }

    public static class CO2 extends Molecule {
        public CO2() {
            super( new CO2Node(), new C(), new O(), new O() );
        }
    }

    public static class CS2 extends Molecule {
        public CS2() {
            super( new CS2Node(), new C(), new S(), new S() );
        }
    }

    public static class F2 extends Molecule {
        public F2() {
            super( new F2Node(), new F(), new F() );
        }
    }

    public static class H2 extends Molecule {
        public H2() {
            super( new H2Node(), new H(), new H() );
        }
    }

    public static class H2O extends Molecule {
        public H2O() {
            super( new H2ONode(), new H(), new H(), new O() );
        }
    }

    public static class H2S extends Molecule {
        public H2S() {
            super( new H2SNode(), new H(), new H(), new S() );
        }
    }

    public static class HCl extends Molecule {
        public HCl() {
            super( new HClNode(), new H(), new Cl() );
        }
    }

    public static class HF extends Molecule {

        public HF() {
            super( new HFNode(), new H(), new F() );
        }
    }

    public static class N2 extends Molecule {
        public N2() {
            super( new N2Node(), new N(), new N() );
        }
    }

    public static class N2O extends Molecule {
        public N2O() {
            super( new N2ONode(), new N(), new N(), new O() );
        }
    }

    public static class NH3 extends Molecule {
        public NH3() {
            super( new NH3Node(), new N(), new H(), new H(), new H() );
        }
    }

    public static class NO extends Molecule {
        public NO() {
            super( new NONode(), new N(), new O() );
        }
    }

    public static class NO2 extends Molecule {
        public NO2() {
            super( new NO2Node(), new N(), new O(), new O() );
        }
    }

    public static class O2 extends Molecule {
        public O2() {
            super( new O2Node(), new O(), new O() );
        }
    }

    public static class OF2 extends Molecule {
        public OF2() {
            super( new OF2Node(), new O(), new F(), new F() );
        }
    }

    public static class P4 extends Molecule {
        public P4() {
            super( new P4Node(), new P(), new P(), new P(), new P() );
        }
    }

    public static class PCl3 extends Molecule {
        public PCl3() {
            super( new PCl3Node(), new P(), new Cl(), new Cl(), new Cl() );
        }
    }

    public static class PCl5 extends Molecule {
        public PCl5() {
            super( new PCl5Node(), new P(), new Cl(), new Cl(), new Cl(), new Cl(), new Cl() );
        }
    }

    public static class PF3 extends Molecule {
        public PF3() {
            super( new PF3Node(), new P(), new F(), new F(), new F() );
        }
    }

    public static class PH3 extends Molecule {
        public PH3() {
            super( new PH3Node(), new P(), new H(), new H(), new H() );
        }
    }

    // There is technically no such thing as a single-atom molecule, but this simplifies the Equation model.
    public static class SMolecule extends Molecule {
        public SMolecule() {
            super( new SNode(), new S() );
        }
    }

    public static class SO2 extends Molecule {
        public SO2() {
            super( new SO2Node(), new S(), new O(), new O() );
        }
    }

    public static class SO3 extends Molecule {
        public SO3() {
            super( new SO3Node(), new S(), new O(), new O(), new O() );
        }
    }
}
