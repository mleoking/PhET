package edu.colorado.phet.statesofmatter.model.engine.energy;

import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ZKineticEnergyAdjusterTester extends TestCase {
    private static final StatesOfMatterParticle P1 = new StatesOfMatterParticle(0, 2, 3, 4);
    private static final StatesOfMatterParticle P2 = new StatesOfMatterParticle(4, 3, 2, 1);

    private volatile KineticEnergyAdjuster adjuster;
    private volatile List particles;

    public void setUp() {
        StatesOfMatterParticle[] particlesList = new StatesOfMatterParticle[]{(StatesOfMatterParticle)P1.clone(), (StatesOfMatterParticle)P2.clone()};

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
            StatesOfMatterParticle p = (StatesOfMatterParticle)particles.get(i);

            p.setVx(0);
            p.setVy(0);
        }

        performTest(10.0);
    }

    public void testAdjustment() {
        performTest(1.9);
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
