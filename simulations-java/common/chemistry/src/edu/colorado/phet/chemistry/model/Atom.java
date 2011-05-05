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
    private final double electronegativity; // dimensionless, see https://secure.wikimedia.org/wikipedia/en/wiki/Electronegativity
    private final double atomicWeight; // in atomic mass units (u). from http://www.webelements.com/periodicity/atomic_weight/
    private final Color color; // color used in visual representations

    public Atom( String symbol, double radius, double electronegativity, double atomicWeight, Color color ) {
        this.symbol = symbol;
        this.radius = radius;
        this.electronegativity = electronegativity;
        this.atomicWeight = atomicWeight;
        this.color = color;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getRadius() {
        return radius;
    }

    public double getElectronegativity() {
        return electronegativity;
    }

    public double getAtomicWeight() {
        return atomicWeight;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return symbol;
    }

    public static class B extends Atom {
        public B() {
            super( "B", 85, 2.04, 10.811, new Color( 255, 170, 119 ) );// peach/salmon colored, CPK coloring
        }
    }

    public static class Br extends Atom {
        public Br() {
            super( "Br", 114, 2.96, 79.904, new Color( 190, 30, 20 ) );// brown
        }
    }

    public static class C extends Atom {
        public C() {
            super( "C", 77, 2.55, 12.0107, new Color( 178, 178, 178 ) );
        }
    }

    public static class Cl extends Atom {
        public Cl() {
            super( "Cl", 100, 3.16, 35.4527, new Color( 153, 242, 57 ) );
        }
    }

    public static class F extends Atom {
        public F() {
            super( "F", 72, 3.98, 18.9984032, new Color( 247, 255, 74 ) );
        }
    }

    public static class H extends Atom {
        public H() {
            super( "H", 37, 2.20, 1.00794, Color.WHITE );
        }
    }

    public static class I extends Atom {
        public I() {
            super( "I", 133, 2.66, 126.90447, new Color( 0x940094 ) ); // dark violet, CPK coloring
        }
    }

    public static class N extends Atom {
        public N() {
            super( "N", 75, 3.04, 14.00674, Color.BLUE );
        }
    }

    public static class O extends Atom {
        public O() {
            super( "O", 73, 3.44, 15.9994, new Color( 255, 85, 0 ) );
        }
    }

    public static class P extends Atom {
        public P() {
            super( "P", 110, 2.19, 30.973762, new Color( 255, 128, 0 ) );
        }
    }

    public static class S extends Atom {
        public S() {
            super( "S", 103, 2.58, 32.066, new Color( 212, 181, 59 ) );
        }
    }

    public static class Si extends Atom {
        public Si() {
            super( "Si", 118, 1.90, 28.0855, new Color( 240, 200, 160 ) ); // tan, Jmol coloring listed from https://secure.wikimedia.org/wikipedia/en/wiki/CPK_coloring
        }
    }

    public boolean isSameTypeOfAtom( Atom atom ) {
        return atom.getSymbol().equals( this.getSymbol() );
    }
}
