// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.model;

/**
 * Data structure for describing atom counts.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AtomCount {

    private final Atom atom;
    private int reactantsCount, productsCount;

    public AtomCount( Atom atom, int reactantsCount, int productsCount ) {
        this.atom = atom;
        this.reactantsCount = reactantsCount;
        this.productsCount = productsCount;
    }

    public Atom getAtom() {
        return atom;
    }

    public int getReactantsCount() {
        return reactantsCount;
    }

    public void setReactantsCount( int reactantsCount ) {
        this.reactantsCount = reactantsCount;
    }

    public int getProductsCount() {
        return productsCount;
    }

    public void setProductsCount( int productsCount ) {
        this.productsCount = productsCount;
    }
}
