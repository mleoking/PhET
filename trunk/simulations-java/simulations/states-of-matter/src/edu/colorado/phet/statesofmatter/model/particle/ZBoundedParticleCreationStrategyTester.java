package edu.colorado.phet.statesofmatter.model.particle;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;

public class ZBoundedParticleCreationStrategyTester extends TestCase {
    protected static final int NUM_PARTICLES_TO_TEST = 300;

    protected static final double PARTICLE_RADIUS = 0.1;
    protected static final double PARTICLE_MASS = 1.0;

    protected volatile ParticleCreationStrategy strategy;


    public void setUp() {
         this.strategy = new BoundedParticleCreationStrategy(StatesOfMatterConstants.CONTAINER_BOUNDS, PARTICLE_RADIUS, PARTICLE_MASS);
    }

    public void testThatNoParticleCenterIsOutsideBounds() {
        Rectangle2D.Double bounds = StatesOfMatterConstants.CONTAINER_BOUNDS;

        Collection particles = new ArrayList();

        strategy.createParticles(particles, NUM_PARTICLES_TO_TEST);

        for (Iterator iterator = particles.iterator(); iterator.hasNext();) {
            StatesOfMatterAtom particle = (StatesOfMatterAtom)iterator.next();

            assertTrue(bounds.contains(particle.getX(), particle.getY()));
        }
    }

    public void testThatNoPartOfParticleIsOutsideBounds() {
        Rectangle2D.Double bounds = StatesOfMatterConstants.CONTAINER_BOUNDS;

        Rectangle2D.Double narrow = new Rectangle2D.Double(bounds.x + PARTICLE_RADIUS,
                                                           bounds.y + PARTICLE_RADIUS,
                                                           bounds.width  - 2 * PARTICLE_RADIUS,
                                                           bounds.height - 2 * PARTICLE_RADIUS);
        Collection particles = new ArrayList();

        strategy.createParticles(particles, NUM_PARTICLES_TO_TEST);

        for (Iterator iterator = particles.iterator(); iterator.hasNext();) {
            StatesOfMatterAtom particle = (StatesOfMatterAtom)iterator.next();

            assertTrue(narrow.contains(particle.getX(), particle.getY()));
        }
    }

    public void testCanCreateList() {
        Collection particles = new ArrayList();

        assertEquals(3, strategy.createParticles(particles, 3));

        assertTrue(particles.iterator().next() instanceof StatesOfMatterAtom);
    }

    public void testListCreationAddsToExistingList() {
        Collection particles = new ArrayList();

        strategy.createParticles(particles, 3);
        strategy.createParticles(particles, 3);

        assertEquals(6, particles.size());
    }

    public void testListCreationReturnsAppropriateParticleCount() {
        Collection particles = new ArrayList();

        strategy.createParticles(particles, 3);

        assertEquals(2, strategy.createParticles(particles, 2));
    }
}
