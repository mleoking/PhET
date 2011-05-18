// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.model;

import edu.colorado.phet.chemistry.model.Element;

/**
 * Data structure for describing how many times an atom appears in an equation.
 * There are separate counts for the left-hand (reactants) and right-hand (products)
 * sides of the equation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AtomCount {

    private final Element element;
    private int reactantsCount, productsCount;

    public AtomCount( Element element, int reactantsCount, int productsCount ) {
        this.element = element;
        this.reactantsCount = reactantsCount;
        this.productsCount = productsCount;
    }

    public Element getElement() {
        return element;
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
