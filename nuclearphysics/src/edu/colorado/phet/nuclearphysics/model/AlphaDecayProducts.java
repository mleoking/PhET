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
//        this.daughter = new Nucleus( parent.getLocation(),
//                                     parent.getNumProtons() - 2,
//                                     parent.getNumNeutrons() - 2 );
        this.daughter = new Thorium143( parent.getLocation() );
        this.alphaParticle = alphaParticle;
        this.alphaParticle.setNucleus( daughter );
        this.alphaParticle.setEscaped( true );

        double dx = parent.getPotentialProfile().getAlphaDecayX() - parent.getLocation().getX();
        dx *= alphaParticle.getLocation().getX() < parent.getLocation().getX() ? 1 : -1;
        double dy = parent.getPotentialProfile().getHillY( parent.getPotentialProfile().getAlphaDecayX() )
                    - parent.getLocation().getY();
        this.alphaParticle.setLocation( dx + parent.getLocation().getX(),
                                        dy + parent.getLocation().getY() );
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
