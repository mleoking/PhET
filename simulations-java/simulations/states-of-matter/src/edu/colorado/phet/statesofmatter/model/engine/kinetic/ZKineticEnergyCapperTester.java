package edu.colorado.phet.statesofmatter.model.engine.kinetic;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;

public class ZKineticEnergyCapperTester extends TestCase {
    private List particles;

    public void testCappingHasNoEffectOnParticlesWithLessKineticEnergyThanMax() {
        StatesOfMatterParticle p = new StatesOfMatterParticle(1, 1, 1, 1);

        p.setKineticEnergy(7);

        cap(new StatesOfMatterParticle[]{p}, 8);

        assertEquals(7.0, highestKE(), 0.00001);
    }

    public void testCappingOnParticleWithMoreKineticEnergyThanMax() {
        StatesOfMatterParticle p = new StatesOfMatterParticle(1, 1, 1, 1);

        p.setKineticEnergy(100);

        cap(new StatesOfMatterParticle[]{p}, 7);

        assertEquals(7.0, highestKE(), 0.00001);
    }

    private void cap(StatesOfMatterParticle[] particles, double max) {
        this.particles = Arrays.asList(particles);

        new KineticEnergyCapper(this.particles).cap(max);
    }

    private double highestKE() {
        double highest = 0.0;

        for (Iterator iterator = particles.iterator(); iterator.hasNext();) {
            StatesOfMatterParticle particle = (StatesOfMatterParticle)iterator.next();

            if (particle.getKineticEnergy() > highest) {
                highest = particle.getKineticEnergy();
            }
        }

        return highest;
    }
}
