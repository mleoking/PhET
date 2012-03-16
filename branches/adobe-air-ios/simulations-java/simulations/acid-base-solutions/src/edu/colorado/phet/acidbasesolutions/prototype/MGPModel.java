// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.prototype;

/**
 * Container that holds the various model elements for the Magnifying Glass prototype.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class MGPModel {
    
    private final Beaker beaker;
    private final MagnifyingGlass magnifyingGlass;
    private final WeakAcid solution;
    
    public MGPModel() {
        beaker = new Beaker(); 
        magnifyingGlass = new MagnifyingGlass();
        solution = new WeakAcid();
    }
    
    public Beaker getBeaker() {
        return beaker;
    }
    
    public MagnifyingGlass getMagnifyingGlass() {
        return magnifyingGlass;
    }
    
    public WeakAcid getSolution() {
        return solution;
    }

}
