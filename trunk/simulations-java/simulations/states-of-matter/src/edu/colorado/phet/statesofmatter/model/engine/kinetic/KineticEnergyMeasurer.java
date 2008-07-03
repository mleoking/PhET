package edu.colorado.phet.statesofmatter.model.engine.kinetic;

import java.util.List;

import edu.colorado.phet.statesofmatter.model.engine.Measurable;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;

public class KineticEnergyMeasurer implements Measurable {
    private final List particles;

    public KineticEnergyMeasurer(List particles) {
        this.particles = particles;
    }

    public double measure() {
        double ke = 0.0;

        for (int i = 0; i < particles.size(); i++) {
            StatesOfMatterAtom p = (StatesOfMatterAtom)particles.get(i);

            ke += p.getKineticEnergy();
        }

        return ke;
    }
}
