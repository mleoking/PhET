package edu.colorado.phet.statesofmatter.model.particle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;

public class ZNonOverlappingParticleCreationStrategyTester extends ZBoundedParticleCreationStrategyTester {
    protected double cushion = 0.05;
    protected List particles;

    public void setUp() {
        particles = new ArrayList();
        strategy  = new NonOverlappingParticleCreationStrategy(StatesOfMatterConstants.CONTAINER_BOUNDS, PARTICLE_MASS, PARTICLE_RADIUS, cushion, particles);
    }

    public void testThatParticlesDoNotOverlap() {
        strategy.createParticles(particles, NUM_PARTICLES_TO_TEST);

        for (Iterator iterator = particles.iterator(); iterator.hasNext();) {
            StatesOfMatterAtom p1 = (StatesOfMatterAtom)iterator.next();

            for (Iterator iterator2 = particles.iterator(); iterator2.hasNext();) {
                StatesOfMatterAtom p2 = (StatesOfMatterAtom)iterator2.next();

                if (p1 != p2) {
                    double deltaX = p1.getX() - p2.getX();
                    double deltaY = p1.getY() - p2.getY();

                    double dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

                    assertTrue("Particles " + p1 + " and " + p2 + " overlap by " + (dist - PARTICLE_RADIUS) + "cm", dist >= 2 * PARTICLE_RADIUS + cushion - 0.0001);
                }

            }
        }
    }
}
