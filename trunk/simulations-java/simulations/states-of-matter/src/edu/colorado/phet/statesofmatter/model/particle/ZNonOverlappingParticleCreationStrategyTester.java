package edu.colorado.phet.statesofmatter.model.particle;

import edu.colorado.phet.statesofmatter.StatesOfMatterConfig;

import java.util.ArrayList;
import java.util.List;

public class ZNonOverlappingParticleCreationStrategyTester extends ZBoundedParticleCreationStrategyTester {
    private static final double CUSHION = 0.05;

    public void setUp() {
        strategy = new NonOverlappingParticleCreationStrategy(StatesOfMatterConfig.CONTAINER_BOUNDS, PARTICLE_RADIUS, CUSHION);
    }

    public void testThatParticlesDoNotOverlap() {
        List particles = new ArrayList();

        for (int i = 0; i < NUM_PARTICLES_TO_TEST; i++) {
            StatesOfMatterParticle p1 = strategy.createNewParticle(particles, PARTICLE_RADIUS, 1.0);

            for (int j = 0; j < particles.size(); j++) {
                StatesOfMatterParticle p2 = (StatesOfMatterParticle)particles.get(j);

                double deltaX = p1.getX() - p2.getX();
                double deltaY = p1.getY() - p2.getY();

                double dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

                assertTrue("Particles " + p1 + " and " + p2 + " overlap by " + (dist - PARTICLE_RADIUS) + "cm", dist >= 2 * PARTICLE_RADIUS + CUSHION - 0.0001);
            }

            particles.add(p1);
        }
    }
}
