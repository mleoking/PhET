// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.chemistry.model;

import java.awt.*;

/**
 * Base class for atoms.
 * Inner classes for each specific atom.
 * <p/>
 * Reference for atom radii:
 * Chemistry: The Molecular Nature of Matter and Change, 5th Edition, Silberberg.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Atom {

    private final String symbol;
    private final double radius; // picometers
    private final Color color; // color used in visual representations

    public Atom( String symbol, double radius, Color color ) {
        this.symbol = symbol;
        this.radius = radius;
        this.color = color;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getRadius() {
        return radius;
    }

    public Color getColor() {
        return color;
    }

    @Override public String toString() {
        return symbol;
    }

    // TODO (JO): can we consider static instances for accessing properties without instance creation?

    public static class C extends Atom {
        public C() {
            super( "C", 77, new Color( 178, 178, 178 ) );
        }
    }

    public static class Cl extends Atom {
        public Cl() {
            super( "Cl", 100, new Color( 153, 242, 57 ) );
        }
    }

    public static class F extends Atom {
        public F() {
            super( "F", 72, new Color( 247, 255, 74 ) );
        }
    }

    public static class H extends Atom {
        public H() {
            super( "H", 37, Color.WHITE );
        }
    }

    public static class N extends Atom {
        public N() {
            super( "N", 75, Color.BLUE );
        }
    }

    public static class O extends Atom {
        public O() {
            super( "O", 73, new Color( 255, 85, 0 ) );
        }
    }

    public static class P extends Atom {
        public P() {
            super( "P", 110, new Color( 255, 0, 255 ) );
        }
    }

    public static class S extends Atom {
        public S() {
            super( "S", 103, new Color( 212, 181, 59 ) );
        }
    }

    public boolean isSameTypeOfAtom( Atom atom ) {
        return atom.getSymbol().equals( this.getSymbol() ) && atom.getRadius() == this.getRadius() && atom.getColor().equals( this.getColor() );
    }
}
