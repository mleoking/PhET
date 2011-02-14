// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.model;

import java.awt.Image;

import edu.colorado.phet.balancingchemicalequations.BCEImages;
import edu.colorado.phet.balancingchemicalequations.BCESymbols;
import edu.colorado.phet.balancingchemicalequations.model.Atom.C;
import edu.colorado.phet.balancingchemicalequations.model.Atom.F;
import edu.colorado.phet.balancingchemicalequations.model.Atom.H;
import edu.colorado.phet.balancingchemicalequations.model.Atom.N;
import edu.colorado.phet.balancingchemicalequations.model.Atom.O;

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
        this.symbol = symbol;
        this.image = image;
        this.atoms = atoms;
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

    public static class CH4 extends Molecule {

        public CH4() {
            super( BCESymbols.CH4, BCEImages.CH4, new Atom[] { new C(), new H(), new H(), new H(), new H() } );
        }
    }

    public static class CO2 extends Molecule {

        public CO2() {
            super( BCESymbols.CO2, BCEImages.CO2, new Atom[] { new C(), new O(), new O() } );
        }
    }

    public static class F2 extends Molecule {

        public F2() {
            super( BCESymbols.F2, BCEImages.F2, new Atom[] { new F(), new F() } );
        }
    }

    public static class H2 extends Molecule {

        public H2() {
            super( BCESymbols.H2, BCEImages.H2, new Atom[] { new H(), new H() } );
        }
    }

    public static class H2O extends Molecule {

        public H2O() {
            super( BCESymbols.H2O, BCEImages.H2O, new Atom[] { new H(), new H(), new O() } );
        }
    }

    public static class HF extends Molecule {

        public HF() {
            super( BCESymbols.HF, BCEImages.HF, new Atom[] { new H(), new F() } );
        }
    }

    public static class N2 extends Molecule {

        public N2() {
            super( BCESymbols.N2, BCEImages.N2, new Atom[] { new N(), new N() } );
        }
    }

    public static class NH3 extends Molecule {

        public NH3() {
            super( BCESymbols.NH3, BCEImages.NH3, new Atom[] { new N(), new H(), new H(), new H() } );
        }
    }

    public static class O2 extends Molecule {

        public O2() {
            super( BCESymbols.O2, BCEImages.O2, new Atom[] { new O(), new O() } );
        }
    }
}
