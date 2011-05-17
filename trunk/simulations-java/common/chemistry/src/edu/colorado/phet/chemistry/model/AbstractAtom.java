//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.chemistry.model;

/**
 * Implements methods that all Atomic instances should have as the same. Would combine into a Scala trait if possible.
 */
public abstract class AbstractAtom implements Atomic {

    public boolean isSameTypeOfAtom( Atomic atom ) {
        return atom.getSymbol().equals( this.getSymbol() );
    }

    public boolean isHydrogen() {
        return isSameTypeOfAtom( Atom.H );
    }

    public boolean isCarbon() {
        return isSameTypeOfAtom( Atom.C );
    }

    public boolean isOxygen() {
        return isSameTypeOfAtom( Atom.O );
    }

    @Override
    public String toString() {
        return getSymbol();
    }
}
