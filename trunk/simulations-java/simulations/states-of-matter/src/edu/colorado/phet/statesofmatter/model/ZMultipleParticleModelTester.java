package edu.colorado.phet.statesofmatter.model;

import java.awt.geom.Rectangle2D;
import java.util.List;

import junit.framework.TestCase;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.model.engine.kinetic.KineticEnergyMeasurer;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;

public class ZMultipleParticleModelTester extends TestCase {
    private volatile MultipleParticleModel model;
    private volatile ConstantDtClock clock;

    public void setUp() {
        this.clock = new ConstantDtClock(1, StatesOfMatterConstants.DELTA_T);
        this.model = new MultipleParticleModel(clock);
    }

    public void testThatNewlyConstructedModelContainsDefaultParticles() {
        List particleList = model.getParticles();

        assertTrue(particleList.size() > 0 && particleList.size() <= StatesOfMatterConstants.INITIAL_MAX_PARTICLE_COUNT);
    }

    public void testThatInitializeResetsParticleList() {
        model.reset();

        List particleList = model.getParticles();

        assertTrue(particleList.size() > 0 && particleList.size() <= StatesOfMatterConstants.INITIAL_MAX_PARTICLE_COUNT);
    }

    public void testThatParticleListIsUnmodifiable() {
        try {
            model.getParticles().add(StatesOfMatterAtom.TEST);

            fail();
        }
        catch (Exception e) {
        }
    }

    public void testThatParticlesMoveWhenClockRunning() {
        clock.start();

        waitForParticleToMove();
    }

    public void testThatNoParticleHasMoreKineticEnergyThanCap() {
        clock.start();

        for (int i = 0; i < 100; i++) {
            waitForParticleToMove();

            for (int j = 0; j < model.getNumMolecules(); j++) {
                StatesOfMatterAtom p = model.getParticle(j);

                assertTrue("Particle " + p + " has kinetic energy " + p.getKineticEnergy(), p.getKineticEnergy() <= StatesOfMatterConstants.PARTICLE_MAX_KE + 0.00001);
            }
        }
    }

    public void testInitialTotalEnergyIsDefault() {
        assertEquals(StatesOfMatterConstants.INITIAL_TOTAL_ENERGY_PER_PARTICLE * model.getNumMolecules(), model.getTotalEnergy(), 0.00001);
    }

    public void testKineticEnergyCorrectlyReported() {
        assertEquals(new KineticEnergyMeasurer(model.getParticles()).measure(), model.getKineticEnergy(), 0.00001);
    }

    public void testPotentialEnergyIsZero() {
        assertTrue(model.getPotentialEnergy() < 1.0E-12);
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

        StatesOfMatterAtom p = model.getParticle(0);

        StatesOfMatterAtom originalP = (StatesOfMatterAtom)p.clone();

        while (p.getPositionReference().equals(originalP.getPositionReference())) {
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
