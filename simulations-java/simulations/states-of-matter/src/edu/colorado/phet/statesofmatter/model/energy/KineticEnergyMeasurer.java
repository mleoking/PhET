package edu.colorado.phet.statesofmatter.model.energy;

import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;

import java.util.List;

public class KineticEnergyMeasurer {
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
