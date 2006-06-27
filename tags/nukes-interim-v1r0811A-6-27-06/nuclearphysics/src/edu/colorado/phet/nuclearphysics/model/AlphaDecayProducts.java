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
    private ProfileableNucleus daughter;
    private AlphaParticle alphaParticle;

    public AlphaDecayProducts( ProfileableNucleus parent, AlphaParticle alphaParticle ) {
        this.parent = parent;
        this.daughter = new Lead207( parent.getPosition() );
        this.alphaParticle = alphaParticle;
        this.alphaParticle.setNucleus( daughter );
        this.alphaParticle.setEscaped( true, parent.getEnergyProfile().getTotalEnergy() );
        alphaParticle.setPotential( parent.getEnergyProfile().getHillY( parent.getEnergyProfile().getAlphaDecayX() ) );
    }

    public Nucleus getParent() {
        return parent;
    }

    public ProfileableNucleus getDaughter() {
        return daughter;
    }

    public AlphaParticle getAlphaParticle() {
        return alphaParticle;
    }
}
