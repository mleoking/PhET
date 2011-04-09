// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.model;

import java.awt.Image;

import edu.colorado.phet.balancingchemicalequations.model.Atom.C;
import edu.colorado.phet.balancingchemicalequations.model.Atom.Cl;
import edu.colorado.phet.balancingchemicalequations.model.Atom.F;
import edu.colorado.phet.balancingchemicalequations.model.Atom.H;
import edu.colorado.phet.balancingchemicalequations.model.Atom.N;
import edu.colorado.phet.balancingchemicalequations.model.Atom.O;
import edu.colorado.phet.balancingchemicalequations.model.Atom.P;
import edu.colorado.phet.balancingchemicalequations.model.Atom.S;
import edu.colorado.phet.balancingchemicalequations.view.molecules.*;
import edu.colorado.phet.balancingchemicalequations.view.molecules.HorizontalMoleculeNode.CNode;
import edu.colorado.phet.balancingchemicalequations.view.molecules.HorizontalMoleculeNode.CO2Node;
import edu.colorado.phet.balancingchemicalequations.view.molecules.HorizontalMoleculeNode.CONode;
import edu.colorado.phet.balancingchemicalequations.view.molecules.HorizontalMoleculeNode.CS2Node;
import edu.colorado.phet.balancingchemicalequations.view.molecules.HorizontalMoleculeNode.Cl2Node;
import edu.colorado.phet.balancingchemicalequations.view.molecules.HorizontalMoleculeNode.F2Node;
import edu.colorado.phet.balancingchemicalequations.view.molecules.HorizontalMoleculeNode.H2Node;
import edu.colorado.phet.balancingchemicalequations.view.molecules.HorizontalMoleculeNode.N2Node;
import edu.colorado.phet.balancingchemicalequations.view.molecules.HorizontalMoleculeNode.N2ONode;
import edu.colorado.phet.balancingchemicalequations.view.molecules.HorizontalMoleculeNode.NONode;
import edu.colorado.phet.balancingchemicalequations.view.molecules.HorizontalMoleculeNode.O2Node;
import edu.colorado.phet.balancingchemicalequations.view.molecules.HorizontalMoleculeNode.SNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Base class for molecules.
 * Inner classes for each specific molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Molecule {


    private final Atom[] atoms;
    private final Image image;
    private final String symbol;

    /**
     * Constructor.
     * Order of atoms determines their display order and format of symbol.
     * Image must be provided because there is no general method for
     * creating a visual representation based on a list of atoms.
     *
     * @param atoms
     * @param image
     */
    public Molecule( Atom[] atoms, Image image ) {//REVIEW: user varargs to improve readability of usages
        this.symbol = createSymbol( atoms );
        this.image = image;
        this.atoms = atoms;
    }

    protected Molecule( Atom[] atoms, PNode node ) {//REVIEW: user varargs to improve readability of usages
        this( atoms, node.toImage() );
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

    /*
     * Creates a symbol (HTML fragment) based on the list of atoms in the molecule.
     * The atoms must be specified in order of appearance in the symbol.
     * Examples:
     *    [C,C,H,H,H,H] becomes "C<sub>2</sub>H<sub>4</sub>"
     *    [HHO] becomes "H<sub>2</sub>O"
     */
    private static final String createSymbol( Atom[] atoms ) {
        StringBuffer b = new StringBuffer();
        int atomCount = 1;
        for ( int i = 0; i < atoms.length; i++ ) {
            if ( i == 0 ) {
                // first atom is treated differently
                b.append( atoms[i].getSymbol() );
            }
            else if ( atoms[i].getClass().equals( atoms[i-1].getClass() ) ) {
                // this atom is the same as the previous atom
                atomCount++;
            }
            else {
                // this atom is NOT the same
                if ( atomCount > 1 ) {
                    // create a subscript
                    b.append( String.valueOf( atomCount ) );
                }
                atomCount = 1;
                b.append( atoms[i].getSymbol() );
            }
        }
        if ( atomCount > 1 ) {
            // create a subscript for the final atom
            b.append( String.valueOf( atomCount ) );
        }
        return toSubscript( b.toString() );
    }

    /*
     * Handles HTML subscript formatting of molecule symbols.
     * All numbers in a string are assumed to be part of a subscript, and will be enclosed in a <sub> tag.
     * For example, "C2H4" becomes "C<sub>2</sub>H<sub>4</sub>".
     */
    private static final String toSubscript( String inString ) {
        String outString = "";
        boolean sub = false; // are we in a <sub> tag?
        for ( int i = 0; i < inString.length(); i++ ) {
            final char c = inString.charAt( i );
            if ( !sub && Character.isDigit( c ) ) {
                // start the subscript tag when a digit is found
                outString += "<sub>";
                sub = true;
            }
            else if ( sub && !Character.isDigit( c ) ) {
                // end the subscript tag when a non-digit is found
                outString += "</sub>";
                sub = false;
            }
            outString += c;
        }
        // end the subscript tag if inString ends with a digit
        if ( sub ) {
            outString += "</sub>";
            sub = false;
        }
        return outString;
    }

    // There is technically no such thing as a single-atom molecule, but this simplifies the Equation model.
    public static class CMolecule extends Molecule {
        public CMolecule() {
            super( new Atom[] { new C() }, new CNode() );
        }
    }

    public static class C2H2 extends Molecule {
        public C2H2() {
            super( new Atom[] { new C(), new C(), new H(), new H() }, new C2H2Node() );
        }
    }

    public static class C2H5Cl extends Molecule {
        public C2H5Cl() {
            super( new Atom[] { new C(), new C(), new H(), new H(), new H(), new H(), new H(), new Cl() }, new C2H5ClNode() );
        }
    }

    public static class C2H5OH extends Molecule {
        public C2H5OH() {
            super( new Atom[] { new C(), new C(), new H(), new H(), new H(), new H(), new H(), new O(), new H() }, new C2H5OHNode() );
        }
    }

    public static class C2H6 extends Molecule {
        public C2H6() {
            super( new Atom[] { new C(), new C(), new H(), new H(), new H(), new H(), new H(), new H() }, new C2H6Node() );
        }
    }

    public static class C2H4 extends Molecule {
        public C2H4() {
            super( new Atom[] { new C(), new C(), new H(), new H(), new H(), new H() }, new C2H4Node() );
        }
    }

    public static class CH2O extends Molecule {
        public CH2O() {
            super( new Atom[] { new C(), new H(), new H(), new O() }, new CH2ONode() );
        }
    }

    public static class CH3OH extends Molecule {
        public CH3OH() {
            super( new Atom[] { new C(), new H(), new H(), new H(), new O(), new H() }, new CH3OHNode() );
        }
    }

    public static class CH4 extends Molecule {
        public CH4() {
            super( new Atom[] { new C(), new H(), new H(), new H(), new H() }, new CH4Node() );
        }
    }

    public static class Cl2 extends Molecule {
        public Cl2() {
            super( new Atom[] { new Cl(), new Cl() }, new Cl2Node() );
        }
    }

    public static class CO extends Molecule {
        public CO() {
            super( new Atom[] { new C(), new O() }, new CONode() );
        }
    }

    public static class CO2 extends Molecule {
        public CO2() {
            super( new Atom[] { new C(), new O(), new O() }, new CO2Node() );
        }
    }

    public static class CS2 extends Molecule {
        public CS2() {
            super( new Atom[] { new C(), new S(), new S() }, new CS2Node() );
        }
    }

    public static class F2 extends Molecule {
        public F2() {
            super( new Atom[] { new F(), new F() }, new F2Node() );
        }
    }

    public static class H2 extends Molecule {
        public H2() {
            super( new Atom[] { new H(), new H() }, new H2Node() );
        }
    }

    public static class H2O extends Molecule {
        public H2O() {
            super( new Atom[] { new H(), new H(), new O() }, new H2ONode() );
        }
    }

    public static class H2S extends Molecule {
        public H2S() {
            super( new Atom[] { new H(), new H(), new S() }, new H2SNode() );
        }
    }

    public static class HCl extends Molecule {
        public HCl() {
            super( new Atom[] { new H(), new Cl() }, new HClNode() );
        }
    }

    public static class HF extends Molecule {

        public HF() {
            super( new Atom[] { new H(), new F() }, new HFNode() );
        }
    }

    public static class N2 extends Molecule {
        public N2() {
            super( new Atom[] { new N(), new N() }, new N2Node() );
        }
    }

    public static class N2O extends Molecule {
        public N2O() {
            super( new Atom[] { new N(), new N(), new O() }, new N2ONode() );
        }
    }

    public static class NH3 extends Molecule {
        public NH3() {
            super( new Atom[] { new N(), new H(), new H(), new H() }, new NH3Node() );
        }
    }

    public static class NO extends Molecule {
        public NO() {
            super( new Atom[] { new N(), new O() }, new NONode() );
        }
    }

    public static class NO2 extends Molecule {
        public NO2() {
            super( new Atom[] { new N(), new O(), new O() }, new NO2Node() );
        }
    }

    public static class O2 extends Molecule {
        public O2() {
            super( new Atom[] { new O(), new O() }, new O2Node() );
        }
    }

    public static class OF2 extends Molecule {
        public OF2() {
            super( new Atom[] { new O(), new F(), new F() }, new OF2Node() );
        }
    }

    public static class P4 extends Molecule {
        public P4() {
            super( new Atom[] { new P(), new P(), new P(), new P() }, new P4Node() );
        }
    }

    public static class PCl3 extends Molecule {
        public PCl3() {
            super( new Atom[] { new P(), new Cl(), new Cl(), new Cl() }, new PCl3Node() );
        }
    }

    public static class PCl5 extends Molecule {
        public PCl5() {
            super( new Atom[] { new P(), new Cl(), new Cl(), new Cl(), new Cl(), new Cl() }, new PCl5Node() );
        }
    }

    public static class PF3 extends Molecule {
        public PF3() {
            super( new Atom[] { new P(), new F(), new F(), new F() }, new PF3Node() );
        }
    }

    public static class PH3 extends Molecule {
        public PH3() {
            super( new Atom[] { new P(), new H(), new H(), new H() }, new PH3Node() );
        }
    }

    // There is technically no such thing as a single-atom molecule, but this simplifies the Equation model.
    public static class SMolecule extends Molecule {
        public SMolecule() {
            super( new Atom[] { new S() }, new SNode() );
        }
    }

    public static class SO2 extends Molecule {
        public SO2() {
            super( new Atom[] { new S(), new O(), new O() }, new SO2Node() );
        }
    }

    public static class SO3 extends Molecule {
        public SO3() {
            super( new Atom[] { new S(), new O(), new O(), new O() }, new SO3Node() );
        }
    }
}
