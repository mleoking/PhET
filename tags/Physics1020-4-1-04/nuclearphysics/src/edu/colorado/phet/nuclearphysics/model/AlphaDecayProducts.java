/**
 * Class: AlphaDecayProducts
 * Class: edu.colorado.phet.nuclearphysics.model
 * User: Ron LeMaster
 * Date: Feb 28, 2004
 * Time: 5:23:16 AM
 */
package edu.colorado.phet.nuclearphysics.model;

public class AlphaDecayProducts {
    private Nucleus parent;
    private Nucleus daughter;
    private AlphaParticle alphaParticle;

    public AlphaDecayProducts( Nucleus parent, AlphaParticle alphaParticle ) {
        this.parent = parent;
        this.daughter = new Thorium143( parent.getLocation() );
        this.alphaParticle = alphaParticle;
        this.alphaParticle.setNucleus( daughter );
        this.alphaParticle.setEscaped( true );
        alphaParticle.setPotential( parent.getPotentialProfile().getHillY( parent.getPotentialProfile().getAlphaDecayX() ) );
    }

    public Nucleus getParent() {
        return parent;
    }

    public Nucleus getDaughter() {
        return daughter;
    }

    public AlphaParticle getAlphaParticle() {
        return alphaParticle;
    }
}
