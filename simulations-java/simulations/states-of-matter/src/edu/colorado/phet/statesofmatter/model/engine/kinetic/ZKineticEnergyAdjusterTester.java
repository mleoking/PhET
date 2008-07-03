package edu.colorado.phet.statesofmatter.model.engine.kinetic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;

public class ZKineticEnergyAdjusterTester extends TestCase {
    private static final StatesOfMatterAtom P1 = new StatesOfMatterAtom(0, 2, 3, 4);
    private static final StatesOfMatterAtom P2 = new StatesOfMatterAtom(4, 3, 2, 1);

    private volatile KineticEnergyAdjuster adjuster;
    private volatile List particles;

    public void setUp() {
        StatesOfMatterAtom[] particlesList = new StatesOfMatterAtom[]{(StatesOfMatterAtom)P1.clone(), (StatesOfMatterAtom)P2.clone()};

        for (int i = 0; i < particlesList.length; i++) {
            particlesList[i].setVx(Math.random());
            particlesList[i].setVy(Math.random());
        }

        this.adjuster  = new KineticEnergyAdjuster();
        this.particles = Arrays.asList(particlesList);
    }

    public void testAdjustmentFromNonZeroToZero() {
        performTest(0.0);
    }

    public void testAdjustmentFromNonZeroToFractionOfInitial() {
        double target = new KineticEnergyMeasurer(particles).measure() / 30.0;

        performTest(target);
    }

    public void testAdjustmentWithNoInitialKineticEnergy() {
        for (int i = 0; i < particles.size(); i++) {
            StatesOfMatterAtom p = (StatesOfMatterAtom)particles.get(i);

            p.setVx(0);
            p.setVy(0);
        }

        performTest(10.0);
    }

    public void testAdjustment() {
        performTest(1.9);
    }

    public void testIllegalArgumentExceptionThrownWhenTargetEnergyNegative() {
        try {
            performTest(-0.00001);

            fail();
        }
        catch (IllegalArgumentException e) {
        }
    }

    public void testStressRandomCases() {
        particles = new ArrayList();

        for (int i = 0; i < 1000; i++) {
            StatesOfMatterAtom particle = new StatesOfMatterAtom(Math.random() * 2 - 1, Math.random() * 2 - 1, Math.random(), 1.0);

            particles.add(particle);

            particle.setVx(Math.random() * 40 - 20);
            particle.setVy(Math.random() * 40 - 20);
        }

        for (int i = 0; i < 100; i++) {
            double target = Math.random() * 10000;

            performTest(target);
        }
    }

    public void testManyRandomCases() {
        for (int i = 0; i < 100; i++) {
            double target = Math.random() * 100;

            performTest(target);
        }
    }

    public void testZeroSizeCaseDoesNotCauseNPE() {
        particles = new ArrayList();

        adjuster.adjust(particles, 1.333);
    }

    private void performTest(double target) {
        adjuster.adjust(particles, target);

        assertEquals(target, new KineticEnergyMeasurer(particles).measure(), 0.000001);
    }
}
