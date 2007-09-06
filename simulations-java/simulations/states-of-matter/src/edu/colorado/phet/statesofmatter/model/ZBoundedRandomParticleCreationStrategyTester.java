package edu.colorado.phet.statesofmatter.model;

import edu.colorado.phet.statesofmatter.StatesOfMatterConfig;
import junit.framework.TestCase;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class ZBoundedRandomParticleCreationStrategyTester extends TestCase {
    private static final int NUM_PARTICLES_TO_TEST = 1000;

    private volatile ParticleCreationStrategy strategy;

    public void setUp() {
         this.strategy = new BoundedRandomParticleCreationStrategy(StatesOfMatterConfig.CONTAINER_BOUNDS);
    }

    public void testThatNoParticleIsOutsideBounds() {
        Rectangle2D.Double bounds = StatesOfMatterConfig.CONTAINER_BOUNDS;

        for (int i = 0; i < NUM_PARTICLES_TO_TEST; i++) {
            StatesOfMatterParticle particle = strategy.createNewParticle(new ArrayList());

            assertTrue(bounds.contains(particle.getX(), particle.getY()));
        }
    }
}
