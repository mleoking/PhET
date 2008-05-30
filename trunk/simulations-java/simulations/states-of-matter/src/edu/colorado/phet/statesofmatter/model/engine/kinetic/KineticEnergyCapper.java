package edu.colorado.phet.statesofmatter.model.engine.kinetic;

import java.util.Iterator;
import java.util.List;

import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;

public class KineticEnergyCapper {
    private final List particles;

    public KineticEnergyCapper(List particles) {
        this.particles = particles;
    }

    public void cap(double max) {
        for (Iterator iterator = particles.iterator(); iterator.hasNext();) {
            StatesOfMatterParticle particle = (StatesOfMatterParticle)iterator.next();

            if (particle.getKineticEnergy() > max) {
                particle.setKineticEnergy(max);
            }
        }
    }
}
