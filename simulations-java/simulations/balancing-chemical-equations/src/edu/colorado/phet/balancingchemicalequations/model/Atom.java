// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.model;

import java.awt.Color;

/**
 * Base class for atoms.
 * Inner classes for each specific atom.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Atom {

    private final String symbol;
    private final Color color;

    public Atom( String symbol, Color color ) {
        this.symbol = symbol;
        this.color = color;
    }

    public String getSymbol() {
        return symbol;
    }

    public Color getColor() {
        return color;
    }

    public static class C extends Atom {
        public C() {
           super( "C", new Color( 178, 178, 178 ) );
        }
    }

    public static class Cl extends Atom {
        public Cl() {
           super( "Cl", new Color( 153, 242, 57 ) );
        }
    }

    public static class F extends Atom {
        public F() {
           super( "F", new Color( 247, 255, 74 ) );
        }
    }

    public static class H extends Atom {
        public H() {
           super( "H", Color.WHITE );
        }
    }

    public static class N extends Atom {
        public N() {
           super( "N", Color.BLUE );
        }
    }

    public static class O extends Atom {
        public O() {
           super( "O", new Color( 255, 85, 0 ) );
        }
    }

    public static class P extends Atom {
        public P() {
           super( "P", new Color( 255, 0, 255 ) );
        }
    }

    public static class S extends Atom {
        public S() {
           super( "S", new Color( 212, 181, 59 ) );
        }
    }
}
