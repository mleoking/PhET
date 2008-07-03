package edu.colorado.phet.statesofmatter.model.engine.lj;

import java.util.List;

import edu.colorado.phet.statesofmatter.model.engine.Measurable;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;

public class LJPotentialEnergyMeasurer implements Measurable {
    private final List particles;
    private final LennardJonesPotential ljp;

    public LJPotentialEnergyMeasurer(List particles, LennardJonesPotential potential) {
        this.particles = particles;
        this.ljp       = potential;
    }

    private StatesOfMatterAtom p(int i) {
        return (StatesOfMatterAtom)particles.get(i);
    }

    public double measure() {
        double potential = 0.0;

        for (int i = 0; i < particles.size() - 1; i++) {
            for (int j = i + 1; j < particles.size(); j++) {
                StatesOfMatterAtom p1 = p(i);
                StatesOfMatterAtom p2 = p(j);

                double dx = p2.getX() - p1.getX();
                double dy = p2.getY() - p1.getY();

                potential += ljp.evaluate(dx, dy);
            }
        }

        return potential;
    }
}
