// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.model;

import java.awt.Color;
import java.awt.Image;

import edu.colorado.phet.balancingchemicalequations.BCEColors;
import edu.colorado.phet.balancingchemicalequations.BCEImages;
import edu.colorado.phet.balancingchemicalequations.BCESymbols;

/**
 * Base class for atoms.
 * Inner classes for each specific atom.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Atom {

    public static enum AtomType { C, H, N, O, S };

    private final AtomType atomType;
    private final String symbol;
    private final Color color;
    private final Image image;

    public Atom( AtomType atomType, String symbol, Color color, Image image ) {
        this.atomType = atomType;
        this.symbol = symbol;
        this.color = color;
        this.image = image;
    }

    public AtomType getAtomType() {
        return atomType;
    }

    public String getSymbol() {
        return symbol;
    }

    public Color getColor() {
        return color;
    }

    public Image getImage() {
        return image;
    }

    public static class C extends Atom {
        public C() {
           super( AtomType.C, BCESymbols.C, BCEColors.C, BCEImages.C );
        }
    }

    public static class H extends Atom {
        public H() {
           super( AtomType.H, BCESymbols.H, BCEColors.H, BCEImages.H );
        }
    }

    public static class N extends Atom {
        public N() {
           super( AtomType.N, BCESymbols.N, BCEColors.N, BCEImages.N );
        }
    }

    public static class O extends Atom {
        public O() {
           super( AtomType.O, BCESymbols.O, BCEColors.O, BCEImages.O );
        }
    }

    public static class S extends Atom {
        public S() {
           super( AtomType.S, BCESymbols.S, BCEColors.S, BCEImages.S );
        }
    }
}
