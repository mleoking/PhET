package edu.colorado.phet.statesofmatter.model.particle;

import edu.colorado.phet.statesofmatter.StatesOfMatterConfig;
import junit.framework.TestCase;

import java.awt.geom.Rectangle2D;

public class ZBoundedParticleCreationStrategyTester extends TestCase {
    protected static final int NUM_PARTICLES_TO_TEST = 300;

    protected static final double PARTICLE_RADIUS = 0.1;
    protected static final double PARTICLE_MASS = 1.0;

    protected volatile ParticleCreationStrategy strategy;


    public void setUp() {
         this.strategy = new BoundedParticleCreationStrategy(StatesOfMatterConfig.CONTAINER_BOUNDS, PARTICLE_RADIUS, PARTICLE_MASS);
    }

    public void testThatNoParticleCenterIsOutsideBounds() {
        Rectangle2D.Double bounds = StatesOfMatterConfig.CONTAINER_BOUNDS;

        for (int i = 0; i < NUM_PARTICLES_TO_TEST; i++) {
            StatesOfMatterParticle particle = strategy.createParticle();

            assertTrue(bounds.contains(particle.getX(), particle.getY()));
        }
    }

    public void testThatNoPartOfParticleIsOutsideBounds() {
        Rectangle2D.Double bounds = StatesOfMatterConfig.CONTAINER_BOUNDS;

        Rectangle2D.Double narrow = new Rectangle2D.Double(bounds.x + PARTICLE_RADIUS,
                                                           bounds.y + PARTICLE_RADIUS,
                                                           bounds.width  - 2 * PARTICLE_RADIUS,
                                                           bounds.height - 2 * PARTICLE_RADIUS);

        for (int i = 0; i < NUM_PARTICLES_TO_TEST; i++) {
            StatesOfMatterParticle particle = strategy.createParticle();

            assertTrue(narrow.contains(particle.getX(), particle.getY()));
        }
    }

    public void testCanCreateList() {
        assertEquals(1, strategy.createParticles(1).size());
        
        assertTrue(strategy.createParticles(1).iterator().next() instanceof StatesOfMatterParticle);
    }
}
