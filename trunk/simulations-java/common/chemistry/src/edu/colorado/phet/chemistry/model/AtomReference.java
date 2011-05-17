//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.chemistry.model;

import java.awt.*;

/**
 * Represents a single atom, which has a certain state
 * <p/>
 * TODO: consider renaming to Atom
 */
public class AtomReference extends AbstractAtom {
    public final Atom atom;

    public AtomReference( Atom atom ) {
        this.atom = atom;
    }

    public String getSymbol() {
        return atom.getSymbol();
    }

    public double getRadius() {
        return atom.getRadius();
    }

    public double getElectronegativity() {
        return atom.getElectronegativity();
    }

    public double getAtomicWeight() {
        return atom.getAtomicWeight();
    }

    public Color getColor() {
        return atom.getColor();
    }
}
