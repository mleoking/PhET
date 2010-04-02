package edu.colorado.phet.acidbasesolutions.prototype;

/**
 * Container that holds the various model elements for the Magnifying Glass View prototype.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ProtoModel {
    
    private final Beaker beaker;
    private final MagnifyingGlass magnifyingGlass;
    
    public ProtoModel() {
        beaker = new Beaker(); 
        magnifyingGlass = new MagnifyingGlass();
    }
    
    public Beaker getBeaker() {
        return beaker;
    }
    
    public MagnifyingGlass getMagnifyingGlass() {
        return magnifyingGlass;
    }

}
