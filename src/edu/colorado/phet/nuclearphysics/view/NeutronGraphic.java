/**
 * Class: NeutronGraphic
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.nuclearphysics.model.NuclearParticle;

import java.awt.*;

public class NeutronGraphic extends ParticleGraphic {

    private static Color color = Color.gray;

    public NeutronGraphic() {
        super( color );
    }

    public NeutronGraphic( NuclearParticle particle ) {
        super( particle, NeutronGraphic.color );
    }
}
