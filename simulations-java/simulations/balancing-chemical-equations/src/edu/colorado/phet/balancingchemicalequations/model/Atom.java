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

    private final String symbol;
    private final Color color;
    private final Image image;

    public Atom( String symbol, Color color, Image image ) {
        this.symbol = symbol;
        this.color = color;
        this.image = image;
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
           super( BCESymbols.C, BCEColors.C, BCEImages.C );
        }
    }

    public static class H extends Atom {
        public H() {
           super( BCESymbols.H, BCEColors.H, BCEImages.H );
        }
    }

    public static class N extends Atom {
        public N() {
           super( BCESymbols.N, BCEColors.N, BCEImages.N );
        }
    }

    public static class O extends Atom {
        public O() {
           super( BCESymbols.O, BCEColors.O, BCEImages.O );
        }
    }

    public static class S extends Atom {
        public S() {
           super( BCESymbols.S, BCEColors.S, BCEImages.S );
        }
    }
}
