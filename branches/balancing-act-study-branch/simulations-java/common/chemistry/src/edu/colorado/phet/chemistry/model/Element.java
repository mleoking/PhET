// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.chemistry.model;

import java.awt.*;

/**
 * Class for actual element properties (symbol, radius, etc.).
 * Inner classes for each specific element.
 * <p/>
 * Reference for atom radii:
 * Chemistry: The Molecular Nature of Matter and Change, 5th Edition, Silberberg.
 * <p/>
 * <p/>
 * Due to only static instances being available, reference equality checks should be suitable
 * for verifying element equality
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @author Jonathan Olson
 */
public class Element {

    private final String symbol;
    private final double radius; // picometers (for a quick chart, see http://en.wikipedia.org/wiki/Atomic_radii_of_the_elements_(data_page) )
    private final double electronegativity; // dimensionless, see https://secure.wikimedia.org/wikipedia/en/wiki/Electronegativity
    private final double atomicWeight; // in atomic mass units (u). from http://www.webelements.com/periodicity/atomic_weight/
    private final Color color; // color used in visual representations

    private Element( String symbol, double radius, double electronegativity, double atomicWeight, Color color ) {
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
    * convenience methods
    *----------------------------------------------------------------------------*/

    public boolean isSameElement( Element element ) {
        // like equals, but is more type-safe
        return element.getSymbol().equals( this.getSymbol() );
    }

    public boolean isHydrogen() {
        return isSameElement( Element.H );
    }

    public boolean isCarbon() {
        return isSameElement( Element.C );
    }

    public boolean isOxygen() {
        return isSameElement( Element.O );
    }

    @Override
    public String toString() {
        return getSymbol();
    }

    /*---------------------------------------------------------------------------*
    * elements
    *----------------------------------------------------------------------------*/

    public static final Element B = new Element( "B", 85, 2.04, 10.811, new Color( 255, 170, 119 ) ); // peach/salmon colored, CPK coloring
    public static final Element Be = new Element( "Be", 105, 1.57, 9.012182, new Color( 0xc2ff00 ) ); // beryllium
    public static final Element Br = new Element( "Br", 114, 2.96, 79.904, new Color( 190, 30, 20 ) ); // brown
    public static final Element C = new Element( "C", 77, 2.55, 12.0107, new Color( 178, 178, 178 ) );
    public static final Element Cl = new Element( "Cl", 100, 3.16, 35.4527, new Color( 153, 242, 57 ) );
    public static final Element F = new Element( "F", 72, 3.98, 18.9984032, new Color( 247, 255, 74 ) );
    public static final Element H = new Element( "H", 37, 2.20, 1.00794, Color.WHITE );
    public static final Element I = new Element( "I", 133, 2.66, 126.90447, new Color( 0x940094 ) ); // dark violet, CPK coloring
    public static final Element N = new Element( "N", 75, 3.04, 14.00674, Color.BLUE );
    public static final Element O = new Element( "O", 73, 3.44, 15.9994, new Color( 255, 85, 0 ) );
    public static final Element P = new Element( "P", 110, 2.19, 30.973762, new Color( 255, 128, 0 ) );
    public static final Element S = new Element( "S", 103, 2.58, 32.066, new Color( 212, 181, 59 ) );
    public static final Element Si = new Element( "Si", 118, 1.90, 28.0855, new Color( 240, 200, 160 ) ); // tan, Jmol coloring listed from https://secure.wikimedia.org/wikipedia/en/wiki/CPK_coloring
    public static final Element Sn = new Element( "Sn", 145, 1.96, 118.710, new Color( 0x668080 ) ); // tin
    public static final Element Xe = new Element( "Xe", 108, 2.60, 131.293, new Color( 0x429eb0 ) ); // radius is based on calculated (not empirical) data

    private static final Element[] ELEMENTS = new Element[] {
            B, Be, Br, C, Cl, F, H, I, N, O, P, S, Si, Xe
    };

    public static Element getElementBySymbol( String symbol ) {
        // TODO: if we really need this for efficiency in BAM startup (profile!!), consider testing hashmap
        for ( Element element : ELEMENTS ) {
            if ( element.getSymbol().equals( symbol ) ) {
                return element;
            }
        }
        throw new RuntimeException( "Element not found with symbol: " + symbol );
    }
}
