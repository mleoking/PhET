/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck;

import edu.colorado.phet.cck.elements.particles.BranchParticle;
import edu.colorado.phet.cck.elements.particles.ParticleSet;
import edu.colorado.phet.cck.elements.particles.ParticleSetGraphic;
import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Dec 6, 2003
 * Time: 9:34:30 PM
 * Copyright (c) Dec 6, 2003 by Sam Reid
 */
public class ParticleRenderer implements Graphic {
    ParticleSet particleSet;
    ParticleSetGraphic particleSetGraphic;

    public ParticleRenderer(ParticleSet particleSet, ParticleSetGraphic particleSetGraphic) {
        this.particleSet = particleSet;
        this.particleSetGraphic = particleSetGraphic;
    }

    public void paint(Graphics2D g) {

        for (int i = 0; i < particleSet.numParticles(); i++) {
            BranchParticle bp = particleSet.particleAt(i);
            double distFromEnd = bp.getDistanceFromClosestJunction();
            if (distFromEnd < .24) {
                particleSetGraphic.paint(g, bp);
            }
        }

    }
}
