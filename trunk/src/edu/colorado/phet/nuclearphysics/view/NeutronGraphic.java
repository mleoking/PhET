/**
 * Class: NeutronGraphic
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.nuclearphysics.model.Particle;

import java.awt.*;

public class NeutronGraphic extends ParticleGraphic {

    private static Color color = Color.gray;

    public NeutronGraphic( Particle particle) {
        super( particle, NeutronGraphic.color );
    }
}
