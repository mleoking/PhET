//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.chemistry.model;

import java.awt.*;

/**
 * Represents a single atom of a certain element type.
 */
public class Atom {
    public final Element element;

    public Atom( Element element ) {
        this.element = element;
    }

    public static Atom createAtomFromSymbol( String symbol ) {
        return new Atom( Element.getElementBySymbol( symbol ) );
    }

    public Element getElement() {
        return element;
    }

    /*---------------------------------------------------------------------------*
    * convenience methods
    *----------------------------------------------------------------------------*/

    public String getSymbol() {
        return element.getSymbol();
    }

    public double getRadius() {
        return element.getRadius();
    }

    public double getElectronegativity() {
        return element.getElectronegativity();
    }

    public double getAtomicWeight() {
        return element.getAtomicWeight();
    }

    public Color getColor() {
        return element.getColor();
    }

    public double getDiameter() {
        return getRadius() * 2;
    }

    public boolean hasSameElement( Atom atom ) {
        return getElement().isSameElement( atom.getElement() );
    }

    public boolean isHydrogen() {
        return getElement().isSameElement( Element.H );
    }

    public boolean isCarbon() {
        return getElement().isSameElement( Element.C );
    }

    public boolean isOxygen() {
        return getElement().isSameElement( Element.O );
    }

    @Override
    public String toString() {
        return getSymbol();
    }
}
