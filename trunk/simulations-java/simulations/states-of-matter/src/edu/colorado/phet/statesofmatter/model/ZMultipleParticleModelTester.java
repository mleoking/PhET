package edu.colorado.phet.statesofmatter.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.statesofmatter.StatesOfMatterConfig;
import edu.colorado.phet.statesofmatter.model.engine.kinetic.KineticEnergyMeasurer;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;
import junit.framework.TestCase;

import java.awt.geom.Rectangle2D;
import java.util.List;

public class ZMultipleParticleModelTester extends TestCase {
    private volatile MultipleParticleModel model;
    private volatile ConstantDtClock clock;

    public void setUp() {
        this.clock = new ConstantDtClock(1, 1);
        this.model = new MultipleParticleModel(clock);
    }

    public void testThatNewlyConstructedModelContainsDefaultParticles() {
        List particleList = model.getParticles();

        assertEquals(StatesOfMatterConfig.INITIAL_PARTICLE_COUNT, particleList.size());
    }

    public void testThatInitializeResetsParticleList() {
        model.initialize();

        List particleList = model.getParticles();

        assertEquals(StatesOfMatterConfig.INITIAL_PARTICLE_COUNT, particleList.size());
    }

    public void testThatParticleListIsUnmodifiable() {
        try {
            model.getParticles().add(StatesOfMatterParticle.TEST);

            fail();
        }
        catch (Exception e) {
        }
    }

    public void testThatParticlesMoveWhenClockRunning() {
        clock.start();

        waitForParticleToMove();
    }

    public void testThatParticleContainerIsInitiallySetToDefaultBounds() {
        assertEquals(StatesOfMatterConfig.CONTAINER_BOUNDS.getBounds2D(), model.getParticleContainer().getShape().getBounds2D());
    }

    public void testThatParticlesDoNotLeaveContainerWhenClockRunning() {
        clock.start();

        for (int i = 0; i < 100; i++) {
            waitForParticleToMove();

            for (int j = 0; j < model.getNumParticles(); j++) {
                StatesOfMatterParticle p = model.getParticle(j);

                Rectangle2D particleContainer = model.getParticleContainer().getShape().getBounds2D();

                assertTrue("Particle " + p + " has left container " + particleContainer, particleContainer.contains(p.getPosition()));
            }
        }
    }

    public void testThatNoParticleHasMoreKineticEnergyThanCap() {
        clock.start();

        for (int i = 0; i < 100; i++) {
            waitForParticleToMove();

            for (int j = 0; j < model.getNumParticles(); j++) {
                StatesOfMatterParticle p = model.getParticle(j);

                assertTrue("Particle " + p + " has kinetic energy " + p.getKineticEnergy(), p.getKineticEnergy() <= StatesOfMatterConfig.PARTICLE_MAX_KE + 0.00001);
            }
        }
    }

    public void testInitialTotalEnergyIsDefault() {
        assertEquals(StatesOfMatterConfig.INITIAL_TOTAL_ENERGY, model.getTotalEnergy(), 0.00001);
    }

    public void testGetKineticEnergy() {
        assertEquals(new KineticEnergyMeasurer(model.getParticles()).measure(), model.getKineticEnergy(), 0.00001);
    }

    public void testGetPotentialEnergy() {
        assertTrue(model.getPotentialEnergy() > 0);
    }

    public void testGetTotalEnergy() {
        assertEquals(model.getKineticEnergy() + model.getPotentialEnergy(), model.getTotalEnergy(), 0.00001);
    }

    public void testThatTotalEnergyConserved() {
        double initialEnergy = model.getTotalEnergy();

        clock.setPaused(true);

        for (int i = 0; i < 100; i++) {
            waitForParticleToMove();

            double curEnergy = model.getKineticEnergy() + model.getPotentialEnergy();

            assertEquals("Energy not conserved at step " + i, initialEnergy, curEnergy, 0.00001);
        }
    }

    private void waitForParticleToMove() {
        long startTime = System.currentTimeMillis();

        StatesOfMatterParticle p = model.getParticle(0);

        StatesOfMatterParticle originalP = (StatesOfMatterParticle)p.clone();

        while (p.getPosition().equals(originalP.getPosition())) {
            if (clock.isPaused()) {
                clock.stepClockWhilePaused();
            }

            Thread.yield();

            if (System.currentTimeMillis() - startTime > 1000) {
                fail("The particle " + p + " has not moved within the timeout period.");
            }
        }
    }
}
