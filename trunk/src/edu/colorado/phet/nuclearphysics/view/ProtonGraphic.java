/**
 * Class: ProtonGraphic
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.nuclearphysics.model.NuclearParticle;

import java.awt.*;

public class ProtonGraphic extends ParticleGraphic {

    private static Color color = Color.red;

    public ProtonGraphic() {
        super( color );
    }

    public ProtonGraphic( NuclearParticle particle ) {
        super( particle, ProtonGraphic.color );
    }
}
