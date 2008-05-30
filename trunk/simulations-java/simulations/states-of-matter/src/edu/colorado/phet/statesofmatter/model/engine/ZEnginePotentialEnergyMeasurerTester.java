package edu.colorado.phet.statesofmatter.model.engine;

import java.util.Arrays;
import java.util.Iterator;

import junit.framework.TestCase;
import edu.colorado.phet.statesofmatter.model.engine.gravity.GravityPotentialMeasurer;
import edu.colorado.phet.statesofmatter.model.engine.lj.LJPotentialEnergyMeasurer;
import edu.colorado.phet.statesofmatter.model.engine.lj.LennardJonesPotential;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;

public class ZEnginePotentialEnergyMeasurerTester extends TestCase {
    private static final double FLOOR = 0.0;

    private volatile EnginePotentialEnergyMeasurer m;

    public void prepare(StatesOfMatterParticle[] particles, double g, LennardJonesPotential ljp) {
        m = new EnginePotentialEnergyMeasurer(Arrays.asList(particles), FLOOR, g, ljp);
    }

    public void setUp() {
        prepare(new StatesOfMatterParticle[]{}, -1.0, LennardJonesPotential.TEST);
    }

    public void testMeasuresGravityPotential() {
        assertEquals(1, count(GravityPotentialMeasurer.class));
    }

    public void testMeasuresLJPotential() {
        assertEquals(1, count(LJPotentialEnergyMeasurer.class));
    }

    public void testNoPotentialWithNoGravityAndOneParticle() {
        prepare(new StatesOfMatterParticle[]{new StatesOfMatterParticle(1, 1, 1, 1)}, 0.0, LennardJonesPotential.TEST);

        assertEquals(0.0, m.measure(), 0.000001);
    }

    public void testPositivePotentialWithGravityAndOneParticleAboveFloor() {
        prepare(new StatesOfMatterParticle[]{new StatesOfMatterParticle(1, FLOOR - 1, 1, 1)}, -1.0, LennardJonesPotential.TEST);

        assertTrue(m.measure() > 0.0);
    }

    public void testNegativePotentialWithGravityAndOneParticleBelowFloor() {
        prepare(new StatesOfMatterParticle[]{new StatesOfMatterParticle(1, FLOOR + 1, 1, 1)}, -1.0, LennardJonesPotential.TEST);

        assertTrue(m.measure() < 0.0);
    }

    public void testZeroPotentialWithGravityAndOneParticleAtFloor() {
        prepare(new StatesOfMatterParticle[]{new StatesOfMatterParticle(1, FLOOR, 1, 1)}, -1.0, LennardJonesPotential.TEST);

        assertEquals(0.0, m.measure(), 0.0);
    }

    public void testPositivePotentialEnergyWithTwoParticlesMuchCloserThanRMinAndNoGravity() {
        prepare(new StatesOfMatterParticle[]{new StatesOfMatterParticle(1, 1, 1, 1), new StatesOfMatterParticle(1 + 0.5 * LennardJonesPotential.TEST.getRMin(), 1, 1, 1)}, 0, LennardJonesPotential.TEST);

        assertTrue(m.measure() > 0.0);
    }

    public void testNegativePotentialEnergyWithTwoParticlesFartherThanRMinAndNoGravity() {
        prepare(new StatesOfMatterParticle[]{new StatesOfMatterParticle(1, 1, 1, 1), new StatesOfMatterParticle(1 + 1.5 * LennardJonesPotential.TEST.getRMin(), 1, 1, 1)}, 0, LennardJonesPotential.TEST);

        assertTrue(m.measure() < 0.0);
    }

    public void testMinusEpsilonPotentialEnergyWithTwoParticlesSeparatedByRMinAndNoGravity() {
        prepare(new StatesOfMatterParticle[]{new StatesOfMatterParticle(1, 1, 1, 1), new StatesOfMatterParticle(1 + LennardJonesPotential.TEST.getRMin(), 1, 1, 1)}, 0, LennardJonesPotential.TEST);

        assertEquals(-LennardJonesPotential.TEST.getEpsilon(), m.measure(), 0.000001);
    }
    
    private int count(Class theClass) {
        int count = 0;

        for (Iterator iterator = m.getMeasurables().iterator(); iterator.hasNext();) {
            Measurable measurable = (Measurable)iterator.next();

            if (theClass.isAssignableFrom(measurable.getClass())) {
                ++count;
            }
        }

        return count;
    }
}
