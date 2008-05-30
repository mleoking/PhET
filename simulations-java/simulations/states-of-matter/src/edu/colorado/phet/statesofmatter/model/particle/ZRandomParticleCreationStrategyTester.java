package edu.colorado.phet.statesofmatter.model.particle;

import java.util.ArrayList;

import junit.framework.TestCase;

public class ZRandomParticleCreationStrategyTester extends TestCase {
    private volatile ParticleCreationStrategy strategy;

    public void setUp() {
        this.strategy = new RandomParticleCreationStrategy(1.0, 1.0);
    }

    public void testNewParticleNonNull() {
        assertNotNull(strategy.createParticle());
    }

    public void testCanCreateList() {
        assertEquals(1, strategy.createParticles(new ArrayList(), 1));
    }
}
