// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.chemistry.model;

import java.awt.*;

/**
 * Class for actual atom state (symbol, radius, etc.).
 * Inner classes for each specific atom.
 * <p/>
 * Reference for atom radii:
 * Chemistry: The Molecular Nature of Matter and Change, 5th Edition, Silberberg.
 * <p/>
 * TODO: consider renaming to AtomState
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @author Jonathan Olson
 */
public class Atom extends AbstractAtom {

    private final String symbol;
    private final double radius; // picometers
    private final double electronegativity; // dimensionless, see https://secure.wikimedia.org/wikipedia/en/wiki/Electronegativity
    private final double atomicWeight; // in atomic mass units (u). from http://www.webelements.com/periodicity/atomic_weight/
    private final Color color; // color used in visual representations

    private Atom( String symbol, double radius, double electronegativity, double atomicWeight, Color color ) {
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

    /*---------------------------------------------------------------------------*
    * atoms
    *----------------------------------------------------------------------------*/

    public static final Atom B = new Atom( "B", 85, 2.04, 10.811, new Color( 255, 170, 119 ) ); // peach/salmon colored, CPK coloring
    public static final Atom Br = new Atom( "Br", 114, 2.96, 79.904, new Color( 190, 30, 20 ) ); // brown
    public static final Atom C = new Atom( "C", 77, 2.55, 12.0107, new Color( 178, 178, 178 ) );
    public static final Atom Cl = new Atom( "Cl", 100, 3.16, 35.4527, new Color( 153, 242, 57 ) );
    public static final Atom F = new Atom( "F", 72, 3.98, 18.9984032, new Color( 247, 255, 74 ) );
    public static final Atom H = new Atom( "H", 37, 2.20, 1.00794, Color.WHITE );
    public static final Atom I = new Atom( "I", 133, 2.66, 126.90447, new Color( 0x940094 ) ); // dark violet, CPK coloring
    public static final Atom N = new Atom( "N", 75, 3.04, 14.00674, Color.BLUE );
    public static final Atom O = new Atom( "O", 73, 3.44, 15.9994, new Color( 255, 85, 0 ) );
    public static final Atom P = new Atom( "P", 110, 2.19, 30.973762, new Color( 255, 128, 0 ) );
    public static final Atom S = new Atom( "S", 103, 2.58, 32.066, new Color( 212, 181, 59 ) );
    public static final Atom Si = new Atom( "Si", 118, 1.90, 28.0855, new Color( 240, 200, 160 ) ); // tan, Jmol coloring listed from https://secure.wikimedia.org/wikipedia/en/wiki/CPK_coloring

}
