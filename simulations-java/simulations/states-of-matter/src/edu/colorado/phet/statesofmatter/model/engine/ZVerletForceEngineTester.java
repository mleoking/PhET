package edu.colorado.phet.statesofmatter.model.engine;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.model.particle.NonOverlappingParticleCreationStrategy;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;

public class ZVerletForceEngineTester extends TestCase {
    private volatile ForceEngine engine;
    private List particles;
    private EngineConfig descriptor = EngineConfig.TEST;
    private ForceComputation computation;

    public void setUp() {
        createParticles(StatesOfMatterConstants.INITIAL_MAX_PARTICLE_COUNT);

        performComputation(descriptor);
    }

    public void testNoMotionWhenNoGravityAndParticleCountIsOne() {
        EngineConfig d = (EngineConfig)descriptor.clone();

        d.gravity = 0.0;

        createParticles(1);

        StatesOfMatterParticle originalP = (StatesOfMatterParticle)((StatesOfMatterParticle)particles.get(0)).clone();

        performComputation(d);

        assertEquals(computation.getNewPositions()[0], originalP.getPosition());
    }

   public void testNoInfinitesWithGravityAndMultipleParticles() {
        for (int i = 0; i < particles.size(); i++) {
            verifyFinite(computation.getNewPositions()[i].getX());
            verifyFinite(computation.getNewPositions()[i].getY());

            verifyFinite(computation.getNewVelocities()[i].getX());
            verifyFinite(computation.getNewVelocities()[i].getY());

            verifyFinite(computation.getNewAccelerations()[i].getX());
            verifyFinite(computation.getNewAccelerations()[i].getY());
        }
    }

    public void testDownardMotionWhenSmallGravityAndParticleCountIsOne() {
        EngineConfig d = (EngineConfig)descriptor.clone();

        d.gravity = -1;

        createParticles(1);

        StatesOfMatterParticle originalP = (StatesOfMatterParticle)((StatesOfMatterParticle)particles.get(0)).clone();

        performAndApplyComputation(d, 2);

        Point2D oldPosition = originalP.getPosition();
        Point2D newPosition = computation.getNewPositions()[0];

        assertTrue("particle should be falling: new y = " + newPosition.getY() + ", old y = " + oldPosition.getY(), newPosition.getY() > oldPosition.getY());
    }

    private void verifyFinite(double x) {
        assertFalse(Double.isInfinite(x) || Double.isNaN(x));
    }

    private void performComputation(EngineConfig descriptor) {
        computation = engine.compute(particles, descriptor);
    }

    private void performAndApplyComputation(EngineConfig descriptor, int count) {
        for (int i = 0; i < count; i++) {
            performComputation(descriptor);

            computation.apply(particles);
        }
    }

    private void createParticles(int particleCount) {
        particles = new ArrayList();
        engine    = new VerletForceEngine();

        NonOverlappingParticleCreationStrategy strategy = new NonOverlappingParticleCreationStrategy(StatesOfMatterConstants.CONTAINER_BOUNDS, StatesOfMatterConstants.PARTICLE_MASS, StatesOfMatterConstants.PARTICLE_RADIUS, StatesOfMatterConstants.PARTICLE_CREATION_CUSHION, particles);

        strategy.createParticles(particles, particleCount);
    }
}
