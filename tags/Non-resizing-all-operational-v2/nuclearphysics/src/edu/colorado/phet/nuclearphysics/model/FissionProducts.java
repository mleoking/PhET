/**
 * Class: FissionProducts
 * Package: edu.colorado.phet.nuclearphysics.controller
 * Author: Another Guy
 * Date: Mar 19, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

public class FissionProducts {
    private Nucleus parent;
    private Nucleus daughter1;
    private Nucleus daughter2;
    private Neutron instigatingNeutron;
    private Neutron[] neutronProducts;

    public FissionProducts( Nucleus parent, Neutron neutron,
                            Nucleus daughter1, Nucleus daughter2,
                            Neutron[] neutronProducts ) {
        this.parent = parent;
        this.daughter1 = daughter1;
        this.daughter2 = daughter2;
        this.instigatingNeutron = neutron;
        this.neutronProducts = neutronProducts;
    }

    public Nucleus getParent() {
        return parent;
    }

    public Nucleus getDaughter1() {
        return daughter1;
    }

    public Nucleus getDaughter2() {
        return daughter2;
    }

    public Neutron getInstigatingNeutron() {
        return instigatingNeutron;
    }

    public Neutron[] getNeutronProducts() {
        return neutronProducts;
    }
}
