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

        // DEBUG!!! the + 20
        double scale = ( daughter.getPotentialProfile().getAlphaDecayX() - 20 )
                       / parent.getPotentialProfile().getAlphaDecayX();
        double dx = alphaParticle.getLocation().getX() - parent.getLocation().getX();
        double dy = alphaParticle.getLocation().getY() - parent.getLocation().getY();
        this.alphaParticle.setLocation( dx * scale + parent.getLocation().getX(),
                                        dy * scale + parent.getLocation().getY() );
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
