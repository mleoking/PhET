/**
 * Class: DecayProducts
 * Class: edu.colorado.phet.nuclearphysics.model
 * User: Ron LeMaster
 * Date: Feb 28, 2004
 * Time: 5:23:16 AM
 */
package edu.colorado.phet.nuclearphysics.model;

public class DecayProducts {
    private Nucleus n0;
    private Nucleus n1;
    private Nucleus n2;

    public DecayProducts( Nucleus n0, Nucleus n1, Nucleus n2 ) {
        this.n0 = n0;
        this.n1 = n1;
        this.n2 = n2;
    }

    public Nucleus getN0() {
        return n0;
    }

    public Nucleus getN1() {
        return n1;
    }

    public Nucleus getN2() {
        return n2;
    }
}
