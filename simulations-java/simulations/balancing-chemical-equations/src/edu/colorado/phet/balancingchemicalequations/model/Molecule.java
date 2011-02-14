// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.model;

import java.awt.Image;

import edu.colorado.phet.balancingchemicalequations.model.Atom.C;
import edu.colorado.phet.balancingchemicalequations.model.Atom.Cl;
import edu.colorado.phet.balancingchemicalequations.model.Atom.F;
import edu.colorado.phet.balancingchemicalequations.model.Atom.H;
import edu.colorado.phet.balancingchemicalequations.model.Atom.N;
import edu.colorado.phet.balancingchemicalequations.model.Atom.O;
import edu.colorado.phet.balancingchemicalequations.view.molecules.*;
import edu.umd.cs.piccolo.PNode;

/**
 * Base class for molecules.
 * Inner classes for each specific molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Molecule {

    private final String symbol;
    private final Image image;
    private final Atom[] atoms; // order of the atoms determines their display order

    public Molecule( String symbol, Image image, Atom[] atoms ) {
        this.symbol = toSubscript( symbol );
        this.image = image;
        this.atoms = atoms;
    }

    protected Molecule( String symbol, PNode node, Atom[] atoms ) {
        this( symbol, node.toImage(), atoms );
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

    /*
     * Handles HTML subscript formatting of molecule symbols.
     * All numbers in a string are assumed to be part of a subscript, and will be enclosed in a <sub> tag.
     * For example, "C2H4" is converted to "C<sub>2</sub>H<sub>4</sub>".
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
        // end the subscript tag if the string ends with a digit
        if ( sub ) {
            outString += "</sub>";
            sub = false;
        }
        return outString;
    }

    public static class CH4 extends Molecule {

        public CH4() {
            super( "CH4", new CH4Node(), new Atom[] { new C(), new H(), new H(), new H(), new H() } );
        }
    }

    public static class Cl2 extends Molecule {

        public Cl2() {
            super( "Cl2", new Cl2Node(), new Atom[] { new Cl(), new Cl() } );
        }
    }

    public static class CO2 extends Molecule {

        public CO2() {
            super( "CO2", new CO2Node(), new Atom[] { new C(), new O(), new O() } );
        }
    }

    public static class F2 extends Molecule {

        public F2() {
            super( "F2", new F2Node(), new Atom[] { new F(), new F() } );
        }
    }

    public static class H2 extends Molecule {

        public H2() {
            super( "H2", new H2Node(), new Atom[] { new H(), new H() } );
        }
    }

    public static class H2O extends Molecule {

        public H2O() {
            super( "H2O", new H2ONode(), new Atom[] { new H(), new H(), new O() } );
        }
    }

    public static class HCl extends Molecule {

        public HCl() {
            super( "HCl", new HClNode(), new Atom[] { new H(), new Cl() } );
        }
    }

    public static class HF extends Molecule {

        public HF() {
            super( "HF", new HFNode(), new Atom[] { new H(), new F() } );
        }
    }

    public static class N2 extends Molecule {

        public N2() {
            super( "N2", new N2Node(), new Atom[] { new N(), new N() } );
        }
    }

    public static class NH3 extends Molecule {

        public NH3() {
            super( "NH3", new NH3Node(), new Atom[] { new N(), new H(), new H(), new H() } );
        }
    }

    public static class O2 extends Molecule {

        public O2() {
            super( "O2", new O2Node(), new Atom[] { new O(), new O() } );
        }
    }
}
