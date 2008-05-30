package edu.colorado.phet.statesofmatter.model.engine.kinetic;

import java.util.List;

import edu.colorado.phet.statesofmatter.model.engine.Measurable;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;

public class KineticEnergyMeasurer implements Measurable {
    private final List particles;

    public KineticEnergyMeasurer(List particles) {
        this.particles = particles;
    }

    public double measure() {
        double ke = 0.0;

        for (int i = 0; i < particles.size(); i++) {
            StatesOfMatterParticle p = (StatesOfMatterParticle)particles.get(i);

            ke += p.getKineticEnergy();
        }

        return ke;
    }
}
