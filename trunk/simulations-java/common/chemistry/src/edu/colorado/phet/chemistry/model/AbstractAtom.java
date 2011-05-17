//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.chemistry.model;

/**
 * Implements methods that all Atomic instances should have as the same. Would combine into a Scala trait if possible.
 */
public abstract class AbstractAtom implements Atomic {

    public boolean isSameElement( Atomic atom ) {
        return atom.getSymbol().equals( this.getSymbol() );
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
}
