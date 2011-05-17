// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.model;

import java.awt.*;

import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.chemistry.molecules.*;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.*;
import edu.colorado.phet.chemistry.utils.ChemUtils;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.chemistry.model.Element.*;

/**
 * Base class for molecules.
 * Inner classes for each specific molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Molecule {

    private final Image image;
    private final Element[] atoms;
    private final String symbol;

    /**
     * Constructor.
     * Order of atoms determines their display order and format of symbol.
     * Image must be provided because there is no general method for
     * creating a visual representation based on a list of atoms.
     *
     * @param image
     * @param atoms
     */
    public Molecule( Image image, Element... atoms ) {
        this.image = image;
        this.atoms = atoms;
        this.symbol = ChemUtils.createSymbol( atoms );
    }

    protected Molecule( PNode node, Element... atoms ) {
        this( node.toImage(), atoms );
    }

    public String getSymbol() {
        return symbol;
    }

    public Image getImage() {
        return image;
    }

    public Element[] getAtoms() {
        return atoms;
    }

    /**
     * Any molecule with more than 5 atoms is considered "big".
     * This affects degree of difficulty in the Game.
     *
     * @return
     */
    public boolean isBig() {
        return atoms.length > 5;
    }

    // There is technically no such thing as a single-atom molecule, but this simplifies the Equation model.
    public static class CMolecule extends Molecule {
        public CMolecule() {
            super( new CNode(), C );
        }
    }

    public static class C2H2 extends Molecule {
        public C2H2() {
            super( new C2H2Node(), C, C, H, H );
        }
    }

    public static class C2H5Cl extends Molecule {
        public C2H5Cl() {
            super( new C2H5ClNode(), C, C, H, H, H, H, H, Cl );
        }
    }

    public static class C2H5OH extends Molecule {
        public C2H5OH() {
            super( new C2H5OHNode(), C, C, H, H, H, H, H, O, H );
        }
    }

    public static class C2H6 extends Molecule {
        public C2H6() {
            super( new C2H6Node(), C, C, H, H, H, H, H, H );
        }
    }

    public static class C2H4 extends Molecule {
        public C2H4() {
            super( new C2H4Node(), C, C, H, H, H, H );
        }
    }

    public static class CH2O extends Molecule {
        public CH2O() {
            super( new CH2ONode(), C, H, H, O );
        }
    }

    public static class CH3OH extends Molecule {
        public CH3OH() {
            super( new CH3OHNode(), C, H, H, H, O, H );
        }
    }

    public static class CH4 extends Molecule {
        public CH4() {
            super( new CH4Node(), C, H, H, H, H );
        }
    }

    public static class Cl2 extends Molecule {
        public Cl2() {
            super( new Cl2Node(), Cl, Cl );
        }
    }

    public static class CO extends Molecule {
        public CO() {
            super( new CONode(), C, O );
        }
    }

    public static class CO2 extends Molecule {
        public CO2() {
            super( new CO2Node(), C, O, O );
        }
    }

    public static class CS2 extends Molecule {
        public CS2() {
            super( new CS2Node(), C, S, S );
        }
    }

    public static class F2 extends Molecule {
        public F2() {
            super( new F2Node(), F, F );
        }
    }

    public static class H2 extends Molecule {
        public H2() {
            super( new H2Node(), H, H );
        }
    }

    public static class H2O extends Molecule {
        public H2O() {
            super( new H2ONode(), H, H, O );
        }
    }

    public static class H2S extends Molecule {
        public H2S() {
            super( new H2SNode(), H, H, S );
        }
    }

    public static class HCl extends Molecule {
        public HCl() {
            super( new HClNode(), H, Cl );
        }
    }

    public static class HF extends Molecule {

        public HF() {
            super( new HFNode(), H, F );
        }
    }

    public static class N2 extends Molecule {
        public N2() {
            super( new N2Node(), N, N );
        }
    }

    public static class N2O extends Molecule {
        public N2O() {
            super( new N2ONode(), N, N, O );
        }
    }

    public static class NH3 extends Molecule {
        public NH3() {
            super( new NH3Node(), N, H, H, H );
        }
    }

    public static class NO extends Molecule {
        public NO() {
            super( new NONode(), N, O );
        }
    }

    public static class NO2 extends Molecule {
        public NO2() {
            super( new NO2Node(), N, O, O );
        }
    }

    public static class O2 extends Molecule {
        public O2() {
            super( new O2Node(), O, O );
        }
    }

    public static class OF2 extends Molecule {
        public OF2() {
            super( new OF2Node(), O, F, F );
        }
    }

    public static class P4 extends Molecule {
        public P4() {
            super( new P4Node(), P, P, P, P );
        }
    }

    public static class PCl3 extends Molecule {
        public PCl3() {
            super( new PCl3Node(), P, Cl, Cl, Cl );
        }
    }

    public static class PCl5 extends Molecule {
        public PCl5() {
            super( new PCl5Node(), P, Cl, Cl, Cl, Cl, Cl );
        }
    }

    public static class PF3 extends Molecule {
        public PF3() {
            super( new PF3Node(), P, F, F, F );
        }
    }

    public static class PH3 extends Molecule {
        public PH3() {
            super( new PH3Node(), P, H, H, H );
        }
    }

    // There is technically no such thing as a single-atom molecule, but this simplifies the Equation model.
    public static class SMolecule extends Molecule {
        public SMolecule() {
            super( new SNode(), S );
        }
    }

    public static class SO2 extends Molecule {
        public SO2() {
            super( new SO2Node(), S, O, O );
        }
    }

    public static class SO3 extends Molecule {
        public SO3() {
            super( new SO3Node(), S, O, O, O );
        }
    }
}
