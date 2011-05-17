//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.chemistry.model;

import java.awt.*;

/**
 * Represents a single atom of a certain element type
 * <p/>
 */
public class Atom extends AbstractAtom {
    public final Element element;

    public Atom( Element element ) {
        this.element = element;
    }

    public static Atom createAtomFromSymbol( String symbol ) {
        return new Atom( Element.getElementBySymbol( symbol ) );
    }

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

    public Element getElement() {
        return element;
    }

    public double getDiameter() {
        return getRadius() * 2;
    }
}
