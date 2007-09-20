package edu.colorado.phet.statesofmatter.model.engine.gravity;

import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;

import java.util.Iterator;
import java.util.List;

public class GravityPotentialMeasurer {
    private final List particles;
    private final double floor;
    private final double g;

    public GravityPotentialMeasurer(List particles, double floor, double g) {
        this.particles = particles;
        this.floor     = floor;
        this.g         = g;
    }

    public double measure() {
        double potential = 0.0;

        for (Iterator iterator = particles.iterator(); iterator.hasNext();) {
            StatesOfMatterParticle particle = (StatesOfMatterParticle)iterator.next();

            potential += (particle.getY() - floor) * g * particle.getMass();
        }

        return potential;
    }
}
