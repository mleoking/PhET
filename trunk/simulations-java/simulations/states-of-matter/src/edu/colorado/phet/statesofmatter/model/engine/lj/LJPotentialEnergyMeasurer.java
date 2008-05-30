package edu.colorado.phet.statesofmatter.model.engine.lj;

import java.util.List;

import edu.colorado.phet.statesofmatter.model.engine.Measurable;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;

public class LJPotentialEnergyMeasurer implements Measurable {
    private final List particles;
    private final LennardJonesPotential ljp;

    public LJPotentialEnergyMeasurer(List particles, LennardJonesPotential potential) {
        this.particles = particles;
        this.ljp       = potential;
    }

    private StatesOfMatterParticle p(int i) {
        return (StatesOfMatterParticle)particles.get(i);
    }

    public double measure() {
        double potential = 0.0;

        for (int i = 0; i < particles.size() - 1; i++) {
            for (int j = i + 1; j < particles.size(); j++) {
                StatesOfMatterParticle p1 = p(i);
                StatesOfMatterParticle p2 = p(j);

                double dx = p2.getX() - p1.getX();
                double dy = p2.getY() - p1.getY();

                potential += ljp.evaluate(dx, dy);
            }
        }

        return potential;
    }
}
