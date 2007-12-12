package edu.colorado.phet.statesofmatter.model.particle;

import junit.framework.TestCase;

import java.util.ArrayList;

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
